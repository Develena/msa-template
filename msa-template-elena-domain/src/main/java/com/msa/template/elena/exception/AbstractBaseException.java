/*
 * AbstractBaseException.java
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

package com.msa.template.elena.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractBaseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AbstractBaseException() {
    super();
  }

  public AbstractBaseException(String msg) {
    super(msg);
  }

  public AbstractBaseException(Throwable e) {
    super(e);
  }

  public AbstractBaseException(String errorMessge, Throwable e) {
    super(errorMessge, e);
  }

  public abstract HttpStatus getHttpStatus();
}
