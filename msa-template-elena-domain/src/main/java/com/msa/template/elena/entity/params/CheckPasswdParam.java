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

package com.msa.template.elena.entity.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사용자 비밀번호 확인")
@Getter
@Setter
public class CheckPasswdParam {

  @Schema(description = "사용자 아이디", example = "hyunhee", required = true)
  private String userId;
  @Schema(description = "사용자 비밀번호", example = "password", required = true)
  private String password;

}
