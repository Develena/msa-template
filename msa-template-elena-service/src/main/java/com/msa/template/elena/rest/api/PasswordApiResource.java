/*
 * UserResource2.java
 *
 * 15/06/21, 02:00 PM
 *
 * Copyright (c) 2021 NURIFLEX, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of NURIFLEX, Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURIFLEX, Inc.
 *
 * For more information on this product, please see
 * http://www.nuriflex.co.kr
 *
 */

package com.msa.template.elena.rest.api;

import com.msa.template.elena.security.TokenProvider;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.enums.UserAuditLogAct;
import com.nuri.green.onm.user.entity.enums.UserAuditLogUpdateItemCode;
import com.nuri.green.onm.user.entity.jwt.User;
import com.nuri.green.onm.user.entity.params.CheckPasswdParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyPasswdParam;
import com.nuri.green.onm.user.entity.params.register.RegisterAuditLogParam;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserResult;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.green.onm.user.exception.ExternalServerException;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.exception.UnauthenticatedException;
import com.msa.template.elena.rest.AbstractBaseResource;
import com.nuri.green.onm.user.spec.UserAuditLogService;
import com.nuri.green.onm.user.spec.UserService;
import com.nuri.utils.DateUtil;
import com.nuri.utils.RestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "PasswordApiResource", description = "User Service API")
@RestController
public class PasswordApiResource extends AbstractBaseResource {

  @Value("${project.code}")
  private String svcName;

  @Autowired
  private UserService userService;

  @Autowired
  private UserAuditLogService userAuditLogService;

  @Autowired
  private TokenProvider tokenProvider;

  @Value("${nuri.jwt.auth.name}")
  private String authHeaderName;

  @Value("${nuri.jwt.access.name}")
  private String accessHeaderName;

  @Value("${msa.url.login}")
  private String loginServiceUrl;

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "현재 비밀번호 확인(access_token)")
  @PostMapping("/api/gnd-onm-user/v1.0/users/password/check")
  public ResponseEntity<ResultMessage<String>> checkPassword(
      @RequestBody CheckPasswdParam param) {
    log.info("Controller - checkPassword");

    ResultMessage<String> message = new ResultMessage<>();

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    User tokenUser = (User) authentication.getPrincipal();
    if (!param.getUserId().equals(tokenUser.getUserId())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR.name(), "check password"));
    }
    // 2. req validation.
    if (StringUtils.isEmpty(param.getUserId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "userId"));
    }
    if (StringUtils.isEmpty(param.getPassword())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "password"));
    }

    try {
      // 3. Password 찾기
      String userPassword = userService.getUserPassword(param.getUserId());
      if (userPassword == null) { // 해당 유저가 없거나, 비밀번호가 없을 경우
        throw new OperationException(ResultResCode.US5007.getCode(),
            ExceptionMessage.makeExceptionMessage(ResultErrorCode.INVALID_ENTITY_ERROR.name(),
                param.getUserId() + "의 비밀번호"));
      }

      // 4. ResultMessage 생성
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      if (encoder.matches(param.getPassword(), userPassword)) {
        message.setPayload(BooleanResultType.VALID.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[현재 비밀번호 확인] 실패 - 비밀번호 불일치");
        throw new OperationException(ResultResCode.US5007.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.INVALID_ENTITY_ERROR.name(), "비밀번호"));
      }


    } catch (Exception e) {
      log.error("[현재 비밀번호 확인] 실패", e);
      throw new OperationException(ResultResCode.US5007.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.INVALID_ENTITY_ERROR.name(), "비밀번호"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }


  @SecurityRequirement(name = "access_token")
  @SecurityRequirement(name = "auth_token")
  @Operation(summary = "비밀번호 변경")
  @Parameters(value = {
      @Parameter(name = "userId", description = "사용자 아이디", example = "gildong", in = ParameterIn.PATH),
      @Parameter(name = "x-auth-token", description = "AuthToken", in = ParameterIn.HEADER),
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/users/{userId}/password")
  public ResponseEntity<ResultMessage<String>> modifyPassword(
      @PathVariable("userId") String userId,
      @RequestBody ModifyPasswdParam param,
      HttpServletRequest request
  ) {
    // 1. req 확인.
    if (StringUtils.isEmpty(param.getPassword())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "password"));
    }
    // 2. User 존재 확인
    UserResult userResult = userService.checkUserId(userId);
    if (userResult == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND
              .name(), userId));
    }

    // 3. 이전 비밀번호와 동일 여부 확인
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String encodePassword = encoder.encode(param.getPassword());
//    if (encoder.matches(param.getPassword(), userResult.getPasswd())) {
//      throw new InvalidRequestException(ExceptionMessage
//          .makeExceptionMessage(ResultErrorCode.NO_NEED_TO_UPDATE.name(), "password"));
//    }

    // 4. auth_token? access_token?
    if (!StringUtils.isEmpty(tokenProvider.resolveToken(request, authHeaderName))) {
      // auth_token
      return modifyPasswordAuthToken(
          userResult,
          encodePassword,
          param.getAuthType(),
          param.getAuthInfo(),
          request);
    } else if (!StringUtils.isEmpty(tokenProvider.resolveToken(request, accessHeaderName))) {
      // access_token
      return modifyPasswordAccessToken(
          userResult,
          encodePassword,
          request);
    }
    throw new AuthorizedFailException("Token invalid");
  }

  public ResponseEntity<ResultMessage<String>> modifyPasswordAuthToken(
      UserResult userResult,
      String encodePassword,
      String authType,
      String authInfo,
      HttpServletRequest request) {

    ResultMessage<String> message = new ResultMessage<>();

    // 1. req 확인.
    String token = tokenProvider.resolveToken(request, authHeaderName);
    if (StringUtils.isEmpty(token)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "x-auth-token"));
    }
    if (StringUtils.isEmpty(authType)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "authType"));
    }
    if (StringUtils.isEmpty(authInfo)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "authInfo"));
    }

    // 2. auth_token 확인
    try {
      JSONObject validationRes = userService.requestAuthTokenValidation(token, authType, authInfo);
      log.info("result from AUTH-SERVICE : {}", validationRes.toJSONString());
    } catch (Exception e) {
      log.error("Token Validation to AUTH-SERVICE result : failed.");
      log.error(e.getMessage(), e);
      throw new ExternalServerException(ResultResCode.US5090.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "AUTH"));
    }

    try {
      // 3. 비밀번호 변경
      Date updateDt = DateUtil.getCurrentDateTime(); // 비밀번호 변경 날짜.

      HashMap<String, Object> modifyParam = new HashMap<>();
      modifyParam.put("userId", userResult.getUserId());
      modifyParam.put("password", encodePassword);
      modifyParam.put("updaterId", "auth-token");
      modifyParam.put("updatedDt", updateDt);

      int result = userService.modifyUser(modifyParam);

      if (result <= 0) {
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "비밀번호"));
      } else {
        message.setPayload(BooleanResultType.SUCCESS.getMsg());

        // 4. Logging
        registerChangePasswordLog(
            userResult.getPasswd(),
            encodePassword,
            request.getRemoteAddr(),
            userResult,
            userResult.getUserId(),
            updateDt
        );
      }

      // 4. Login Service로 전송
      try {
        toLoginService(userResult.getUserId(), encodePassword, updateDt, false);
      } catch (Exception e) {
        log.error("Password Update to LOGIN-SERVICE result : failed.");
        log.error(e.getMessage(), e);
        throw new ExternalServerException(ResultResCode.US5090.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
      }

      // 5. 결과 메시지 작성
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[비밀번호 변경] 실패", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "비밀번호"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  public ResponseEntity<ResultMessage<String>> modifyPasswordAccessToken(
      UserResult userResult,
      String encodePassword,
      HttpServletRequest request) {

    ResultMessage<String> message = new ResultMessage<>();

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User tokenUser = (User) authentication.getPrincipal();

    if (!userResult.getUserId().equals(tokenUser.getUserId())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR.name(), "해당 사용자의 비밀번호 변경"));
    }

    try {
      // 2. 비밀번호 변경
      Date updateDt = DateUtil.getCurrentDateTime(); // 비밀번호 변경 날짜.

      HashMap<String, Object> modifyParam = new HashMap<>();
      modifyParam.put("userId", userResult.getUserId());
      modifyParam.put("password", encodePassword);
      modifyParam.put("updaterId", tokenUser.getUserId());
      modifyParam.put("updatedDt", updateDt);

      int result = userService.modifyUser(modifyParam);

      if (result <= 0) {
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "비밀번호"));
      } else {
        message.setPayload(BooleanResultType.SUCCESS.getMsg());

        // 4. Logging
        registerChangePasswordLog(
            userResult.getPasswd(),
            encodePassword,
            request.getRemoteAddr(),
            userResult,
            userResult.getUserId(),
            updateDt
        );

      }

      // 4. Login Service로 전송
      try {
        toLoginService(userResult.getUserId(), encodePassword, updateDt, false);
      } catch (Exception e) {
        log.error("Password Update to LOGIN-SERVICE result : failed.");
        log.error(e.getMessage(), e);
        throw new ExternalServerException(ResultResCode.US5090.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
      }

      // 5. 결과 메시지 작성
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[비밀번호 변경] 실패", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "비밀번호"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  private void registerChangePasswordLog(String beforeEncoded, String afterEncoded, String ip,
      UserResult userResult, String createId, Date updateDt) throws Exception{
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("passwd", beforeEncoded);
      String before = jsonObject.toString();
      jsonObject.put("passwd", afterEncoded);
      String after = jsonObject.toString();

      RegisterAuditLogParam auditLogParam = RegisterAuditLogParam.builder()
          .userSeq(userResult.getUserSeq())
          .userId(userResult.getUserId())
          .userNm(userResult.getUserNm())
          .act(UserAuditLogAct.UPDATED.name())
          .updateItem(UserAuditLogUpdateItemCode.PASSWORD.name())
          .beforeValue(before)
          .afterValue(after)
          .ip(ip)
          .creatorId(createId)
          .createdDt(updateDt)
          .build();
      int auditLogResult = userAuditLogService.insertUserAuditLog(auditLogParam);
      if (auditLogResult == 0) {
        log.error("[비밀번호 변경] 변경사항 저장 실패 - 쿼리 결과 : 0");
        throw new OperationException();
      }
  }

  private void toLoginService(String userId, String encodePassword, Date updateDt, boolean tempYn)
      throws Exception {
    JSONObject paramdata = new JSONObject();
    paramdata.put("passwd", encodePassword);
    paramdata.put("tempPasswdIssuedYn", tempYn);
    paramdata.put("passwdUpdateDt", updateDt);
//    paramdata.put("updatedDt", DateUtil.getCurrentDateTime());

    JSONObject sendData = new JSONObject();
    sendData.put("url", loginServiceUrl + "/users/" + userId + "/password/init");
    sendData.put("method", HttpMethod.PUT);
    sendData.put("content-type", "application/json");
    sendData.put("params", paramdata);

    ResponseEntity<String> loginResult = RestUtil.sendRestApi(sendData);
    log.debug("loginResult.getStatusCode().value() :: {}", loginResult.getStatusCode().value());
    if (loginResult.getStatusCode() != HttpStatus.OK) {
     throw new ExternalServerException(ResultResCode.US5090.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
    }
  }

}
