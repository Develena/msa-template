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


import com.nuri.green.onm.user.entity.params.UserRoleParam;
import com.nuri.green.onm.user.entity.results.Role;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserRoleMapperStore implements UserRoleStore {

  @Autowired
  private SqlSessionTemplate sqlSession;


  @Override
  public int registerUserRole(UserRoleParam param) {
    return sqlSession.insert("userRoleMapper.createUserRole", param);
  }

  @Override
  public Role getRoleBySeq(Integer roleSeq) {
    return sqlSession.selectOne("userRoleMapper.selectRoleBySeq", roleSeq);
  }

  @Override
  public Role getRoleById(String roleId) {
    return sqlSession.selectOne("userRoleMapper.selectRoleById", roleId);
  }
}
