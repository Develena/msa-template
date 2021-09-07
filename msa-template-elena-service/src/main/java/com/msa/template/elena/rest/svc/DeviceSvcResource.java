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

package com.msa.template.elena.rest.svc;

import com.msa.template.elena.rest.AbstractBaseResource;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.params.modify.ModifyDeviceParam;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserResult;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.spec.DeviceService;
import com.nuri.green.onm.user.spec.UserService;
import com.nuri.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class DeviceSvcResource extends AbstractBaseResource {

  @Autowired
  private UserService userService;

  @Autowired
  private DeviceService deviceService;

  @Value("${project.code}")
  private String svcName;

  /*@SecurityRequirement(name = "access_token")*/
  @Operation(summary = "디바이스 정보 변경 (사용자와 매핑)")
  @Parameters(value = {
      @Parameter(name = "userId", description = "사용자 아이디", example = "gildong", in = ParameterIn.PATH)
  })
  @PutMapping("/svc/gnd-onm-user/v1.0/users/{userId}/devices")
  public ResponseEntity<ResultMessage<String>> modifyDeviceInfo(
      @PathVariable("userId") String userId, @RequestBody ModifyDeviceParam modifyDeviceParam)
      throws Exception {

    // 1. access_token 확인
    /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new AuthorizedFailException(ExceptionMessage
          .makeExceptionMessage(ResultErrorCode.TOKEN_INVALID_ERROR.name(), "access_token"));
    }*/
    //User user = (User) authentication.getPrincipal();

    // 2. req 확인.
    if (modifyDeviceParam == null || modifyDeviceParam.getUdid() == null || modifyDeviceParam
        .getUdid().isEmpty()) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "UDID"));
    }
    if (userId == null || userId.isEmpty()) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "userId"));
    }
    UserResult userResult = userService.checkUserId(userId);
    if (userResult == null) {
      log.error("[장비 정보 변경] 실패 - 사용자 없음");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    } else {
      modifyDeviceParam.setUserSeq(userResult.getUserSeq());
    }

    modifyDeviceParam.setUpdatedDt(DateUtil.getCurrentDateTime());
    modifyDeviceParam.setUpdaterId("app");
    int i = deviceService.modifyDeviceInfo(modifyDeviceParam);

    // 4. ResultMessage 생성
    ResultMessage message = new ResultMessage();
//    if (i == 1) {
      message.setPayload(BooleanResultType.SUCCESS.getMsg());
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());
//    } else {
//      log.error("[디바이스 정보 변경] 실패 - 쿼리결과 : 0");
//      throw new OperationException(ResultResCode.US5003.getCode(),
//          ExceptionMessage
//              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "디바이스"));
//    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}
