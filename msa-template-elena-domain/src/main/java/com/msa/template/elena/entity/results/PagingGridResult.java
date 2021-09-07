/*
 * PagingGridResult.java
 *
 *
 * 15/06/21, 02:00 PM
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
 */

package com.msa.template.elena.entity.results;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "페이징 응답 결과")
@Getter
@Setter
public class PagingGridResult<T> {

  @Schema(description = "결과 데이터", example = "")
  private List<T> data;
  @Schema(description = "결과 데이터의 갯수", example = "")
  private Integer dataCnt;
  @Schema(description = "검색 데이터의 총 개수", example = "")
  private Integer totalCnt;
  @Schema(description = "요청 limit", example = "")
  private Integer limit;
  @Schema(description = "요청 offset", example = "")
  private Integer offset;

}
