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
import com.msa.template.elena.rest.AbstractBaseResource;
import com.nuri.code.NuriCode;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.enums.UserAuditLogAct;
import com.nuri.green.onm.user.entity.enums.UserAuditLogUpdateItemCode;
import com.nuri.green.onm.user.entity.jwt.User;
import com.nuri.green.onm.user.entity.params.UserContactInfoParam;
import com.nuri.green.onm.user.entity.params.UserIdFindParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyUserParam;
import com.nuri.green.onm.user.entity.params.register.RegisterAuditLogParam;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.FindUserIdResult;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserInfoResult;
import com.nuri.green.onm.user.entity.results.UserResult;
import com.nuri.green.onm.user.entity.results.UserReturnResult;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.exception.UnauthenticatedException;
import com.nuri.green.onm.user.spec.UserService;
import com.nuri.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "UserApiResource", description = "User Service API")
@RestController
public class PublicUserApiResource extends AbstractBaseResource {

  @Value("${project.code}")
  private String svcName;

  @Autowired
  private UserService userService;

  @Autowired
  private TokenProvider tokenProvider;

  @Value("${nuri.jwt.auth.name}")
  private String authHeaderName;


  @SecurityRequirements // remove security
  @Operation(summary = "아이디 중복 체크")
  @Parameters(value = {
      @Parameter(name = "userId", description = "사용자 아이디", example = "hong", in = ParameterIn.QUERY)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/users/check/id")
  public ResponseEntity<ResultMessage<String>> checkIdExists(String userId) {

    UserResult userResult = userService.checkUserId(userId);
    log.info("userResult : {}", userResult);

    ResultMessage message = new ResultMessage();

    if (userResult == null) {
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } else {
      if (userResult.getServiceStatus().equals(NuriCode.US001.name())) {
        throw new OperationException(ResultResCode.US5007.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.INVALID_ENTITY_ERROR.name(),
                    "serviceStatus"));
      }else{
        throw new OperationException(ResultResCode.US5005.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.DUPLICATED_ERROR.name(), userId));

      }

    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirements // remove security
  @Operation(summary = "사용자 이름 및 연락처 정보로 조회")
  @PostMapping("/api/gnd-onm-user/v1.0/users/username/{userName}")
  public ResponseEntity<ResultMessage<String>> verifyCellphone(
      @PathVariable("userName") String userName, @RequestBody UserContactInfoParam body) {

    if (StringUtils.isEmpty(body.getCellphone()) && StringUtils.isEmpty(body.getEmail())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR
              .name(), "cellphone or email"));
    }

    Map<String, Object> param = new HashMap<String, Object>();
    param.put("userName", userName);
    param.put("email", body.getEmail());
    param.put("cellphone", body.getCellphone());

    UserResult userResult = userService.getUserInfoByName(param);
    log.info("userResult : {}", userResult);

    if (userResult == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userName));
    }

    ResultMessage message = new ResultMessage();
    message.setPayload(BooleanResultType.OK.getMsg());
    message.setCode(svcName + ResultResCode.US2000.getCode());
    message.setMessage(ResultResCode.US2000.getKoMsg());

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirements // remove security
  @Operation(summary = "사용자 아이디로 연락처 정보 유효성 확인")
  @PostMapping("/api/gnd-onm-user/v1.0/users/{userId}/contact/verify")
  public ResponseEntity<ResultMessage<String>> verifyUserIdCellphone(
      @PathVariable("userId") String userId, @RequestBody UserContactInfoParam body) {

    if (StringUtils.isEmpty(body.getCellphone()) && StringUtils.isEmpty(body.getEmail())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR
              .name(), "cellphone or email"));
    }

    UserInfoResult userResult = userService.getUserInfo(userId);

    if (userResult == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    // 휴대전화 정보 불일치
    if (!StringUtils.isEmpty(body.getCellphone()) && !userResult.getCellphone()
        .equals(body.getCellphone())) {
      throw new OperationException(ResultResCode.US5007.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.INVALID_ENTITY_ERROR.name(), "휴대전화번호"));
    }

    // 이메일 정보 불일치
    if (!StringUtils.isEmpty(body.getEmail()) && !userResult.getEmail().equals(body.getEmail())) {
      throw new OperationException(ResultResCode.US5007.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.INVALID_ENTITY_ERROR.name(), "이메일"));
    }

    ResultMessage message = new ResultMessage();
    message.setPayload(BooleanResultType.VALID.getMsg());
    message.setCode(svcName + ResultResCode.US2000.getCode());
    message.setMessage(ResultResCode.US2000.getKoMsg());

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "auth_token")
  @Parameters(value = {
      @Parameter(name = "x-auth-token", description = "AuthToken", in = ParameterIn.HEADER),
  })
  @Operation(summary = "아이디 찾기")
  @PostMapping("/api/gnd-onm-user/v1.0/users/find/id")
  public ResponseEntity<ResultMessage<FindUserIdResult>> findUserId(
      @RequestBody UserIdFindParam body, HttpServletRequest request) {
    log.info("Controller - findUserId");

    // 1. req validation.
    String token = tokenProvider.resolveToken(request, authHeaderName);
    if (StringUtils.isEmpty(token)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "x-auth-token"));
    }
    if (StringUtils.isEmpty(body.getUserName())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(),
              "userName"));
    }
    if (StringUtils.isEmpty(body.getAuthType()) || StringUtils.isEmpty(body.getAuthInfo())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(),
              "authType or authInfo"));
    }

    Map<String, Object> queryParam = new HashMap<String, Object>();
    queryParam.put("userName", body.getUserName());

    if (body.getAuthType().equals("email")) {
      queryParam.put("email", body.getAuthInfo());
    } else if (body.getAuthType().equals("sms")) {
      queryParam.put("sms", body.getAuthInfo());
    } else {
      throw new InvalidRequestException(ExceptionMessage
          .makeExceptionMessage(ResultErrorCode.BAD_REQUEST_ERROR.name()));
    }

    // 2. 인증토큰 Validation 요청.
    try {
      JSONObject validationRes = userService
          .requestAuthTokenValidation(token, body.getAuthType(), body.getAuthInfo());
      log.info("result from AUTH-SERVICE : {}", validationRes.toJSONString());
    } catch (Exception e) {
      log.error("Token Validation to AUTH-SERVICE result : failed.");
      log.error(e.getMessage());
      throw new AuthorizedFailException(ResultResCode.US4031.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.INVALID_TOKEN_ERROR.name(), "x-auth-token"));
    }

    // 3. 아이디 찾기
    UserResult userResult = userService.getUserInfoByName(queryParam);
    log.info("userResult : {}", userResult);

    if (userResult == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), body.getUserName()));
    }

    FindUserIdResult result = FindUserIdResult.builder()
        .userId(userResult.getUserId())
        .userName(userResult.getUserNm())
        .telephone(userResult.getTelephone())
        .cellphone(userResult.getCellphone())
        .email(userResult.getEmail())
        .createdDt(userResult.getCreatedDt())
        .build();

    ResultMessage message = new ResultMessage();
    message.setPayload(result);
    message.setCode(svcName + ResultResCode.US2000.getCode());
    message.setMessage(ResultResCode.US2000.getKoMsg());

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }


  @SecurityRequirement(name = "access_token")
  @Operation(summary = "사용자 아이디로 사용자 정보 조회")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "사용자 아이디", example = "hong", in = ParameterIn.PATH)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/users/{userId}")
  public ResponseEntity<ResultMessage<UserReturnResult>> getUserPublic(
      @PathVariable("userId") String userId) throws Exception {

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    User tokenUser = (User) authentication.getPrincipal();

    UserInfoResult userResult = userService.getUserInfo(userId);

    if (userResult == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    if (!userResult.getUserId().equals(tokenUser.getUserId())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "해당 사용자 정보 조회"));
    }

    ResultMessage message = new ResultMessage();

    try {
      // System.out.println("-----> userResult : " + userResult.toString());
//      String serviceStatus = NuriCode.getCodeList("US").stream()
//          .filter(c -> c.name().equals(userResult.getServiceStatus()))
//          .findAny().get().getKoMsg();
      if(userResult.getServiceStatus().equalsIgnoreCase("US000")){
        userResult.setServiceStatus("정상");
      }else{
        userResult.setServiceStatus("자격중지");
      }

      message.setPayload(userResult.toEntity());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[내 정보 조회] 실패", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "사용자 정보"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }


  @SecurityRequirement(name = "access_token")
  @Operation(summary = "사용자 정보 변경")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "사용자 아이디", example = "gildong", in = ParameterIn.PATH)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/users/{userId}")
  public ResponseEntity<ResultMessage<String>> modifyUserInfo(
      @PathVariable("userId") String userId,
      @RequestBody ModifyUserParam modifyUserParam,
      HttpServletRequest request) {
    log.info("Controller - changeUserInfo");

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User tokenUser = (User) authentication.getPrincipal();

    // 2. User 존재 확인
    UserResult userResult = userService.checkUserId(userId);
    if (userResult == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    if (!userResult.getUserId().equals(tokenUser.getUserId())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "해당 사용자 정보 변경"));
    }

    ResultMessage message = new ResultMessage();

    try {

      // 3. 수정.
      Date updateDt = DateUtil.getCurrentDateTime();
      Map<String, Object> queryParam = new HashMap<String, Object>();
      queryParam.put("userId", userId);
      queryParam.put("updatedDt", updateDt);
      queryParam.put("updaterId", tokenUser.getUserId());

      queryParam.put("userSeq", userResult.getUserSeq());
      queryParam.put("userNm", modifyUserParam.getUserNm());
      queryParam.put("cellphone", modifyUserParam.getCellphone());
      queryParam.put("email", modifyUserParam.getEmail());
      queryParam.put("birthday", modifyUserParam.getBirthday());

      String result = "";

      int updated = userService.modifyUser(queryParam);
      if (updated > 0) {
        result = BooleanResultType.SUCCESS.getMsg();

        UserResult bef = userService.checkUserId(userId);
        JSONObject befJSON = new JSONObject();
        befJSON.put("userNm", bef.getUserNm());
        befJSON.put("cellphone", bef.getCellphone());
        befJSON.put("email", bef.getEmail());
        befJSON.put("birthday", bef.getBirthday());

        JSONObject aftJSON = new JSONObject();
        aftJSON.put("userNm", queryParam.get("userNm"));
        aftJSON.put("cellphone", queryParam.get("cellphone"));
        aftJSON.put("email", queryParam.get("email"));
        aftJSON.put("birthday", queryParam.get("birthday"));

        // 4. Logging
        RegisterAuditLogParam auditLogParam = RegisterAuditLogParam.builder()
            .userSeq(userResult.getUserSeq())
            .userId(userId)
            .userNm(modifyUserParam.getUserNm())
            .act(UserAuditLogAct.UPDATED.name())
            .updateItem(UserAuditLogUpdateItemCode.USERINFO.name())
            .beforeValue(befJSON.toJSONString())
            .afterValue(aftJSON.toJSONString())
            .ip(request.getRemoteAddr())
            .creatorId(tokenUser.getUserId())
            .createdDt(updateDt)
            .build();

        int logged = userService.registerAuditLog(auditLogParam);
        log.info("update loggin result={}", logged);

        if (logged == 0) {
          log.error("[내 정보 변경] 변경 사항 저장 실패 - 쿼리결과 : 0");
          throw new OperationException(ResultResCode.US5003.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "사용자 정보"));
        }
        // 4. ResultMessage 생성
        message.setPayload(result);
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());

      } else {
        log.error("[내 정보 변경] 실패 - 쿼리결과 : 0");
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "사용자 정보"));
      }

    } catch (Exception e) {
      log.error("[내 정보 변경] 실패", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "사용자 정보"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}
