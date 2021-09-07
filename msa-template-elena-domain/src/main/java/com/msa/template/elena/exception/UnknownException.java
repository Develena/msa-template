/*
 * UnknownException.java
 *
 *
 * 21. 3. 15. 오전 11:18
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

package com.msa.template.elena.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UnknownException extends AbstractBaseException {

  private static final long serialVersionUID = 1L;

  @Getter
  private String code;

  public UnknownException() {
    super();
  }

  public UnknownException(Throwable e) {
    super(e);
  }

  public UnknownException(String code, String message) {
    super(message);
    this.code = code;
  }

  public UnknownException(String message, Throwable e) {
    super(message, e);
  }

  public UnknownException(String message, Object... args) {
    super(String.format(message, args));
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

}
