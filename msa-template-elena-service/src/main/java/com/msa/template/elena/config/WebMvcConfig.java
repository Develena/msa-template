/*
 * WebMvcConfig.java
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

package com.msa.template.elena.config;

import com.msa.template.elena.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired
  private TokenProvider tokenProvider;

  @Value("${nuri.jwt.auth.name}")
  private String authHeaderName;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedMethods("PUT", "DELETE", "GET", "POST")
        .allowedOrigins("*");
  }

//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(getTokenInterceptor())
//        .addPathPatterns("/gnd-onm-user/**")
//        // public url pattern setting.
//        .excludePathPatterns("/gnd-onm-user/v1.0/users/**/token")
//        .excludePathPatterns("/gnd-onm-user/v1.0/users/**/role")
//    ;
//  }

//  @Bean
//  public TokenInterceptor getTokenInterceptor() {
//    return new TokenInterceptor(tokenProvider);
//
//  }
}
