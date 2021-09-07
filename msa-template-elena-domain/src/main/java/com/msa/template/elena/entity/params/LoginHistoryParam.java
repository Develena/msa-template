/*
 * UserParam.java
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

package com.msa.template.elena.entity.params;


import com.msa.template.elena.entity.page.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "로그인 내역 조회")
public class LoginHistoryParam extends Pagination {

  @Schema(description = "권한아이디", example = "R001", required = true)
  private String roleId;
  @Schema(description = "시작일시", example = "1970-01-01")
  private String startDt;
  @Schema(description = "종료일시", example = "2022-01-01")
  private String endDt;
  @Schema(description = "상태", example = "LG003")
  private String loginStatus;
  @Schema(description = "업체관리키", example = "1", nullable = true)
  private Integer compSeq;
}
