/*
 * UserResult.java
 *
 *
 * 21. 3. 15. 오전 11:17
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

package com.msa.template.elena.entity.results;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoResult {

  private Integer userSeq;
  private String userId;
  private String userNm;
  private String passwd;
  private String cellphone;
  private String email;
  private String birthday;
  private String serviceStatus;
  private String creatorId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date createdDt;
  private String updaterId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date updatedDt;
  private Integer compSeq;
  private String compNm;
  private String roleId;
  private String roleNm;

  public UserReturnResult toEntity() {
    return UserReturnResult.builder()
        .userSeq(this.userSeq)
        .userId(this.userId)
        .userNm(this.userNm)
        .cellphone(this.cellphone)
        .email(this.email)
        .birthday(this.birthday)
        .serviceStatus(this.serviceStatus)
        .creatorId(this.creatorId)
        .createdDt(this.createdDt)
        .updaterId(this.updaterId)
        .updatedDt(this.updatedDt)
        .compSeq(this.compSeq)
        .compNm(this.compNm)
        .roleId(this.roleId)
        .roleNm(this.roleNm)
        .build();
  }

}
