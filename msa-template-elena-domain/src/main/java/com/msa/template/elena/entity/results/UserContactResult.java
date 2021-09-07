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


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserContactResult {

  @Schema(description = "사용자 관리키", example = "142")
  private Integer userSeq;
  @Schema(description = "사용자 아이디", example = "hong")
  private String userId;
  @Schema(description = "사용자 이름", example = "홍길동")
  private String userNm;
  @Schema(description = "전화번호", example = "01012345678")
  private String telephone;
  @Schema(description = "휴대번호", example = "01012345678")
  private String cellphone;
  @Schema(description = "이메일", example = "")
  private String email;

}
