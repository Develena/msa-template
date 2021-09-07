/*
 * PasswdCheckBody.java
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

package com.msa.template.elena.entity.params.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사용자 비밀번호 확인")
@Getter
@Setter
public class ModifyPasswdParam {

  @Schema(description = "변경할 비밀번호", required = true)
  private String password;

  @Schema(description = "본인인증 받은 수단", example = "sms")
  private String authType;
  @Schema(description = "휴대번호", example = "01012345678")
  private String authInfo;

}
