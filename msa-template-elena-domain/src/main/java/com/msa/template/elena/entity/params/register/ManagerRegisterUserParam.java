/*
 * UserPostBody.java
 *
 *
 * 21. 3. 18. 오후 3:56
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

package com.msa.template.elena.entity.params.register;

import com.nuri.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Schema(description = "사용자 등록")
@Getter
@Setter
public class ManagerRegisterUserParam {

  @Schema(description = "사용자 아이디", example = "yourId", required = true, maxLength = 60)
  private String userId;
  @Schema(description = "사용자 이름", example = "홍길동", required = true, maxLength = 30)
  private String userName;
  @Schema(description = "비밀번호", required = true)
  private String password;
  @Schema(description = "휴대번호", example = "01012345678", required = true)
  private String cellphone;
  @Schema(description = "이메일", example = "gildong@nuriflex.co.kr", required = true)
  private String email;
  @Schema(description = "생년월일", example = "19900101", required = true)
  private String birthday;

  public RegisterUserParam toEntity(String creatorId) {
    return RegisterUserParam.builder()
        .userId(this.userId)
        .userNm(this.userName)
        .passwd(this.password)
        .cellphone(this.cellphone)
        .email(this.email)
        .birthday(this.birthday)
        .createdDt(DateUtil.getCurrentDateTime())
        .creatorId(creatorId)
        .build();
  }


}
