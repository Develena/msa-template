/*
 * ExceptionHandlerFilter.java
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

package com.msa.template.elena.security;

import com.google.gson.Gson;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private MessageSource messageSource;

  public ExceptionHandlerFilter(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {
    log.info("########## - doFilterInternal()");
    try {
      filterChain.doFilter(httpServletRequest, httpServletResponse);
    } catch (ExpiredJwtException e) {
      log.error("########## - doFilterInternal() - token expired", e);
      String message = ExceptionMessage.makeExceptionMessage(ResultErrorCode.EXPIRED_TOKEN_ERROR
          .name(), "x-access-token");
      setErrorResponse(HttpStatus.FORBIDDEN, httpServletResponse, message);
    } catch (MalformedJwtException e) {
      log.error("########## - doFilterInternal() - MalformedJwtException");
      log.error(e.getMessage(), e);
      String message = ExceptionMessage.makeExceptionMessage(ResultErrorCode.INVALID_TOKEN_ERROR
          .name(), "x-access-token");
      setErrorResponse(HttpStatus.FORBIDDEN, httpServletResponse, message);
    } catch (SignatureException e) {
      log.error("########## - doFilterInternal() - SignatureException");
      log.error(e.getMessage(), e);
      String message = ExceptionMessage.makeExceptionMessage(ResultErrorCode.INVALID_TOKEN_ERROR
          .name(), "x-access-token");
      setErrorResponse(HttpStatus.FORBIDDEN, httpServletResponse, message);
    } catch (AuthorizedFailException e) {
      log.error("########## - doFilterInternal() - AuthorizedFailException");
      log.error(e.getMessage(), e);
      setErrorResponse(HttpStatus.FORBIDDEN, httpServletResponse, e.getMessage());

    } catch (AuthenticationServiceException e) {
      log.error("########## - doFilterInternal() - AuthenticationServiceException");
      log.error(e.getMessage(), e);
      String message = ExceptionMessage.makeExceptionMessage(ResultErrorCode.FORBIDDEN_ERROR
          .name(), "x-access-token");
      setErrorResponse(HttpStatus.FORBIDDEN, httpServletResponse, message);

    } catch (Exception e) {
      log.info("########## - doFilterInternal() - INTERNAL_SERVER_ERROR");
      log.error(e.getMessage(), e);
      setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, httpServletResponse, e.getMessage());

    }

  }

  public void setErrorResponse(HttpStatus status, HttpServletResponse response, String errorMsg) {
    log.info("########## - setErrorResponse");

    response.setStatus(status.value());
    response.setContentType("application/json"); // ;charset=UTF-8
    response.setCharacterEncoding("UTF-8");

    String[] message = null;
    String key = null;
    String[] params = null;

    if (!StringUtils.isEmpty(errorMsg)) {
      message = errorMsg.split(":");
      key = message[0];
      params = (message.length > 1) ? message[1].split(",") : null;
    }

    ExceptionMessage res = new ExceptionMessage(status);

    if (status.is4xxClientError()) {
      res.setCode("US" + ResultResCode.US4039.getCode());
      res.setMessage(ResultResCode.US4039.getKoMsg());
    } else {
      res.setCode("US" + ResultResCode.US5099.getCode());
      res.setMessage(ResultResCode.US5099.getKoMsg());
    }

    if (message != null && ResultErrorCode.get(key) != null) {
      res.setDetail(messageSource.getMessage(key, params, LocaleContextHolder.getLocale()));
    } else {
      res.setDetail(messageSource
          .getMessage(ResultErrorCode.INTERNAL_SERVER_ERROR.name(), null,
              LocaleContextHolder.getLocale()));
    }

    try {
      response.getWriter().write(new Gson().toJson(res));
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

}