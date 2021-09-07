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


import com.nuri.green.onm.user.entity.params.UserCompanyParam;
import com.nuri.green.onm.user.entity.results.UserCompany;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserCompanyMapperStore implements UserCompanyStore {

  @Autowired
  private SqlSessionTemplate sqlSession;

  @Override
  public int registerUserCompany(UserCompanyParam param) {
    return sqlSession.insert("userCompanyMapper.createUserCompany", param);
  }

  @Override
  public UserCompany getUserCompanyByUserSeq(Integer userSeq) {
    return sqlSession.selectOne("userCompanyMapper.selectUserCompanyByUserSeq", userSeq);
  }

  @Override
  public List<UserCompany> getUserCompaniesByParam(Map param) {
    return sqlSession.selectList("userCompanyMapper.selectUserCompanyByParam",param);
  }
}
