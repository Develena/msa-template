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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Schema(description = "사용자 등록")
@Getter
@Setter
public class RegisterUserDeviceParam {

  @Schema(description = "사용자 아이디", example = "yourId", required = true, maxLength = 60)
  private String userId;


  @Schema(description = "등록자 아이디", example = "admin")
  private String creatorId;
  @Schema(description = "등록일시", hidden = true)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date createdDt;

}
