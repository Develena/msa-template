/*
 * CommonObj.java
 *
 *
 * 21. 3. 15. 오전 11:16
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

package com.msa.template.elena.entity.page;

import com.nuri.utils.TypeConvertUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Getter
@Setter
@ToString(callSuper = true)
public class CommonParam {

  // like 조건절
//  @Schema(description = "검색 필드", example = "name", hidden = true)
//  private String searchfield; // 검색필드
  @Schema(description = "검색어", example = "홍길동")
  private String searchquery; // 검색어

  // 정렬
  @Hidden // String type의 orders 로 요청받고, OrderByMap 형태로 쿼리입력 형태로 변환되므로 Swagger Hidden 처리
  private List<OrderByMap> orderMap;
  @Schema(description = "order")
  private String orders;

  // 요청 파라미터를 쿼리 필드 형태로 변환
  public void convertToQueryField() {
    // 1. camel case 입력 필드를 snake case 쿼리 필드로 변환. (nUserSeq -> n_user_seq)
//    if (!StringUtils.isEmpty(this.searchfield)) {
//      this.searchfield = TypeConvertUtil.recoverySnakeCase(this.searchfield);
//    }
    //  1. String으로 받은 order 정보를 쿼리의 Map 객체로 변환.
    //  2. camel case 입력 필드를 snake case 쿼리 필드로 변환.
    if (!StringUtils.isEmpty(this.orders)) {
      this.orderMap = new ArrayList<OrderByMap>();
      for (String strOrder : this.orders.split(",")) {
        if (strOrder.startsWith("-")) {
          this.orderMap.add(
              new OrderByMap(TypeConvertUtil.recoverySnakeCase(strOrder.substring(1)), "desc"));
        } else {
          this.orderMap.add(new OrderByMap(TypeConvertUtil.recoverySnakeCase(strOrder), "asc"));
        }
      }
    }

  }

}
