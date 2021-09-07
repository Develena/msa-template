/*
 * ResponseErrorCode.java
 *
 *
 * 15/06/21, 02:00 PM
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
 */

package com.msa.template.elena.entity.enums;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import lombok.AllArgsConstructor;

/**
 * 해당 클래스는 서비스의 정상/에러 응답 코드를 작성합니다.
 */
@AllArgsConstructor
public enum UserAuditLogAct {

  CREATED,
  UPDATED,
  DELTED;

  private static final Map<String, UserAuditLogAct> lookup = Maps.uniqueIndex(
      Arrays.asList(UserAuditLogAct.values()),
      UserAuditLogAct::name
  );


  public static UserAuditLogAct get(String code) {
    return lookup.get(code);
  }
}
