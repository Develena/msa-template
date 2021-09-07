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


import com.msa.template.elena.entity.page.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "업체 페이지 조회 (params)")
public class CompanyPageParam extends Pagination {

  @Schema(description = "업체 생성일시 시작 검색필드 ", example = "1970-01-01 00:00:00")
  private String startCreateDt;
  @Schema(description = "업체 생성일시 종료 검색필드 ", example = "2021-01-01 00:00:00")
  private String endCreateDt;

}