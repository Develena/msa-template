/*
 * UserMapperStore.java
 *
 *
 * 21. 3. 15. 오전 11:20
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


import com.nuri.green.onm.user.entity.params.register.RegisterUserParam;
import com.nuri.green.onm.user.entity.results.UserGridReturnResult;
import com.nuri.green.onm.user.entity.results.UserInfoResult;
import com.nuri.green.onm.user.entity.results.UserResult;
import com.nuri.green.onm.user.entity.results.UserRoleRes;
import com.nuri.green.onm.user.entity.results.UserToken;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserMapperStore implements UserStore {

  @Autowired
  private SqlSessionTemplate sqlSession;

  @Override
  public UserResult findUserById(String userId) {
    return sqlSession.selectOne("userMapper.selectUserById", userId);

  }

  @Override
  public UserInfoResult getUserInfo(String userId) {
    return sqlSession.selectOne("userMapper.selectUserInfoById", userId);

  }

  @Override
  public int registerUser(RegisterUserParam user) {
    return sqlSession.insert("userMapper.createUser", user);
  }

  @Override
  public UserToken getUserTokenInfo(String userId) {
    return sqlSession.selectOne("userMapper.selectUserTokenInfo", userId);
  }

  @Override
  public UserRoleRes getUserRoleInfo(String userId) {
    return sqlSession.selectOne("userMapper.selectUserRoleInfo", userId);
  }

  @Override
  public UserResult findUserByUserName(Map<String, Object> param) {
    return sqlSession.selectOne("userMapper.selectUserByUserName", param);
  }

  @Override
  public String getUserPassword(String userId) {
    return sqlSession.selectOne("userMapper.selectUserPassword", userId);
  }

  @Override
  public Integer modifyUser(Map<String, Object> param) {
    return sqlSession.update("userMapper.updateUser", param);
  }

  @Override
  public List<UserGridReturnResult> getUserGridListByParam(Map param) {
    return sqlSession.selectList("userMapper.selectUserGrid", param);
  }

  @Override
  public Integer getUserGridTotalCntByParam(Map param) {
    return sqlSession.selectOne("userMapper.countUserGridTotal", param);
  }
}
