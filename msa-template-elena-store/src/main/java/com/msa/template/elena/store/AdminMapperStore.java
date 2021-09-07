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


import com.nuri.green.onm.user.entity.params.UserPageParam;
import com.nuri.green.onm.user.entity.results.UserReturnResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AdminMapperStore implements AdminStore {

  @Autowired
  private SqlSessionTemplate sqlSession;


  @Override
  public List<UserReturnResult> getUserPageList(UserPageParam param) {
    return sqlSession.selectList("adminMapper.getUserPageList", param);
  }

  @Override
  public int getUserListCount(UserPageParam param) {
    return sqlSession.selectOne("adminMapper.getUserListCount", param);
  }
}
