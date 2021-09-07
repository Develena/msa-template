/*
 * WebConfig.java
 *
 *
 * 21. 3. 15. 오전 11:19
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

package com.msa.template.elena.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import com.msa.template.elena.injection.HTMLCharacterEscapes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FilterConfig implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
    filterRegistration.setFilter(new XssEscapeServletFilter());
    filterRegistration.setOrder(1);
    filterRegistration.addUrlPatterns("/*");

    return filterRegistration;
  }

  /**
   * xss 방어 처리 messageConverters
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    //super.configureMessageConverters(converters);
    converters.add(htmlEscapingConveter());
  }

  private HttpMessageConverter<?> htmlEscapingConveter() {
    ObjectMapper objectMapper = new ObjectMapper();
    // 3. ObjectMapper에 특수 문자 처리 기능 적용
    objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());

    // 4. MessageConverter에 ObjectMapper 설정
    MappingJackson2HttpMessageConverter htmlEscapingConverter =
        new MappingJackson2HttpMessageConverter();
    htmlEscapingConverter.setObjectMapper(objectMapper);

    return htmlEscapingConverter;
  }

  @Bean
  public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
    ObjectMapper copy = objectMapper.copy();
    copy.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
    return new MappingJackson2HttpMessageConverter(copy);
  }
}
