/*
 * UserStore.java
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

package com.msa.template.elena.store;

import com.msa.template.elena.entity.params.UserPageParam;
import com.msa.template.elena.entity.results.UserReturnResult;

import java.util.List;

public interface AdminStore {

  List<UserReturnResult> getUserPageList(UserPageParam param);

  int getUserListCount(UserPageParam param);
}
