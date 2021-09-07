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

import com.msa.template.elena.rest.AbstractBaseResource;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.params.modify.ModifyAppInfoParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyDevicePushTokenParam;
import com.nuri.green.onm.user.entity.params.register.RegisterDeviceParam;
import com.nuri.green.onm.user.entity.results.AppInfoResult;
import com.nuri.green.onm.user.entity.results.DeviceResult;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.exception.UnknownException;
import com.nuri.green.onm.user.spec.DeviceService;
import com.nuri.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "DeviceApiResource", description = "Device Service API")
@RestController
public class DeviceApiResource extends AbstractBaseResource {

  @Value("${project.code}")
  private String svcName;

  @Autowired
  private DeviceService deviceService;

  @SecurityRequirements // remove security
  @Operation(summary = "디바이스 정보 등록")
  @PostMapping("/api/gnd-onm-user/v1.0/devices")
  public ResponseEntity<ResultMessage<String>> registerDeviceInfo(
      @RequestBody RegisterDeviceParam registerDeviceParam) throws Exception {

    if (StringUtils.isEmpty(registerDeviceParam.getUdid())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "UDID"));
    }
    // UDID 유무 체크
    DeviceResult deviceInfo = deviceService.getDeviceInfo(registerDeviceParam.getUdid());
    if (deviceInfo != null) {
      throw new OperationException(ResultResCode.US5005.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.DUPLICATED_ERROR.name(), "UDID"));
    }

    registerDeviceParam.setCreatedDt(DateUtil.getCurrentDateTime());
    registerDeviceParam.setCreatorId("app");

    ResultMessage message = new ResultMessage();

    try {
      int i = deviceService.registerDeviceInfo(registerDeviceParam);
      // 4. ResultMessage 생성
      if (i == 1) {
        message.setPayload(BooleanResultType.SUCCESS.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[디바이스 등록]실패 - 쿼리결과 : 0");
        throw new OperationException(ResultResCode.US5001.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "디바이스"));
      }
    } catch (Exception e) {
      log.error("[디바이스 등록]실패", e);
      throw new OperationException(ResultResCode.US5001.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "디바이스"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirements // remove security
  @Operation(summary = "디바이스 PushToken 업데이트")
  @PutMapping("/api/gnd-onm-user/v1.0/devices")
  public ResponseEntity<ResultMessage<String>> modifyDevicePushToken(
      @RequestBody ModifyDevicePushTokenParam modifyDevicePushTokenParam) throws Exception {

    if (StringUtils.isEmpty(modifyDevicePushTokenParam.getUdid())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "UDID"));
    }
    // UDID 유무 체크
    DeviceResult deviceInfo = deviceService.getDeviceInfo(modifyDevicePushTokenParam.getUdid());
    if (deviceInfo == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(),
              modifyDevicePushTokenParam.getUdid()));
    }

    modifyDevicePushTokenParam.setUpdatedDt(DateUtil.getCurrentDateTime());
    modifyDevicePushTokenParam.setUpdaterId("app");

    ResultMessage message = new ResultMessage();

    try {
      int i = deviceService.modifyDevicePushToken(modifyDevicePushTokenParam);

      // 4. ResultMessage 생성
      if (i == 1) {
        message.setPayload(BooleanResultType.SUCCESS.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[디바이스 수정] 실패 - 쿼리결과 : 0");
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "디바이스"));
      }
    } catch (Exception e) {
      log.error("[디바이스 수정] 실패", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "디바이스"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirements // remove security
  @Operation(summary = "앱 정보 조회")
  @Parameters(value = {
      @Parameter(name = "os", description = "앱OS", example = "IOS",in = ParameterIn.QUERY)
//      @Parameter(name = "appId", description = "앱아이디", example = "***", in = ParameterIn.QUERY),
//      @Parameter(name = "appNm", description = "앱이름", example = "GndOnm", in = ParameterIn.QUERY),
      ,@Parameter(name = "appVersion", description = "앱버전", example = "1.0.0", in = ParameterIn.QUERY)
      ,@Parameter(name = "inApproval", description = "심사상태", example = "true", schema = @Schema(type = "boolean"), in = ParameterIn.QUERY)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/devices/app")
  public ResponseEntity<ResultMessage<List<AppInfoResult>>> getAppInfo(HttpServletRequest request) throws Exception {

    ResultMessage message = new ResultMessage();

    try {
      Map<String, Object> param = new HashMap<String, Object>();
      if(!StringUtils.isEmpty(request.getParameter("os"))){
        param.put("os", request.getParameter("os").toLowerCase());
      }
      if(!StringUtils.isEmpty(request.getParameter("appVersion"))){
        param.put("appVersion", request.getParameter("appVersion"));
      }
      if(!StringUtils.isEmpty(request.getParameter("inApproval"))){
         param.put("inApproval", Boolean.parseBoolean(request.getParameter("inApproval")));
      }

      List<AppInfoResult> results = deviceService.getAppInfo(param);

      // 4. ResultMessage 생성
      message.setPayload(results);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[앱 정보 조회] 실패", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "앱 정보"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirements // remove security
  @Operation(summary = "앱 정보 수정")
  @Parameters(value = {
      @Parameter(name = "appId", description = "앱아이디", example = "***", in = ParameterIn.PATH)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/devices/app/{appId}")
  public ResponseEntity<ResultMessage<List<String>>> updateAppInfo(@PathVariable String appId,
      @RequestBody ModifyAppInfoParam body) throws Exception {

    ResultMessage message = new ResultMessage();

    if(body == null) {
       throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "body"));
    }

    try {
      Map<String, Object> param = new HashMap<String, Object>();
      param.put("appId", appId);
      param.put("os", body.getOs());
      param.put("appVersion", body.getAppVersion());
      param.put("inApproval", body.isInApproval());
      param.put("updaterId", "develop");
      param.put("updatedDt", DateUtil.getCurrentDateTime());

      System.out.println("--------------- approval : " + body.isInApproval());

      int results = deviceService.modifyAppInfo(param);

      // 4. ResultMessage 생성
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[앱 정보 수정] 실패", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "앱 정보"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }


}
