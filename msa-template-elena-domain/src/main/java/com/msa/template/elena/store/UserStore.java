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

import com.msa.template.elena.entity.params.register.RegisterUserParam;
import com.msa.template.elena.entity.results.UserGridReturnResult;
import com.msa.template.elena.entity.results.UserInfoResult;
import com.msa.template.elena.entity.results.UserResult;
import com.msa.template.elena.entity.results.UserRoleRes;
import com.msa.template.elena.entity.results.UserToken;
import java.util.List;
import java.util.Map;

public interface UserStore {

  UserResult findUserById(String userId);

  UserInfoResult getUserInfo(String userId);

  int registerUser(RegisterUserParam user);

  UserToken getUserTokenInfo(String userId);

  UserRoleRes getUserRoleInfo(String userId);

  UserResult findUserByUserName(Map<String, Object> param);

  String getUserPassword(String userId);

  Integer modifyUser(Map<String, Object> param);

  List<UserGridReturnResult> getUserGridListByParam(Map param);
  Integer getUserGridTotalCntByParam(Map param);
}
