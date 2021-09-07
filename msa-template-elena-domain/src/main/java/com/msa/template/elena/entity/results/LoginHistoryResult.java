/*
 * UserResult.java
 *
 *
 * 21. 3. 15. 오전 11:17
 *
 *
 * Copyright (c) 2021 NURITELECOM, Inc.
 * All rights reserved.
 *
 *
 * This software is the confidential and proprietary information of NURITELECOM, Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURITELECOM, Inc.
 *
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */

package com.msa.template.elena.entity.results;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Schema(description = "로그인 내역")
public class LoginHistoryResult {

  @Schema(name = "loginLogSeq", description = "로그인 로그 관리키")
  private Integer loginLogSeq;
  @Schema(name = "userSeq", description = "사용자 관리키")
  private Integer userSeq;
  @Schema(name = "createdDt", description = "생성일시")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
  private Date createdDt;
  @Schema(name = "userId", description = "사용자 아이디")
  private String userId;
  @Schema(name = "userNm", description = "사용자 이름")
  private String userNm;
  @Schema(name = "loginStatus", description = "로그인 상태")
  private String loginStatus;
  @Schema(name = "compNm", description = "업체명")
  private String compNm;

}
