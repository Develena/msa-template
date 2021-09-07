/*
 * PagingGridResult.java
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

package com.msa.template.elena.entity.results;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GridResult<T> {

  private List<T> data;
  private int totalCnt;

}
