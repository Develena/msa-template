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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Schema(description = "업체 등록")
@Getter
@Setter
public class RegisterCompanyParam {
  @Schema(description = "업체 이름", example = "누리플렉스", required = true, maxLength = 30)
  String compNm;
  @Schema(description = "법인번호", example = "1234-56-789abc d", required = true)
  String corpNo;
  @Schema(description = "사업자번호", example = "123-45-6789a", required = true)
  String bizRegNo;
  @Schema(description = "업종", example = "정보통신", required = true)
  String industry;
  @Schema(description = "주소", example = "서울특별시 서초구 사평대로 16 누리빌딩", required = true)
  String compAddr;
  @Schema(description = "대표전화번호", example = "02-781-0777", required = true)
  String compTelephone;
  @Schema(description = "담당자명", example = "김누리", required = true)
  String manager;
  @Schema(description = "담당자연락처", example = "010-1234-5678", required = true)
  String managerTelephone;
}
