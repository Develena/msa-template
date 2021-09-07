/*
 * ResponseMessage.java
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
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Api Response")
@Getter
@Setter
public class ResultMessage<T> {

  @Schema(description = "응답코드", example = "US-2000")
  private String code;
  @Schema(description = "응답메세지", example = "성공")
  private String message;
  @Schema(description = "응답데이터")
  private T payload;
  @Schema(description = "응답시간", example = "2021-03-16'T'08:35:22", format ="yyyy-MM-dd'T'HH:mm:ss" )
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date when;

  public ResultMessage() {
    this.when = new Date();
  }

  public ResultMessage(T payload) {
    this.when = new Date();
    this.payload = payload;
  }

}
