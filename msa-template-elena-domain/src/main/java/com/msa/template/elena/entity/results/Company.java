/*
 * UserCompany.java
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

package com.msa.template.elena.entity.results;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(Include.NON_NULL)
public class Company {
  private Integer compSeq;
  private String compNm;
  private String ceoNm;
  private String bizRegNo;
  private String corpNo;
  private String manager;
  private String managerTelephone;
  private String industry;
  private String bizcnd;
  private String compZipcode;
  private String compAddr;
  private String compAddr2;
  private String compTelephone;
  private String compEmail;
  private String compFax;
  private String compUrl;
  private Integer serviceStatus;
  private String parentCompId;
  private String creatorId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date createdDt;
  private String updaterId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date updatedDt;
}
