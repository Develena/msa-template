/*
 * TokenValidationFilter.java
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

import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;


/**
 * Access Token validation Url filter
 */
@Slf4j
public class JwtAccessTokenFilter implements Filter {

  private TokenProvider tokenProvider;
  private String headerName;
  private MessageSource messageSource;

  public JwtAccessTokenFilter(TokenProvider tokenProvider, String headerName,
      MessageSource messageSource) {
    this.tokenProvider = tokenProvider;
    this.headerName = headerName;
    this.messageSource = messageSource;

  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.debug("####### JwtAccessTokenFilter - init()");
  }


  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException, AuthorizedFailException {
    log.info("####### JwtAccessTokenFilter - before #######");

    String path = ((HttpServletRequest) request).getRequestURI();
    log.debug("req path : {} ", path);

    String accessToken = tokenProvider.resolveToken((HttpServletRequest) request, headerName);
    log.debug("accessToken: {}", accessToken);

    if (!StringUtils.isEmpty(accessToken)) {

      if (tokenProvider.validateToken(accessToken)) {

        if (!tokenProvider.getTokenType(accessToken).equals("access_token")) {
          log.error("token type invalid");
          throw new MalformedJwtException("invalid type token");

        } else {
          // Jwt의 User 정보를 authentication에 setting.
          Authentication authentication = tokenProvider.getAuthentication(accessToken);
          log.info("user role : {} ",
              ((UserDetails) authentication.getPrincipal()).getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("accessToken validation : success");
        }

      } else {
        log.error("token expired");
        throw new ExpiredJwtException(null, null, "expired token");
      }

    }

    filterChain.doFilter(request, response);

    log.info("####### JwtAccessTokenFilter - after #####");

  }

}
