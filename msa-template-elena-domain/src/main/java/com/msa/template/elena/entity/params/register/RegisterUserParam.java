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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuri.utils.DateUtil;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@Getter
@Setter
public class RegisterUserParam {

  private String userId;
  private String userNm;
  private String passwd;
  private String telephone;
  private String cellphone;
  private String email;
  private String birthday;

  private String gender;
  private String zipcode;
  private String addr;
  private String addr2;
  private String serviceStatus;
  private String userType;
  private String joinType;
  private String inactiveDt;
  private String creatorId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date createdDt;

  private String compNm;
  private Integer compSeq;

  private String termsAgree;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date termsAgreeDt;
  private Integer smsYn;
  private Integer smsVerified;
  private Integer emailYn;
  private Integer emailVerified;
  private Integer mobileUseYn;

  private String roleId;


  public RegisterUserParam toEntity() {
    return RegisterUserParam.builder()
        .userId(this.userId)
        .userNm(this.userNm)
        .passwd(new BCryptPasswordEncoder().encode(this.passwd))
        .telephone(this.telephone)
        .cellphone(this.cellphone)
        .email(this.email)
        .birthday(this.birthday)
        .gender(this.gender)
        .zipcode(this.zipcode)
        .addr(this.addr)
        .addr2(this.addr2)
        .serviceStatus(this.serviceStatus)
        .userType(this.userType)
        .joinType(this.joinType)
        .inactiveDt(this.inactiveDt)
        .creatorId(this.creatorId)
        .roleId(this.roleId)
        .createdDt(DateUtil.getCurrentDateTime())
        .build();
  }


}
