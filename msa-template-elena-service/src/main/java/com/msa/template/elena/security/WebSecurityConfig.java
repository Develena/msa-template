/*
 * WebSecurityConfig.java
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


  @Autowired
  private TokenProvider tokenProvider;
  @Autowired
  private MessageSource messageSource;

  @Value("${nuri.jwt.access.name}")
  private String accessHeaderName;


  /**
   * ROLE(ALL, ADMIN)에 따른 path url 지정 예시
   * <p>
   * /api/gnd-onm-user/v1.0/users/**:USER /api/gnd-onm-user/v1.0/admin/**:ADMIN
   */
  @Value("#{'${nuri.security.auth}'.split(',')}")
  private String[] auth;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    System.out.println("###### WebSecurityConfig setting : url, method 권한 체크.");

    http.httpBasic().disable() // rest -> 기본 설정 사용 안함(기본 설정: 로그인폼 화면으로 redirect)
        .csrf().disable() // rest -> csrf 보안 불필요.
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token -> session 불필요.
        .and()
        .authorizeRequests()
        .and()
        .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
    ;

    /**
     * ROLE(ALL, ADMIN)에 따른 path url 지정 예시
     */
    for (String str : auth) {
      String[] split = StringUtils.delimitedListToStringArray(str, ":");
      log.info("path: {}, role: {}", split[0], split[1]);
      http.authorizeRequests().antMatchers(split[0]).hasAnyAuthority(split[1]);
    }

    http.addFilterBefore(new JwtAccessTokenFilter(tokenProvider, accessHeaderName, messageSource),
        UsernamePasswordAuthenticationFilter.class);

    http.addFilterBefore(new ExceptionHandlerFilter(messageSource),
        JwtAccessTokenFilter.class);
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler(messageSource);
  }

}
