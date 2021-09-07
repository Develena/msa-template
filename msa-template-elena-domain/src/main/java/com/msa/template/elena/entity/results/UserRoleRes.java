/*
 * UserRoleInfo.java
 *
 * 15/06/21, 02:00 PM
 *
 * Copyright (c) 2021 NURIFLEX, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of NURIFLEX, Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURIFLEX, Inc.
 *
 * For more information on this product, please see
 * http://www.nuriflex.co.kr
 *
 */

package com.msa.template.elena.entity.results;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(Include.NON_NULL)
@Schema
public class UserRoleRes {

  @Schema(description = "사용자명", example = "홍길동")
  private String userNm;
  @Schema(description = "권한아이디", example = "ADMIN")
  private String roleId;
  @Schema(description = "권한명", example = "관리자")
  private String roleNm;
}
