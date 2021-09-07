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
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Schema
public class FindUserIdResult {

  @Schema(description = "사용자 아이디", example = "hong")
  private String userId;
  @Schema(description = "사용자 이름", example = "홍길동")
  private String userName;
  @Schema(description = "전화번호", example = "0212345678")
  private String telephone;
  @Schema(description = "휴대번호", example = "01012345678")
  private String cellphone;
  @Schema(description = "이메일", example = "hong@nuriflex.co.kr")
  private String email;
  @Schema(description = "생성날짜", example = "2021-03-16'T'08:35:22")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date createdDt;

}
