/*
 * TokenInfo.java
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
import org.checkerframework.checker.fenum.qual.SwingCompassDirection;
import org.json.simple.JSONObject;

@Getter
@Setter
@ToString
@Builder
@Schema
@JsonInclude(Include.NON_NULL)
public class UserTokenRes {

  @Schema(description = "사용자 권한", example = "ADMIN")
  private String roles;
  @Schema(description = "토큰에 들어가는 기타 정보", example = "{\"email\":\"gildong@nuriflex.co.kr\"}", required = false, type = "object")
  private JSONObject content;
}
