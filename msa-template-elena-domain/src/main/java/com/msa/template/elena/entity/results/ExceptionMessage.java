/*
 * ExceptionMessage.java
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
import com.msa.template.elena.exception.AbstractBaseException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Date;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Schema(name = "Error", description = "Api Error Response")
@Getter
@Setter
@ToString
@Slf4j
public class ExceptionMessage {

  @Schema(description = "에러코드", example = "")
  private String code;
  @Schema(description = "에러메세지", example = "")
  private String message;
  @Schema(description = "에러메세지 상세", example = "")
  private String detail;
  @Schema(description = "응답시간", example = "2021-03-16'T'08:35:22", format = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date when;

  public ExceptionMessage() {
    this(HttpStatus.OK);
  }

  public ExceptionMessage(HttpStatus httpStatus) {
    this.when = new Date();
  }

  public ExceptionMessage(AbstractBaseException ex) {
    HttpStatus httpStatus = ex.getHttpStatus();
    this.detail = ex.getMessage();
    this.when = new Date();
  }

  public static String makeExceptionMessage(String errorCode, String... params) {

    StringJoiner sj = new StringJoiner(",");
    Arrays.stream(params).forEach(s -> sj.add(s));
    return errorCode + ":" + sj.toString();

  }

}
