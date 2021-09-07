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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "사용자 페이지 조회 (params)")
public class UserPageParam extends Pagination {

  @Schema(description = "사용자 관리키", example = "", nullable = true)
  private Integer userSeq;
  @Schema(description = "사용자 아이디", example = "")
  private String userId;
  @Schema(description = "사용자 이름", example = "")
  private String userNm;
  @Schema(description = "권한 그룹 명", example = "")
  private String permGrNm;
  @Schema(description = "사용자 영문 이름", example = "")
  private String userEngNm;
  @Schema(description = "권한 그룹 아이디", example = "")
  private String permGrId;
  @Schema(description = "사업자 그룹 아이디", example = "")
  private String bizGrId;
  @Schema(description = "사업자 그룹명", example = "")
  private String bizGrNm;
  @Schema(description = "등록자 아이디", example = "")
  private String creatorId;

}
