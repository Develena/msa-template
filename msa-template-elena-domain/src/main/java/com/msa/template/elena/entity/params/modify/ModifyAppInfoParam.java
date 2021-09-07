/*
 * PasswdCheckBody.java
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

package com.msa.template.elena.entity.params.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "앱 정보 변경")
@Getter
@Setter
public class ModifyAppInfoParam {

  @Schema(description ="앱 버전", example = "1.0.0")
  private String appVersion;
  @Schema(description = "OS", example = "IOS")
  private String os;
  @Schema(description = "심사중 상태", example = "true")
  boolean inApproval;

}
