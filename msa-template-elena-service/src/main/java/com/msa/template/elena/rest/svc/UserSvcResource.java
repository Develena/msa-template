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
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserRoleRes;
import com.nuri.green.onm.user.entity.results.UserTokenRes;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.spec.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class UserSvcResource extends AbstractBaseResource {

  @Autowired
  private UserService userService;

  @Value("${project.code}")
  private String svcName;

  @GetMapping("/svc/gnd-onm-user/v1.0/users/{userId}/token")
  public ResponseEntity<ResultMessage<UserTokenRes>> getUserTokenInfo(
      @PathVariable("userId") String userId) throws Exception {

    UserTokenRes result = userService.getUserTokenInfo(userId);

    if (result == null) {
      log.error("[토큰 정보 조회] 실패 - 사용자 없음");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    ResultMessage message = new ResultMessage();
    message.setPayload(result);
    message.setCode(svcName + ResultResCode.US2000.getCode());
    message.setMessage(ResultResCode.US2000.getKoMsg());

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @GetMapping("/svc/gnd-onm-user/v1.0/users/{userId}/role")
  public ResponseEntity<ResultMessage<UserRoleRes>> getUserRoleInfo(
      @PathVariable("userId") String userId) {
    log.info("Controller - getUserRoleInfo");
    UserRoleRes result = userService.getUserRoleInfo(userId);

    if (result == null) {
      log.error("[사용자 권한 정보 조회] 실패 - 사용자 없음");
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), userId));
    }

    ResultMessage message = new ResultMessage();
    message.setPayload(result);
    message.setCode(svcName + ResultResCode.US2000.getCode());
    message.setMessage(ResultResCode.US2000.getKoMsg());

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}
