/*
 * CustomAccessDeniedHandler.java
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
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private MessageSource messageSource;

  public CustomAccessDeniedHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException e) throws IOException, ServletException {

    log.error(">>>>>>>>>>>>>>>> handle() - {}", request.getRequestURI());
    String detail = messageSource.getMessage(ResultErrorCode.FORBIDDEN_ERROR.name(),
        new String[]{request.getRequestURI()},
        LocaleContextHolder.getLocale());

    ExceptionMessage res = new ExceptionMessage();
    res.setCode("US"+ResultResCode.US4039.getCode());
    res.setMessage(ResultResCode.US4039.getKoMsg());
    res.setDetail(detail);

    log.info("Message : {}", res.toString());
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.getWriter().write(new Gson().toJson(res));
  }

}
