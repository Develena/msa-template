/*
 * TokenProvider.java
 *
 *
 * 15/06/21, 02:00 PM
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

package com.msa.template.elena.security;

import com.nuri.green.onm.user.entity.jwt.Token;
import com.nuri.green.onm.user.entity.jwt.User;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.utils.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Base64;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {

  @Value("${nuri.jwt.sign.secret}")
  private String secretKey;

  // secretKey를 Base64로 인코딩
  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String resolveToken(HttpServletRequest request, String headerName) {
    return request.getHeader(headerName);
  }

  // 토큰의 유효성 + 만료일자 확인
  public boolean validateToken(String jwtToken) {
    Jws<Claims> claims = null;
    try {
      claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
    } catch (MalformedJwtException e) {
      throw new MalformedJwtException("malformed token");
    } catch (SignatureException e) {
      throw new SignatureException("signature invalid token");
    }

    return !claims.getBody().getExpiration().before(DateUtil.getCurrentDateTime());
  }

  public Claims getClaims(String jwtToken) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody();
  }

  public String getId(String jwtToken) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getId();
  }

  public String getTokenType(String jwtToken) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody()
        .get("tokenType").toString();
  }

  public Authentication getAuthentication(String jwtToken) {

    Token token = getToken(jwtToken);
    UserDetails userDetails = User.builder()
        .userId(token.getAudience())
        .role(token.getRoles())
        .content(token.getContent()) // id, role 이외의 나머지 정보.
        .build();
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public Token getToken(String jwtToken) {
    Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody();

    return Token.builder()
        .tokenId(body.getId())
        .issuer(body.getIssuer())
        .issuedAt(body.getIssuedAt())
        .expiration(body.getExpiration())
        .audience(body.getAudience())
        .roles(body.get("role").toString())
        .content(body.get("content").toString())
        .build();
  }


}
