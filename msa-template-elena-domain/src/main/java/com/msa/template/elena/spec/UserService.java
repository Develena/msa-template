/*
 * UserService.java
 *
 *
 * 21. 3. 15. 오전 11:18
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

package com.msa.template.elena.spec;

import com.msa.template.elena.entity.params.UserCompanyParam;
import com.msa.template.elena.entity.params.register.RegisterAuditLogParam;
import com.msa.template.elena.entity.params.register.RegisterUserParam;
import com.msa.template.elena.entity.results.UserGridReturnResult;
import com.msa.template.elena.entity.results.UserCompany;
import com.msa.template.elena.entity.results.UserInfoResult;
import com.msa.template.elena.entity.results.UserResult;
import com.msa.template.elena.entity.results.UserRoleRes;
import com.msa.template.elena.entity.results.UserTokenRes;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

public interface UserService {

  // 아이디 체크
  UserResult checkUserId(String userId);

  // 사용자 정보 가져오기 (회사정보, 권한정보 포함)
  UserInfoResult getUserInfo(String userId);

  // 회원가입
  UserResult registerUser(RegisterUserParam userParam) throws Exception;

  // 사용자 토큰정보 요청
  UserTokenRes getUserTokenInfo(String userId);

  // 사용자 권한 정보 요청
  UserRoleRes getUserRoleInfo(String userId);

  // 사용자 이름으로 조회
  UserResult getUserInfoByName(Map<String, Object> param);

  String getUserPassword(String userId);

  int modifyUser(Map<String, Object> param);

  int registerAuditLog(RegisterAuditLogParam userParam);

  // 사용자-회사 매핑정보 저장
  int registerUserCompany(UserCompanyParam userCompanyParam);

  // 사용자-회사 매핑정보 조회
  UserCompany getUserCompany(Integer userSeq);

  // token validation 요청
  JSONObject requestAuthTokenValidation(String token, String authType, String authInfo)
      throws Exception;

  List<UserGridReturnResult> getUserGridListByParam(Map param);

  Integer getUserGridTotalCntByParam(Map param);


}
