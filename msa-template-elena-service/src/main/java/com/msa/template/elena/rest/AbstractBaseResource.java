/*
 * AbstractBaseResource.java
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

package com.msa.template.elena.rest;

import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.page.OrderByMap;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.green.onm.user.exception.ExternalServerException;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.NotFoundException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.exception.UnauthenticatedException;
import com.nuri.green.onm.user.exception.UnknownException;
import com.nuri.utils.TypeConvertUtil;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class AbstractBaseResource {

  
  @Autowired
  private MessageSource messageSource;

  @Value("${project.code}")
  private String svcName;

  // 400
  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ExceptionMessage> invalidRequestException(HttpServletRequest req,
      final InvalidRequestException exception) {

    log.error("InvalidRequestException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US4009.getCode());
       response.setMessage(ResultResCode.US4009.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;

      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.BAD_REQUEST_ERROR.name(), null,
                LocaleContextHolder.getLocale()));
      }
    } else {

      response.setDetail(messageSource
          .getMessage(ResultErrorCode.BAD_REQUEST_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }

  // 401
  @ExceptionHandler(UnauthenticatedException.class)
  public ResponseEntity<ExceptionMessage> authenticationException(HttpServletRequest req,
      final UnauthenticatedException exception) {

    log.error("UnauthenticatedException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US4019.getCode());
      response.setMessage(ResultResCode.US4019.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;

      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.AUTHENTICATION_FAILED_ERROR.name(), null,
                LocaleContextHolder.getLocale()));
      }
    } else {
      response.setDetail(messageSource
          .getMessage(ResultErrorCode.AUTHENTICATION_FAILED_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }


  // 403
  @ExceptionHandler(AuthorizedFailException.class)
  public ResponseEntity<ExceptionMessage> authorizedFailException(HttpServletRequest req,
      final AuthorizedFailException exception) {

    log.error("AuthorizedFailException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US4039.getCode());
      response.setMessage(ResultResCode.US4039.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;

      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.FORBIDDEN_ERROR.name(), null,
                LocaleContextHolder.getLocale()));
      }
    } else {
      response.setDetail(messageSource
          .getMessage(ResultErrorCode.FORBIDDEN_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }

  // 404
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ExceptionMessage> notFoundException(HttpServletRequest req,
      final NotFoundException exception) {

    log.error("NotFoundException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US4040.getCode());
      response.setMessage(ResultResCode.US4040.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;

      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.RESOURCE_NOT_FOUND.name(), null,
                LocaleContextHolder.getLocale()));
      }
    } else {
      response.setDetail(messageSource
          .getMessage(ResultErrorCode.RESOURCE_NOT_FOUND.name(), null,
              LocaleContextHolder.getLocale()));

    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }


  // 500
  @ExceptionHandler(OperationException.class)
  public ResponseEntity<ExceptionMessage> operationException(HttpServletRequest req,
      final OperationException exception) {

    log.error("OperationException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US5099.getCode());
      response.setMessage(ResultResCode.US5099.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;
      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.INTERNAL_SERVER_ERROR.name(), null,
                LocaleContextHolder.getLocale()));
      }

    } else {
      response.setDetail(messageSource
          .getMessage(ResultErrorCode.INTERNAL_SERVER_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }

  // 500
  @ExceptionHandler(ExternalServerException.class)
  public ResponseEntity<ExceptionMessage> externalException(HttpServletRequest req,
      final ExternalServerException exception) {

    log.error("ExternalServerException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US5090.getCode());
      response.setMessage(ResultResCode.US5090.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;

      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), null,
                LocaleContextHolder.getLocale()));
      }

    } else {
      response.setDetail(messageSource
          .getMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }

  // 500
  @ExceptionHandler(UnknownException.class)
  public ResponseEntity<ExceptionMessage> unknownException(HttpServletRequest req,
      final UnknownException exception) {

    log.error("UnknownException :  {}", exception.getMessage());

    ExceptionMessage response = new ExceptionMessage(exception);

    String code = exception.getCode();
    if (!StringUtils.isEmpty(code)) {
      response.setCode(svcName+ResultResCode.get(code).getCode());
      response.setMessage(ResultResCode.get(code).getKoMsg());
    } else {
      response.setCode(svcName+ResultResCode.US5099.getCode());
      response.setMessage(ResultResCode.US5099.getKoMsg());
    }

    String[] message = null;
    String msgKey = null;
    String[] params = null;

    if (!StringUtils.isEmpty(exception.getMessage())) {
      message = exception.getMessage().split(":");
      msgKey = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;

      if (!StringUtils.isEmpty(msgKey) && ResultErrorCode.get(msgKey) != null) {
        response
            .setDetail(messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale()));
      } else {
        response.setDetail(messageSource
            .getMessage(ResultErrorCode.INTERNAL_SERVER_ERROR.name(), null,
                LocaleContextHolder.getLocale()));
      }
    } else {
      response.setDetail(messageSource
          .getMessage(ResultErrorCode.INTERNAL_SERVER_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, exception.getHttpStatus());
  }

  // 500 : 나머지
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionMessage> otherException(HttpServletRequest req,
      final Exception exception) {

    log.error("Exception :  {}", exception.getMessage(), exception);

    UnknownException unknownException = new UnknownException(exception.getMessage(), exception);

    ExceptionMessage response = new ExceptionMessage(unknownException);
    response.setCode(svcName+ResultResCode.US5099.getCode());
    response.setMessage(ResultResCode.US5099.getKoMsg());
    response.setDetail(messageSource
        .getMessage(ResultErrorCode.INTERNAL_SERVER_ERROR.name(), null,
            LocaleContextHolder.getLocale()));

    log.error("Response Exception : {}", response.toString());

    return new ResponseEntity<ExceptionMessage>(response, unknownException.getHttpStatus());
  }

  public boolean updateValidation(Object _old, Object _new) {
    return _new != null && (_old == null || !_old.equals(_new));
  }

  public List<OrderByMap> orderValidation(List<OrderByMap> map, String... validationKeys){
    List<OrderByMap> ret = new ArrayList<>();
    map.forEach(elem -> {
      for(String key : validationKeys){
        if(TypeConvertUtil.recoverySnakeCase(key).equals(elem.getSort())){
          ret.add(elem);
        }
      }
    });
    return ret;
  }
}