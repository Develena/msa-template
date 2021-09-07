/*
 * ResponseErrorCode.java
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
 */

package com.msa.template.elena.entity.enums;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 해당 클래스는 서비스의 정상/에러 응답 코드를 작성합니다.
 */
@AllArgsConstructor
public enum ResultResCode {

   /* 200 OK */
  US2000("2000", "성공", "Operation Success"),

  /* 400 BadRequest */
  US4000("4000", "필수 파라미터 미입력", "Mandatory param error"),
  US4001("4001", "지원하지 않는 타입", "Invalid param error"),
  US4002("4002", "잘못된 포맷", "Bad format param error"),
  US4009("4009", "요청 실패", "Bad Request"),

  /* 401 Unauthenticated (인증) */
  US4010("4010", "미인증(로그인)", "Unauthenticated"),
  US4011("4011", "미인증(인증코드)", "Unauthenticated"),
  US4012("4012", "만료된 인증코드", "Expired Auth code"),
  US4013("4013", "이미 인증된 코드", "Already authenticated code"),
  US4019("4019", "미인증", "Unauthenticated"),

  /* 403 Forbidden(Unauthorized) (토큰관련)  */
  US4030("4030", "만료된 토큰", "Expired Token"),
  US4031("4031", "잘못된 토큰", "Invalid Token"),
  US4032("4032", "권한 없음", "Forbidden"),
  US4039("4039", "기타 권한 오류", "Forbidden"),

  /* 404 Not Found */
  US4040("4040", "페이지/파일 없음", "Resource Not Found"),


  /* 500 Internal Server Error */
  US5000("5000", "조회 실패", "Query Failed"),
  US5001("5001", "등록 실패", "Registration Failed"),
  US5002("5002", "생성 실패", "Generation Failed"),
  US5003("5003", "수정 실패", "Modification Failed"),
  US5004("5004", "삭제 실패", "Removal Failed"),
  US5005("5005", "중복 에러", "Duplicated Error"),
  US5006("5006", "존재하지 않는 엔티티", "Not Found"),
  US5007("5007", "유효하지 않음", "Invalid Entity"),
  US5090("5090", "외부 서버 에러", "External Server Error"),
  US5099("5099", "내부 서버 에러", "Internal Server Error"),

  ;

  private static final Map<String, ResultResCode> lookup = Maps.uniqueIndex(
      Arrays.asList(ResultResCode.values()),
      ResultResCode::getCode
  );

  @Getter
  private String code;
  @Getter
  private String koMsg;
  @Getter
  private String enMsg;

  public static ResultResCode get(String code) {
    return lookup.get(code);
  }
}
