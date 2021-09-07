/*
 * User.java
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

package com.msa.template.elena.entity.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  private static final long serialVersionUID = 2863408751430021949L;

  private String userId;

  private Date created;

  private String password;

  private String role;

  private String content;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.asList(StringUtils.commaDelimitedListToStringArray(this.role)).stream().map(
        SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return userId;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
