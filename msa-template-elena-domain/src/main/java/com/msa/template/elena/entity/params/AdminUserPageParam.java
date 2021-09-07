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
@Schema(description = "사용자 페이지 조회 (params)")
public class AdminUserPageParam extends Pagination {

  @Schema(description = "사용자 권한 탭", example = "R001", required = true)
  private String roleId;
  @Schema(description = "사용자 상태 검색필드", example = "US000")
  private String serviceStatus;
  @Schema(description = "사용자 가입일시 시작 검색필드 ", example = "1970-01-01")
  private String startCreateDt;
  @Schema(description = "사용자 가입일시 종료 검색필드 ", example = "2022-01-01")
  private String endCreateDt;
  @Schema(description = "사용자 업체 검색필드", example = "1")
  private Integer compSeq;

}