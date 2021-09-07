/*
 * UserParam.java
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

package com.msa.template.elena.entity.params;


import com.msa.template.elena.entity.page.CommonParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "사용자 조회 (params)")
public class UserParam extends CommonParam {

  @Schema(description = "사용자 관리키", example = "142", nullable = true)
  private Integer userSeq;
  @Schema(description = "사용자 아이디", example = "hong")
  private String userId;
  @Schema(description = "사용자 이름", example = "홍길동")
  private String userNm;
  @Schema(description = "사용자 전화번호", example = "02-123-4567")
  private String telephone;
  @Schema(description = "사용자 휴대 전화번호", example = "010-1234-5678")
  private String cellphone;
  @Schema(description = "사용자 이메일", example = "sample@nuriflex.co.kr")
  private String email;
  @Schema(description = "사용자 생일", example = "19940418")
  private Integer birthday;
  @Schema(description = "사용자 성별", example = "남")
  private String gender;
  @Schema(description = "사용자 우편번호", example = "06552")
  private String zipcode;
  @Schema(description = "사용자 주소", example = "서울특별시 서초구 사평대로 16")
  private String addr;
  @Schema(description = "사용자 상세주소", example = "7층 기술연구소")
  private String addr2;
  @Schema(description = "서비스 상태", example = "US000")
  private String serviceStatus;
  @Schema(description = "사용자구분")
  private String userType;
  @Schema(description = "가입유형")
  private String joinType;
  @Schema(description = "생성자 아이디", example = "creatorId")
  private String creatorId;
  @Schema(description = "수정자 아이디", example = "updaterId")
  private String updaterId;

}
