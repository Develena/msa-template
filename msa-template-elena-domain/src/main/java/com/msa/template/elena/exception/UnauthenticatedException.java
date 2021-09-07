/*
 * UnknownException.java
 *
 *
 * 21. 3. 15. 오전 11:18
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

package com.msa.template.elena.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthenticatedException extends AbstractBaseException {

  private static final long serialVersionUID = 1L;

  @Getter
  private String code;

  public UnauthenticatedException() {
    super();
  }

  public UnauthenticatedException(Throwable e) {
    super(e);
  }

  public UnauthenticatedException(String code, String message) {
    super(message);
    this.code = code;
  }

  public UnauthenticatedException(String message, Throwable e) {
    super(message, e);
  }

  public UnauthenticatedException(String message, Object... args) {
    super(String.format(message, args));
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.UNAUTHORIZED;
  }

}
