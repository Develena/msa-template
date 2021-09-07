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
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "업체 정보")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyReturnResult {

  @Schema(description = "업체 관리키", example = "1")
  Integer compSeq;
  @Schema(description = "업체 이름", example = "누리플렉스")
  String compNm;
  @Schema(description = "법인등록번호", example = "1234-56-789asbc d")
  String corpNo;
  @Schema(description = "사업자번호", example = "123-45-6789a")
  String bizRegNo;
  @Schema(description = "업종", example = "정보통신")
  String industry;
  @Schema(description = "주소", example = "서울특별시 서초구 사평대로 16")
  String compAddr;
  @Schema(description = "업체 대표 번호", example = "02-1234-5678")
  String compTelephone;
  @Schema(description = "담당자", example = "김누리")
  String manager;
  @Schema(description = "담당자 전화번호", example = "010-1234-5678")
  String managerTelephone;
  @Schema(description = "등록 일자", example = "2021-03-16", format ="yyyy-MM-dd" )
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  Date createdDt;
}
