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
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
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
import com.nuri.green.onm.user.entity.params.AdminUserPageParam;
import com.nuri.green.onm.user.entity.params.LoginHistoryParam;
import com.nuri.green.onm.user.entity.params.modify.AdminModifyUserParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyStatusParam;
import com.nuri.green.onm.user.entity.params.register.AdminRegisterUserParam;
import com.nuri.green.onm.user.entity.params.register.RegisterAuditLogParam;
import com.nuri.green.onm.user.entity.params.register.RegisterUserParam;
import com.nuri.green.onm.user.entity.results.AdminLoginHistoryGridExcelResult;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.LoginHistoryResult;
import com.nuri.green.onm.user.entity.results.PagingGridResult;
import com.nuri.green.onm.user.entity.results.ResultMessage;
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
import com.nuri.utils.RestUtil;
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
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONObject;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
@Tag(name = "AdminUserApiResource", description = "Admin User Service API")
@RestController
public class AdminUserApiResource extends AbstractBaseResource {

  @Value("${project.code}")
  private String svcName;

  @Value("${msa.url.login}")
  private String loginServiceUrl;

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
  @Operation(summary = "?????? ????????? ??????",
      description =
          "<b>serviceStatus ??????</b><br>"
              + "- US000(\"??????\")<br>"
              + "- US001(\"?????? ??????\")<br><br>"
              + "<b>orders ?????? ??????</b>(???????????? ??????)<br>"
              + "- userId<br>"
              + "- userNm<br>"
              + "- createdDt<br>"
              + "- compNm")
  @GetMapping("/api/gnd-onm-user/v1.0/admin/users")
  public ResponseEntity<ResultMessage<PagingGridResult>> getUserGridAdmin(
      @ParameterObject AdminUserPageParam userPageParam)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    ResultMessage message = new ResultMessage();

    // 2. ?????? ???????????? validation
    if (StringUtils.isEmpty(userPageParam.getRoleId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "roleId"));
    }

    try {

      // 3. Make param
      if (!StringUtils.isEmpty(userPageParam.getOrders())) {
        userPageParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(userPageParam.getOrderMap(),
            "userId", "userNm", "createdDt", "compNm");
        // add table alias
        orderByMapList.forEach(elem -> {
          switch (elem.getSort()) {
            case "created_dt":
            case "user_id":
            case "user_nm":
              elem.setSort("ui." + elem.getSort());
              break;
            case "comp_nm":
              elem.setSort("ci." + elem.getSort());
              break;
          }
        });
        userPageParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      if (userPageParam.getRoleId().equals(NuriRole.R001.name())) {
        userPageParam.setCompSeq(null);
      }
      HashMap<String, Object> param = mapper.convertValue(userPageParam, HashMap.class);

      // 4. Get grid data
      List<UserGridReturnResult> gridList = userService.getUserGridListByParam(param);
      Integer gridTotal = userService.getUserGridTotalCntByParam(param);

      // 5. ????????? ?????????
      gridList.forEach(elem -> {
        // ???????????? ?????????
        elem.setCellphone(elem.getCellphone().replaceAll(".{4}$", "****"));
        if (userPageParam.getRoleId().equals(NuriRole.R001.name())) {
          elem.setBirthday(null);
          elem.setCompNm(null);
        }
        if (elem.getServiceStatusCode().equals(NuriCode.US000.name())) {
          elem.setServiceStatus(NuriCode.US000.getKoMsg());
        } else {
          elem.setServiceStatus("????????????");
        }
      });

      // 6. Return
      PagingGridResult<UserGridReturnResult> result = new PagingGridResult<>();
      result.setData(gridList);
      result.setTotalCnt(gridTotal);
      result.setDataCnt(gridList.size());
      result.setLimit(userPageParam.getLimit());
      result.setOffset(userPageParam.getOffset());

      message.setPayload(result);
      message.setCode(svcName+ResultResCode.US2000.getCode());
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
  @Operation(summary = "?????? ????????? ??????(??????)",
      description =
          "<b>serviceStatus ??????</b><br>"
              + "- US000(\"??????\")<br>"
              + "- US001(\"?????? ??????\")<br><br>"
              + "<b>orders ?????? ??????</b>(???????????? ??????)<br>"
              + "- userId<br>"
              + "- userNm<br>"
              + "- createdDt<br>"
              + "- compNm")
  @GetMapping("/api/gnd-onm-user/v1.0/admin/users/excel")
  public ResponseEntity<ResultMessage<UserGridExcelResult>> getUserGridExcelAdmin(
      @ParameterObject AdminUserPageParam userPageParam)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    ResultMessage message = new ResultMessage();

    // 2. ?????? ???????????? validation
    if (StringUtils.isEmpty(userPageParam.getRoleId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "roleId"));
    }

    try {

      // 3. Make param
      if (!StringUtils.isEmpty(userPageParam.getOrders())) {
        userPageParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(userPageParam.getOrderMap(),
            "userId", "userNm", "createdDt", "compNm");
        // add table alias
        orderByMapList.forEach(elem -> {
          switch (elem.getSort()) {
            case "created_dt":
            case "user_id":
            case "user_nm":
              elem.setSort("ui." + elem.getSort());
              break;
            case "comp_nm":
              elem.setSort("ci." + elem.getSort());
              break;
          }
        });
        userPageParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      userPageParam.setOffset(null);
      userPageParam.setLimit(null);
      if (userPageParam.getRoleId().equals(NuriRole.R001.name())) {
        userPageParam.setCompSeq(null);
      }
      HashMap<String, Object> param = mapper.convertValue(userPageParam, HashMap.class);

      // 4. Get grid data
      List<UserGridReturnResult> gridList = userService.getUserGridListByParam(param);

      // 5.  Make Excel
      Workbook workbook = new HSSFWorkbook();

      Sheet sheet = workbook.createSheet("User List");
      Row row = sheet.createRow(0);
      int cellIdx = 0;
      row.createCell(cellIdx++).setCellValue("??????");
      row.createCell(cellIdx++).setCellValue("?????????");
      row.createCell(cellIdx++).setCellValue("??????");
      row.createCell(cellIdx++).setCellValue("???????????????");
      if (!userPageParam.getRoleId().equals(NuriRole.R001.name())) {
        row.createCell(cellIdx++).setCellValue("??????");
      }
      if (!userPageParam.getRoleId().equals(NuriRole.R001.name())) {
        row.createCell(cellIdx++).setCellValue("????????????");
      }
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
        if (!userPageParam.getRoleId().equals(NuriRole.R001.name())) {
          row.createCell(cellIdx++).setCellValue(elem.getCompNm());
        }
        if (!userPageParam.getRoleId().equals(NuriRole.R001.name())) {
          row.createCell(cellIdx++).setCellValue(elem.getBirthday());
        }
        row.createCell(cellIdx++).setCellValue(elem.getEmail());
        row.createCell(cellIdx++).setCellValue(elem.getCreatedDt());
        row.createCell(cellIdx++).setCellValue(elem.getServiceStatus());
      }

      String fileName = NuriRole.getRoleNameByCode(userPageParam.getRoleId()).toUpperCase()
          + "_UserList_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".xls";
      File rolePath = new File(NuriRole.R001.getRoleName());
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
      result.setCallbackURL("/api/gnd-onm-user/v1.0/admin/excel/download/" + fileName);

      message.setPayload(result);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[?????? ?????????(??????) ??????]", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "?????? ?????????(??????)"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "Admin Excel ????????????")
  @GetMapping("/api/gnd-onm-user/v1.0/admin/excel/download/{fileName}")
  public ResponseEntity<Resource> getExcelDownLoadAdmin(
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
        NuriRole.R001.getRoleName() + File.separator + accessUser.getUserId() + File.separator
            + fileName);
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
  @Operation(summary = "?????? ??????")
  @PostMapping("/api/gnd-onm-user/v1.0/admin/users")
  public ResponseEntity<ResultMessage<String>> registerUserAdmin(
      @RequestBody AdminRegisterUserParam registerUserParam)
      throws Exception {
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

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
    if (StringUtils.isEmpty(registerUserParam.getRoleId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "roleId"));
    }
    if (registerUserParam.getRoleId().equals(NuriRole.R002.name()) && StringUtils
        .isEmpty(registerUserParam.getCompSeq())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "compSeq"));
    }
    if (registerUserParam.getRoleId().equals(NuriRole.R002.name()) && StringUtils
        .isEmpty(registerUserParam.getBirthday())) {
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
      RegisterUserParam user = registerUserParam.toEntity();
      user.setCreatorId(accessUser.getUserId());
      user.setServiceStatus(NuriCode.US000.name()); // default : "??????"
      user.setSmsVerified(Integer.valueOf(BooleanResultType.Y.getMsg())); // ???????????? ????????? ?????? ?????????????????? ??????
      user.setEmailVerified(Integer.valueOf(BooleanResultType.N.getMsg()));

      UserResult result = userService.registerUser(user);
      if (result == null) {
        log.error("[?????? ??????] ?????? - ?????? ?????? : 0");
        throw new OperationException(ResultResCode.US5001.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "??????"));
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
  @Operation(summary = "????????? ?????? ?????? ??????(??????????????? -> ???????????????, ?????????)",
      description = "<b>serviceStatus ??????</b><br>"
              + "- US000(\"??????\")<br>"
              + "- US001(\"????????????\")"
  )
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "????????? ?????????", example = "gildong", in = ParameterIn.PATH)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/admin/users/{userId}/status")
  public ResponseEntity<ResultMessage<String>> modifyUserStatusAdmin(
      @PathVariable("userId") String userId,
      @RequestBody ModifyStatusParam statusParam,
      HttpServletRequest request) {

    log.info("Controller - modifyUserStatusAdmin");
    String serviceStatus = statusParam.getServiceStatus();
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User user = (User) authentication.getPrincipal();

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

    if (userResult.getRoleId().equals(NuriRole.R003.name())) {
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
      modifyParam.put("updaterId", user.getUserId());
      modifyParam.put("updatedDt", updateDt);

      int result = userService.modifyUser(modifyParam);

      if (result > 0) {
        // 3.1 ???????????? Logging
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
            .creatorId(user.getUserId())
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
  @GetMapping("/api/gnd-onm-user/v1.0/admin/users/{userId}")
  public ResponseEntity<ResultMessage<UserReturnResult>> getUserAdmin(
      @PathVariable("userId") String userId) throws Exception {

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
  @PutMapping("/api/gnd-onm-user/v1.0/admin/users/{userId}")
  public ResponseEntity<ResultMessage<String>> modifyUserAdmin(
      @PathVariable("userId") String userId,
      @RequestBody AdminModifyUserParam modifyUserParam,
      HttpServletRequest request) {
    log.info("Controller - modifyUser");
    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User user = (User) authentication.getPrincipal();

    // 2. req ??????.
    if (modifyUserParam == null) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.INVALID_PARAM_ERROR.name(), "modifyUserParam"));
    }

    UserResult userResult = userService.checkUserId(userId);
    if (userResult == null) {
      log.error("[????????? ?????? ??????] ?????? - ????????? ??????");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 3. ???????????? ??????
      HashMap<String, Object> modifyParam = new HashMap<>();
      JSONObject beforeJson = new JSONObject();
      JSONObject afterJson = new JSONObject();
      if (updateValidation(userResult.getUserNm(), modifyUserParam.getUserNm())) {
        modifyParam.put("userNm", modifyUserParam.getUserNm());
        beforeJson.put("userNm", userResult.getUserNm());
        afterJson.put("userNm", modifyUserParam.getUserNm());
      }
      if (updateValidation(userResult.getCellphone(), modifyUserParam.getCellphone())) {
        modifyParam.put("cellphone", modifyUserParam.getCellphone());
        beforeJson.put("cellphone", userResult.getCellphone());
        afterJson.put("cellphone", modifyUserParam.getCellphone());
      }
      if (updateValidation(userResult.getEmail(), modifyUserParam.getEmail())) {
        modifyParam.put("email", modifyUserParam.getEmail());
        beforeJson.put("email", userResult.getEmail());
        afterJson.put("email", modifyUserParam.getEmail());
      }
      if (updateValidation(userResult.getServiceStatus(), modifyUserParam.getServiceStatus())) {
        modifyParam.put("serviceStatus", modifyUserParam.getServiceStatus());
        beforeJson.put("serviceStatus", userResult.getServiceStatus());
        afterJson.put("serviceStatus", modifyUserParam.getServiceStatus());
      }

      Date updateDt = DateUtil.getCurrentDateTime();
      modifyParam.put("userId", userId);
      modifyParam.put("updaterId", user.getUserId());
      modifyParam.put("updatedDt", updateDt);
      int result = userService.modifyUser(modifyParam);

      // 3.1 ???????????? Logging
      if (result > 0) {
        RegisterAuditLogParam auditLogParam = RegisterAuditLogParam.builder()
            .userSeq(userResult.getUserSeq())
            .userId(userResult.getUserId())
            .userNm(userResult.getUserNm())
            .act(UserAuditLogAct.UPDATED.name())
            .updateItem(UserAuditLogUpdateItemCode.USERINFO.name())
            .beforeValue(beforeJson.toString())
            .afterValue(afterJson.toString())
            .ip(request.getRemoteAddr())
            .creatorId(user.getUserId())
            .createdDt(updateDt).build();
        int auditLogResult = userAuditLogService.insertUserAuditLog(auditLogParam);
        if (auditLogResult == 0) {
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
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[????????? ?????? ??????] ?????? ", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), userId));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "???????????? ???????????? ?????????")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "userId", description = "????????? ?????????", example = "hong", in = ParameterIn.PATH)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/admin/users/{userId}/password/init")
  public ResponseEntity<ResultMessage<String>> initPasswordAdmin(
      @PathVariable("userId") String userId,
      HttpServletRequest request)
      throws Exception {

    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User user = (User) authentication.getPrincipal();

    //  2. req ??????.
    UserResult userResult = userService.checkUserId(userId);
    if (userResult == null) {
      log.error("[???????????? ?????????] ?????? - ????????? ??????");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
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
      modifyParam.put("updaterId", user.getUserId());
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
            .creatorId(user.getUserId())
            .createdDt(updateDt).build();
        int auditLogResult = userAuditLogService.insertUserAuditLog(auditLogParam);
        if (auditLogResult == 0) {
          log.error("[???????????? ?????????] ???????????? ?????? ?????? - ???????????? : 0");
          throw new OperationException(ResultResCode.US5003.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "???????????? ????????? ??????"));
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

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "????????? ?????? ??????(Excel)",
      description =
          "<b>loginStatus ??????</b><br>"
              + "- LG003(\"????????? ??????\")<br>"
              + "- LG004(\"???????????? ??????\")<br>"
              + "- LG005(\"????????? ??????\")<br>"
              + "- LG006(\"????????? ?????? ??????\")<br>"
              + "- LG007(\"????????? ?????? ?????? ??????\")<br>"
              + "- LG008(\"????????? ???????????? ??????\")<br><br>"
              + "<b>orders ?????? ??????</b>(???????????? ??????)<br>"
              + "- loginStatus<br>"
              + "- createdDt<br>"
              + "- userId<br>"
              + "- userNm<br>"
              + "- compNm")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
  })
  @GetMapping("/api/gnd-onm-user/v1.0/admin/login/history/excel")
  public ResponseEntity<ResultMessage<AdminLoginHistoryGridExcelResult>> getLoginHistoryExcel(
      @ParameterObject LoginHistoryParam loginHistoryParam)
      throws Exception {

    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    // 2. req ??????.
    if (StringUtils.isEmpty(loginHistoryParam.getRoleId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "roleId"));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 3. Make Param
      if (!StringUtils.isEmpty(loginHistoryParam.getOrders())) {
        loginHistoryParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(loginHistoryParam.getOrderMap(),
            "loginStatus", "createdDt", "userId", "userNm", "compNm");
        //
        orderByMapList.forEach(elem -> {
          switch (elem.getSort()) {
            case "login_status":
            case "created_dt":
            case "user_id":
            case "user_nm":
              elem.setSort("log." + elem.getSort());
              break;
            case "comp_nm":
              elem.setSort("user." + elem.getSort());
              break;
          }
        });
        loginHistoryParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      loginHistoryParam.setOffset(null);
      loginHistoryParam.setLimit(null);
      HashMap<String, Object> param = mapper.convertValue(loginHistoryParam, HashMap.class);

      // 4. toLoginService
      ResponseEntity<String> response = toLoginService(param, "/login/history", HttpMethod.POST);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ExternalServerException(ResultResCode.US5090.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
      }

      // 5. process response
      Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
      ResultMessage<LinkedTreeMap> resultMessage = gson
          .fromJson(response.getBody(), ResultMessage.class);
      PagingGridResult<LoginHistoryResult> gridResult = gson
          .fromJson(gson.toJson(resultMessage.getPayload()), PagingGridResult.class);

      // 6. Make Excel
      Workbook workbook = new HSSFWorkbook();

      Sheet sheet = workbook.createSheet("LoginLog List");
      Row row = sheet.createRow(0);
      int cellIdx = 0;
      row.createCell(cellIdx++).setCellValue("??????");
      row.createCell(cellIdx++).setCellValue("?????????");
      row.createCell(cellIdx++).setCellValue("??????");
      if (!loginHistoryParam.getRoleId().equals(NuriRole.R001.name())) {
        row.createCell(cellIdx++).setCellValue("?????????");
      }
      row.createCell(cellIdx++).setCellValue("??????");
      row.createCell(cellIdx++).setCellValue("??????");

      for (int i = 0; i < gridResult.getData().size(); i++) {
        row = sheet.createRow(i + 1);
        LoginHistoryResult elem = gson
            .fromJson(gson.toJson(gridResult.getData().get(i)), LoginHistoryResult.class);
        cellIdx = 0;
        row.createCell(cellIdx++).setCellValue(gridResult.getData().size() - i);
        row.createCell(cellIdx++).setCellValue(elem.getUserId());
        row.createCell(cellIdx++).setCellValue(elem.getUserNm());
        if (!loginHistoryParam.getRoleId().equals(NuriRole.R001.name())) {
          row.createCell(cellIdx++).setCellValue(elem.getCompNm());
        }
        row.createCell(cellIdx++).setCellValue(NuriCode.getCodeList("LGS").stream()
            .filter(c -> c.name().equals(elem.getLoginStatus()))
            .findAny().get().getKoMsg());
        row.createCell(cellIdx++).setCellValue(elem.getCreatedDt().toString());
      }

      String fileName = NuriRole.getRoleNameByCode(loginHistoryParam.getRoleId()).toUpperCase()
          + "_LoginLogList_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".xls";
      File rolePath = new File(NuriRole.R001.getRoleName());
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
      AdminLoginHistoryGridExcelResult result = new AdminLoginHistoryGridExcelResult();
      result.setCallbackURL("/api/gnd-onm-user/v1.0/admin/excel/download/" + fileName);

      // 7. Result
      message.setPayload(result);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[????????? ??????(??????) ??????] ??????", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "????????? ??????(??????)"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "????????? ?????? ??????",
      description =
          "<b>loginStatus ??????</b><br>"
              + "- LG003(\"????????? ??????\")<br>"
              + "- LG004(\"???????????? ??????\")<br>"
              + "- LG005(\"????????? ??????\")<br>"
              + "- LG006(\"????????? ?????? ??????\")<br>"
              + "- LG007(\"????????? ?????? ?????? ??????\")<br>"
              + "- LG008(\"????????? ???????????? ??????\")<br><br>"
              + "<b>orders ?????? ??????</b>(???????????? ??????)<br>"
              + "- loginStatus<br>"
              + "- createdDt<br>"
              + "- userId<br>"
              + "- userNm<br>"
              + "- compNm")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/admin/login/history")
  public ResponseEntity<ResultMessage<PagingGridResult<LoginHistoryResult>>> getLoginHistory(
      @ParameterObject LoginHistoryParam loginHistoryParam)
      throws Exception {

    // 1. access_token ??????
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    // 2. req ??????.
    if (StringUtils.isEmpty(loginHistoryParam.getRoleId())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "roleId"));
    }

    ResultMessage message = new ResultMessage();

    try {
      // 3. Make Param
      if (!StringUtils.isEmpty(loginHistoryParam.getOrders())) {
        loginHistoryParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(loginHistoryParam.getOrderMap(),
            "loginStatus", "createdDt", "userId", "userNm", "compNm");
        //
        orderByMapList.forEach(elem -> {
          switch (elem.getSort()) {
            case "login_status":
            case "created_dt":
            case "user_id":
            case "user_nm":
              elem.setSort("log." + elem.getSort());
              break;
            case "comp_nm":
              elem.setSort("user." + elem.getSort());
              break;
          }
        });
        loginHistoryParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> param = mapper.convertValue(loginHistoryParam, HashMap.class);

      // 4. toLoginService
      ResponseEntity<String> response = toLoginService(param, "/login/history", HttpMethod.POST);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ExternalServerException(ResultResCode.US5090.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
      }

      // 5. process response
      Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
      ResultMessage<LinkedTreeMap> resultMessage = gson
          .fromJson(response.getBody(), ResultMessage.class);
      PagingGridResult<LinkedTreeMap> gridResult = gson
          .fromJson(gson.toJson(resultMessage.getPayload()), PagingGridResult.class);
      gridResult.getData()
          .forEach(elem -> elem.put("loginStatus", NuriCode.getCodeList("LGS").stream()
              .filter(c -> c.name().equals(elem.get("loginStatus")))
              .findAny().get().getKoMsg())
          );

      // 6. Result
      message.setPayload(gridResult);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
    } catch (Exception e) {
      log.error("[????????? ?????? ??????] ??????", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "????????? ??????"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  private ResponseEntity<String> toLoginService(HashMap<String, Object> param, String url,
      HttpMethod method)
      throws Exception {
    JSONObject paramdata = new JSONObject();
    if (method.equals(HttpMethod.GET)) {
      List<NameValuePair> paramList = Lists.newArrayList();
      param.forEach((k, v) -> {
        if (v != null) {
          paramList.add(new BasicNameValuePair(k, v.toString()));
        }
      });
      url += "?" + URLEncodedUtils.format(paramList, "UTF-8");
    } else {
      paramdata.putAll(param);
    }

    JSONObject sendData = new JSONObject();
    sendData.put("url", loginServiceUrl + url);
    sendData.put("method", method);
    sendData.put("content-type", "application/json");
    if (!paramdata.isEmpty()) {
      sendData.put("params", paramdata);
    }

    return RestUtil.sendRestApi(sendData);
  }

}
