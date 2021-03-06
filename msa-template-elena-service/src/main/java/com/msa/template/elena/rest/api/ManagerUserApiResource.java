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

import com.msa.template.elena.utils.SvcUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.template.elena.rest.AbstractBaseResource;
import com.nuri.code.NuriCode;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.NuriRole;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.enums.UserAuditLogAct;
import com.nuri.green.onm.user.entity.enums.UserAuditLogUpdateItemCode;
import com.nuri.green.onm.user.entity.jwt.User;
import com.nuri.green.onm.user.entity.page.OrderByMap;
import com.nuri.green.onm.user.entity.params.ManagerUserPageParam;
import com.nuri.green.onm.user.entity.params.modify.ManagerModifyUserParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyStatusParam;
import com.nuri.green.onm.user.entity.params.register.ManagerRegisterUserParam;
import com.nuri.green.onm.user.entity.params.register.RegisterAuditLogParam;
import com.nuri.green.onm.user.entity.params.register.RegisterUserParam;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.PagingGridResult;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserCompany;
import com.nuri.green.onm.user.entity.results.UserGridExcelResult;
import com.nuri.green.onm.user.entity.results.UserGridReturnResult;
import com.nuri.green.onm.user.entity.results.UserInfoResult;
import com.nuri.green.onm.user.entity.results.UserResult;
import com.nuri.green.onm.user.entity.results.UserReturnResult;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.green.onm.user.exception.ExternalServerException;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.exception.UnauthenticatedException;
import com.nuri.green.onm.user.spec.UserAuditLogService;
import com.nuri.green.onm.user.spec.UserService;
import com.nuri.utils.DateUtil;
import com.nuri.utils.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "ManagerUserApiResource", description = "Manager User Service API")
@RestController
public class ManagerUserApiResource extends AbstractBaseResource {

  @Value("${project.code}")
  private String svcName;

  @Autowired
  private UserService userService;

  @Autowired
  private UserAuditLogService userAuditLogService;

  @Autowired
  private SvcUtils svcUtils;


  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "?????? ????????? ??????(??????)",
      description =
          "<b>serviceStatus ??????</b><br>"
              + "- US000(\"??????\")<br>"
              + "- US001(\"?????? ??????\")<br><br>"
              + "<b>orders ?????? ??????</b>(???????????? ??????)<br>"
              + "- userId<br>"
              + "- userNm<br>"
              + "- createdDt")
  @GetMapping("/api/gnd-onm-user/v1.0/manager/users/excel")
  public ResponseEntity<ResultMessage<UserGridExcelResult>> getUserGridExcelManager(
      @ParameterObject ManagerUserPageParam userPageParam)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    UserResult userResult = userService.checkUserId(accessUser.getUserId());
    if (userResult == null) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_ERROR.name()));
    }

    UserCompany userCompany = userService.getUserCompany(userResult.getUserSeq());
    if (userCompany == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), "?????? ???????????? ?????? ??????"));
    }

    // 2. ?????? ???????????? validation
    ResultMessage message = new ResultMessage();

    try {
      // 3. Make param
      if (!StringUtils.isEmpty(userPageParam.getOrders())) {
        userPageParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(userPageParam.getOrderMap(),
            "userId", "userNm", "createdDt");
        // add table alias
        orderByMapList.forEach(elem -> {
          elem.setSort("ui." + elem.getSort());
        });
      }
      ObjectMapper mapper = new ObjectMapper();
      userPageParam.setOffset(null);
      userPageParam.setLimit(null);
      HashMap<String, Object> param = mapper.convertValue(userPageParam, HashMap.class);
      param.put("compSeq", userCompany.getCompSeq());
      param.put("roleId", NuriRole.R003.name());

      // 4. Get grid data
      List<UserGridReturnResult> gridList = userService.getUserGridListByParam(param);
//    Integer gridTotal = userService.getUserGridTotalCntByParam(param);

      // 5.  Make Excel
      Workbook workbook = new HSSFWorkbook();

      Sheet sheet = workbook.createSheet("User List");
      Row row = sheet.createRow(0);
      int cellIdx = 0;
      row.createCell(cellIdx++).setCellValue("??????");
      row.createCell(cellIdx++).setCellValue("?????????");
      row.createCell(cellIdx++).setCellValue("??????");
      row.createCell(cellIdx++).setCellValue("???????????????");
      row.createCell(cellIdx++).setCellValue("????????????");
      row.createCell(cellIdx++).setCellValue("?????????");
      row.createCell(cellIdx++).setCellValue("????????????");
      row.createCell(cellIdx++).setCellValue("??????");

      for (int i = 0; i < gridList.size(); i++) {
        row = sheet.createRow(i + 1);
        UserGridReturnResult elem = gridList.get(i);
        if (elem.getServiceStatusCode().equals(NuriCode.US000.name())) {
          elem.setServiceStatus(NuriCode.US000.getKoMsg());
        } else {
          elem.setServiceStatus("????????????");
        }
        cellIdx = 0;
        row.createCell(cellIdx++).setCellValue(gridList.size() - i);
        row.createCell(cellIdx++).setCellValue(elem.getUserId());
        row.createCell(cellIdx++).setCellValue(elem.getUserNm());
        row.createCell(cellIdx++).setCellValue(elem.getCellphone());
        row.createCell(cellIdx++).setCellValue(elem.getBirthday());
        row.createCell(cellIdx++).setCellValue(elem.getEmail());
        row.createCell(cellIdx++).setCellValue(elem.getCreatedDt());
        row.createCell(cellIdx++).setCellValue(elem.getServiceStatus());
      }

      String fileName = "Operator_UserList_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".xls";
      File rolePath = new File(NuriRole.R002.getRoleName());
      if (!rolePath.exists()) {
        rolePath.mkdir();
      }
      File userIdPath = new File(rolePath + File.separator + accessUser.getUserId());
      if (!userIdPath.exists()) {
        userIdPath.mkdir();
      }
      File f = new File(userIdPath.getPath() + File.separator + fileName);
      FileOutputStream fos = new FileOutputStream(f, false);
      workbook.write(fos);
      fos.close();

      // 6. Return
      UserGridExcelResult result = new UserGridExcelResult();
      result.setCallbackURL("/api/gnd-onm-user/v1.0/manager/excel/download/" + fileName);

      message.setPayload(result);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[?????? ?????????(??????)] ??????", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "?????? ?????????(??????)"));

    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "Manager Excel ????????????")
  @GetMapping("/api/gnd-onm-user/v1.0/manager/excel/download/{fileName}")
  public ResponseEntity<Resource> getExcelDownLoadManager(
      @PathVariable("fileName") String fileName)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    // 2. ?????? ???????????? validation
    if (StringUtils.isEmpty(fileName)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "fileName"));
    }

    // 3. Get File
    File file = new File(
        NuriRole.R002.getRoleName() + File.separator + accessUser.getUserId() + File.separator + fileName);
    if (!file.exists()) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), "??????"));
    }
    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

    // 4. Response
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");
    headers.add("Content-Disposition", "attachment; filename=" + fileName);

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(file.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "?????? ????????? ??????(Grid)",
      description =
          "<b>serviceStatus ??????</b><br>"
              + "- US000(\"??????\")<br>"
              + "- US001(\"?????? ??????\")<br><br>"
              + "<b>orders ?????? ??????</b>(???????????? ??????)<br>"
              + "- userId<br>"
              + "- userNm<br>"
              + "- createdDt")
  @GetMapping("/api/gnd-onm-user/v1.0/manager/users")
  public ResponseEntity<ResultMessage<PagingGridResult>> getUserGridManager(
      @ParameterObject ManagerUserPageParam userPageParam)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    UserResult userResult = userService.checkUserId(accessUser.getUserId());
    if (userResult == null) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_ERROR.name()));
    }

    UserCompany userCompany = userService.getUserCompany(userResult.getUserSeq());
    if (userCompany == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), "?????? ???????????? ?????? ??????"));
    }

    // 2. ?????? ???????????? validation

    ResultMessage message = new ResultMessage();

    try {
      // 3. Make param
      // 3. Make param
      if (!StringUtils.isEmpty(userPageParam.getOrders())) {
        userPageParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(userPageParam.getOrderMap(),
            "userId", "userNm", "createdDt");
        // add table alias
        orderByMapList.forEach(elem -> {
          elem.setSort("ui." + elem.getSort());
        });
        userPageParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> param = mapper.convertValue(userPageParam, HashMap.class);
      param.put("compSeq", userCompany.getCompSeq());
      param.put("roleId", NuriRole.R003.name());

      // 4. Get grid data
      List<UserGridReturnResult> gridList = userService.getUserGridListByParam(param);
      Integer gridTotal = userService.getUserGridTotalCntByParam(param);
      gridList.forEach(elem -> {
        if (elem.getCellphone() != null) {
          elem.setCellphone(elem.getCellphone().replaceAll(".{4}$", "****"));
        }
        if (elem.getServiceStatusCode().equals(NuriCode.US000.name())) {
          elem.setServiceStatus(NuriCode.US000.getKoMsg());
        } else {
          elem.setServiceStatus("????????????");
        }
      });

      // 6. Return
      PagingGridResult<UserGridReturnResult> result = new PagingGridResult();
      result.setLimit(userPageParam.getLimit());
      result.setOffset(userPageParam.getOffset());
      result.setTotalCnt(gridTotal);
      result.setDataCnt(gridList.size());
      result.setData(gridList);

      message.setPayload(result);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[?????? ????????? ??????]", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "?????? ?????????"));
    }
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "?????? ??????")
  @PostMapping("/api/gnd-onm-user/v1.0/manager/users")
  public ResponseEntity<ResultMessage<String>> registerUserManager(
      @RequestBody ManagerRegisterUserParam registerUserParam)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();
    JSONParser parser = new JSONParser();
    JSONObject content = (JSONObject) parser.parse(accessUser.getContent());
    Integer compSeq = Integer.valueOf(content.get("compSeq").toString());

    // 2. ?????? ???????????? validation
    if (StringUtils.isEmpty(registerUserParam.getUserId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "userId"));
    }
    if (StringUtils.isEmpty(registerUserParam.getUserName())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "userName"));
    }
    if (StringUtils.isEmpty(registerUserParam.getPassword())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "password"));
    }
    if (StringUtils.isEmpty(registerUserParam.getCellphone())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "cellphone"));
    }
    if (StringUtils.isEmpty(registerUserParam.getEmail())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "email"));
    }
    if (StringUtils.isEmpty(registerUserParam.getBirthday())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "birthday"));
    }

    // 3. ????????? ?????? ??????
    UserResult userResult = userService.checkUserId(registerUserParam.getUserId());
    log.debug("userResult : {}", userResult);
    if (userResult != null) {
      log.error("[?????? ??????] ?????? - ????????? ??????");
      throw new OperationException(ResultResCode.US5005.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.DUPLICATED_ERROR.name(),
              registerUserParam.getUserId()));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 4. ????????? ??????.
      RegisterUserParam user = registerUserParam.toEntity("");
      user.setCreatorId(accessUser.getUserId());
      user.setServiceStatus(NuriCode.US000.name()); // default : "??????"
      user.setSmsVerified(Integer.valueOf(BooleanResultType.Y.getMsg())); // ???????????? ????????? ?????? ?????????????????? ??????
      user.setEmailVerified(Integer.valueOf(BooleanResultType.N.getMsg()));
      user.setRoleId(NuriRole.R003.name()); // ?????? ????????? ??????.
      user.setCompSeq(compSeq);

      UserResult result = userService.registerUser(user);

      if (result == null) {
        log.error("[?????? ??????] ?????? - ?????? ?????? : 0");
        throw new OperationException(ResultResCode.US5001.getCode(),
            ExceptionMessage.makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "??????"));
      }

      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[?????? ??????] ??????", e);
      throw new OperationException(ResultResCode.US5001.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "??????"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);

  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "????????? ?????? ?????? ??????(?????? ????????? -> ?????????)",
      description = "<b>serviceStatus ??????</b><br>"
              + "- US000(\"??????\")<br>"
              + "- US001(\"????????????\")")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "????????? ?????????", example = "gildong", in = ParameterIn.PATH)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/manager/users/{userId}/status")
  public ResponseEntity<ResultMessage<String>> modifyUserStatusManager(
      @PathVariable("userId") String userId,
      @RequestBody ModifyStatusParam statusParam,
      HttpServletRequest request) throws ParseException {
    log.info("Controller - modifyUserStatusManager");
    String serviceStatus = statusParam.getServiceStatus();
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User tokenUser = (User) authentication.getPrincipal();

    // 2. req ??????.
    if (StringUtils.isEmpty(serviceStatus)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "serviceStatus"));
    }

    UserInfoResult userResult = userService.getUserInfo(userId);
    if (userResult == null) {
      log.error("[????????? ?????? ?????? ??????] ?????? - ????????? ??????");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    JSONParser parser = new JSONParser();
    JSONObject content = (JSONObject) parser.parse(tokenUser.getContent());
    log.debug("----------------> user token content : {} ", content.toJSONString());

    if (Integer.valueOf(content.get("compSeq").toString()) != userResult.getCompSeq()) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "?????? ???????????? ???????????? ??????"));
    }

    if (!userResult.getRoleId().equals(NuriRole.R003.name())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "?????? ???????????? ???????????? ??????"));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 3. ???????????? ??????
      Date updateDt = DateUtil.getCurrentDateTime();

      // Login Service ??? ?????? ?????? ??????.
      ResponseEntity<String> loginResult = svcUtils
          .sendUserStatusToLoginService(userId, statusParam.getServiceStatus(), updateDt);
      if (loginResult.getStatusCode() != HttpStatus.OK) {
        throw new ExternalServerException(ResultResCode.US5090.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
      }

      HashMap<String, Object> modifyParam = new HashMap<>();
      modifyParam.put("userId", userResult.getUserId());
      modifyParam.put("serviceStatus", serviceStatus);
      modifyParam.put("updaterId", tokenUser.getUserId());
      modifyParam.put("updatedDt", updateDt);
      int result = userService.modifyUser(modifyParam);

      // 3.1 ???????????? Logging
      if (result > 0) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serviceStatus", userResult.getServiceStatus());
        String before = jsonObject.toString();
        jsonObject.put("serviceStatus", serviceStatus);
        String after = jsonObject.toString();
        RegisterAuditLogParam auditLogParam = RegisterAuditLogParam.builder()
            .userSeq(userResult.getUserSeq())
            .userId(userResult.getUserId())
            .userNm(userResult.getUserNm())
            .act(UserAuditLogAct.UPDATED.name())
            .updateItem(UserAuditLogUpdateItemCode.USERINFO.name())
            .beforeValue(before)
            .afterValue(after)
            .ip(request.getRemoteAddr())
            .creatorId(tokenUser.getUserId())
            .createdDt(updateDt).build();
        int auditLogResult = userAuditLogService.insertUserAuditLog(auditLogParam);
        if (auditLogResult == 0) {
          log.error("[????????? ?????? ?????? ??????] ???????????? ?????? ?????? - ???????????? : 0");
          throw new OperationException(ResultResCode.US5003.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "?????? ?????? ??????"));
        }
      } else {
        log.error("[????????? ?????? ?????? ??????] ?????? - ???????????? : 0");
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "?????? ?????? ??????"));
      }

      // 4. ResultMessage ??????
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[????????? ?????? ?????? ??????] ??????", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "?????? ?????? ??????"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "????????? ???????????? ????????? ?????? ??????")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "????????? ?????????", example = "hong", in = ParameterIn.PATH)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/manager/users/{userId}")
  public ResponseEntity<ResultMessage<UserReturnResult>> getUserManager(
      @PathVariable("userId") String userId,
      HttpServletResponse response) throws Exception {

    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 2. User ??????
      UserInfoResult user = userService.getUserInfo(userId);
      if (user == null) {
        log.error("[????????? ?????? ??????] ?????? - ????????? ??????");
        throw new OperationException(ResultResCode.US5006.getCode(),
            ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
      }

      // ?????? ??????
      if (!user.getRoleId().equals(NuriRole.R003.name())) {
        throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
            ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
                .name(), "????????? ?????? ??????"));
      }
      // ?????? ?????? ??????
      User tokenUser = (User) authentication.getPrincipal();
      JSONParser parser = new JSONParser();
      JSONObject content = (JSONObject) parser.parse(tokenUser.getContent());
      log.debug("----------------> user token content : {} ", content.toJSONString());
      if (Integer.valueOf(content.get("compSeq").toString()) != user.getCompSeq()) {
        throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
            ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
                .name(), "????????? ?????? ??????"));
      }

      message.setPayload(user.toEntity());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[????????? ?????? ??????] ??????", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "????????? ??????"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "????????? ?????? ??????")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "????????? ?????????", example = "gildong", in = ParameterIn.PATH)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/manager/users/{userId}")
  public ResponseEntity<ResultMessage<String>> modifyUserManager(
      @PathVariable("userId") String userId,
      @RequestBody ManagerModifyUserParam modifyUserParam,
      HttpServletRequest request) throws Exception {
    log.info("Controller - modifyUser");
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User tokenUser = (User) authentication.getPrincipal();

    // 2. req ??????.
    if (modifyUserParam == null) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.INVALID_PARAM_ERROR.name(), "modifyUserParam"));
    }

    UserInfoResult user = userService.getUserInfo(userId);
    if (user == null) {
      log.error("[????????? ?????? ??????] ?????? - ????????? ??????");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    // ?????? ??????
    if (!user.getRoleId().equals(NuriRole.R003.name())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "?????? ????????? ?????? ??????"));
    }

    // ?????? ?????? ??????
    JSONParser parser = new JSONParser();
    JSONObject content = (JSONObject) parser.parse(tokenUser.getContent());
    log.debug("----------------> user token content : {} ", content.toJSONString());
    if (Integer.valueOf(content.get("compSeq").toString()) != user.getCompSeq()) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "?????? ????????? ?????? ??????"));
    }

    ResultMessage message = new ResultMessage();
    try {
      // 3. ??????
      Date updateDt = DateUtil.getCurrentDateTime();

      Map<String, Object> queryParam = new HashMap<String, Object>();
      queryParam.put("userId", userId);
      queryParam.put("updatedDt", updateDt);
      queryParam.put("updaterId", tokenUser.getUserId());

      queryParam.put("userSeq", user.getUserSeq());
      queryParam.put("userNm", modifyUserParam.getUserNm());
      queryParam.put("cellphone", modifyUserParam.getCellphone());
      queryParam.put("email", modifyUserParam.getEmail());
      queryParam.put("birthday", modifyUserParam.getBirthday());
      queryParam.put("serviceStatus", modifyUserParam.getServiceStatus());

      // -----------------------------------------------------
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
        befJSON.put("serviceStatus", bef.getServiceStatus());

        JSONObject aftJSON = new JSONObject();
        aftJSON.put("userNm", queryParam.get("userNm"));
        aftJSON.put("cellphone", queryParam.get("cellphone"));
        aftJSON.put("email", queryParam.get("email"));
        aftJSON.put("birthday", queryParam.get("birthday"));
        aftJSON.put("serviceStatus", queryParam.get("serviceStatus"));

        // 4. Logging
        RegisterAuditLogParam auditLogParam = RegisterAuditLogParam.builder()
            .userSeq(user.getUserSeq())
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
          log.error("[????????? ?????? ??????] ???????????? ?????? ?????? - ???????????? : 0");
          throw new OperationException(ResultResCode.US5003.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), userId));
        }

      } else {
        log.error("[????????? ?????? ??????] ?????? - ???????????? : 0");
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), userId));
      }
      // 4. ResultMessage ??????
      message.setPayload(result);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[????????? ?????? ?????? ??????] ??????", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "?????? ?????? ??????"));
    }
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "???????????? ???????????? ?????????")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "????????? ?????????", example = "hong", in = ParameterIn.PATH)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/manager/users/{userId}/password/init")
  public ResponseEntity<ResultMessage<String>> initPasswordManager(
      @PathVariable("userId") String userId,
      HttpServletRequest request)
      throws Exception {

    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User tokenUser = (User) authentication.getPrincipal();

    // 2. req ??????.
    UserInfoResult userResult = userService.getUserInfo(userId);
    if (userResult == null) {
      log.error("[???????????? ?????????] ?????? - ????????? ??????");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    // ?????? ??????
    if (!userResult.getRoleId().equals(NuriRole.R003.name())) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "?????? ???????????? ???????????? ??????"));
    }

    // ?????? ?????? ??????
    JSONParser parser = new JSONParser();
    JSONObject content = (JSONObject) parser.parse(tokenUser.getContent());
    log.debug("----------------> user token content : {} ", content.toJSONString());
    if (Integer.valueOf(content.get("compSeq").toString()) != userResult.getCompSeq()) {
      throw new AuthorizedFailException(ResultResCode.US4032.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_TO_ERROR
              .name(), "?????? ???????????? ???????????? ??????"));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 3. ???????????? ?????????
      String newPass = StringUtil.getRandomPassword(); // ?????? ???????????? ??????
      String pass = new BCryptPasswordEncoder().encode(newPass);
      Date updateDt = DateUtil.getCurrentDateTime();
      HashMap<String, Object> modifyParam = new HashMap<>();
      modifyParam.put("userId", userId);
      modifyParam.put("password", pass);
      modifyParam.put("updaterId", tokenUser.getUserId());
      modifyParam.put("updatedDt", updateDt);

      int updateCnt = userService.modifyUser(modifyParam);
      if (updateCnt > 0) {
        // 4. ????????? ???????????? ??????
        ResponseEntity<String> loginResult = svcUtils
            .sendPasswdToLoginService(userId, pass, updateDt);
        if (loginResult.getStatusCode() != HttpStatus.OK) {
          throw new ExternalServerException(ResultResCode.US5090.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
        }

        // 5. SMS ??????
        ResponseEntity<String> umsResult = svcUtils
            .sendUmsService(userResult.getCellphone(), newPass, userResult.getUserId());
        if (umsResult.getStatusCode() != HttpStatus.OK) {
          throw new ExternalServerException(ResultResCode.US5090.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "UMS"));
        }

        // 6. logging
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("passwd", userResult.getPasswd());
        String before = jsonObject.toString();
        jsonObject.put("passwd", pass);
        String after = jsonObject.toString();
        RegisterAuditLogParam auditLogParam = RegisterAuditLogParam.builder()
            .userSeq(userResult.getUserSeq())
            .userId(userResult.getUserId())
            .userNm(userResult.getUserNm())
            .act(UserAuditLogAct.UPDATED.name())
            .updateItem(UserAuditLogUpdateItemCode.USERINFO.name())
            .updateItem(UserAuditLogUpdateItemCode.PASSWORD.name())
            .beforeValue(before)
            .afterValue(after)
            .ip(request.getRemoteAddr())
            .creatorId(tokenUser.getUserId())
            .createdDt(updateDt).build();
        int auditLogResult = userAuditLogService.insertUserAuditLog(auditLogParam);
        if (auditLogResult == 0) {
          log.error("[???????????? ?????????] ???????????? ?????? ?????? - ???????????? : 0");
          throw new OperationException(ResultResCode.US5003.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(),
                      "???????????? ????????? ??????"));
        }

        // 7. Result
        message.setPayload(BooleanResultType.SUCCESS.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[???????????? ?????????] ?????? - ???????????? : 0");
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "???????????? ????????? ??????"));
      }

    } catch (Exception e) {
      log.error("[???????????? ?????????] ??????", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "???????????? ????????? ??????"));
    }
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

}
