/*
 * UserLogic.java
 *
 *
 * 21. 3. 15. 오전 11:20
 *
 *
 * Copyright (c) 2021 NURIFLEX, Inc.
 * All rights reserved.
 *
 *
 * This software is the confidential and proprietary information of NURIFLEX, Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURIFLEX, Inc.
 *
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */

package com.msa.template.elena.logic;

import com.nuri.green.onm.user.entity.params.register.RegisterAuditLogParam;
import com.nuri.green.onm.user.spec.UserAuditLogService;
import com.nuri.green.onm.user.store.UserAuditLogStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserAuditLogLogic implements UserAuditLogService {

  @Autowired
  private UserAuditLogStore userAuditLogStore;

  @Override
  public int insertUserAuditLog(RegisterAuditLogParam param) {
    return userAuditLogStore.registerUserAuditLog(param);
  }
}
