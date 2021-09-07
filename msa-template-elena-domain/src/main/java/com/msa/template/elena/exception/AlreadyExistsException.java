///*
// * AlreadyExistsException.java
// *
// *
// * 21. 3. 15. 오전 11:18
// *
// *
// * Copyright (c) 2021 NURIFLEX, Inc.
// * All rights reserved.
// *
// *
// * This software is the confidential and proprietary information of NURIFLEX, Inc. ("Confidential Information").
// * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURIFLEX, Inc.
// *
// *
// * For more information on this product, please see
// * http://www.nuritelecom.co.kr
// *
// */
//
//package com.nuri.green.onm.user.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//public class AlreadyExistsException extends AbstractBaseException {
//
//  private static final long serialVersionUID = 1L;
//
//  public AlreadyExistsException() {
//    super();
//  }
//
//  public AlreadyExistsException(Throwable e) {
//    super(e);
//  }
//
//  public AlreadyExistsException(String message) {
//    super(message);
//  }
//
//  public AlreadyExistsException(String message, Throwable e) {
//    super(message, e);
//  }
//
//  public AlreadyExistsException(String message, Object... args) {
//    super(String.format(message, args));
//  }
//
//  @Override
//  public HttpStatus getHttpStatus() {
//    return HttpStatus.INTERNAL_SERVER_ERROR;
//  }
//}
