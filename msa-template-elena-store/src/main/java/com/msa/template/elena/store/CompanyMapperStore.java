/*
 * CompanyMapperStore.java
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


import com.nuri.green.onm.user.entity.results.Company;
import com.nuri.green.onm.user.entity.results.CompanyReturnResult;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CompanyMapperStore implements CompanyStore {

  @Autowired
  private SqlSessionTemplate sqlSession;

  @Override
  public int registerCompany(Map param) {
    return sqlSession.insert("companyMapper.createCompany", param);
  }

  @Override
  public int modifyCompany(Map param) {
    return sqlSession.insert("companyMapper.updateCompany", param);
  }

  @Override
  public int deleteCompany(Integer compSeq) {
    return sqlSession.delete("companyMapper.deleteCompany", compSeq);
  }

  @Override
  public Company getCompanyByCompSeq(Integer compSeq) {
    return sqlSession.selectOne("companyMapper.selectCompanyByCompSeq", compSeq);
  }

  @Override
  public Company getCompanyByCompNm(String compNm) {
    return sqlSession.selectOne("companyMapper.selectCompanyByCompNm", compNm);
  }

  @Override
  public Company getCompanyByParam(Map param) {
    return sqlSession.selectOne("companyMapper.selectCompanyByParam", param);
  }

  @Override
  public List<CompanyReturnResult> getCompanyGridListByParam(Map param) {
    return sqlSession.selectList("companyMapper.selectCompanyGrid", param);
  }

  @Override
  public Integer getCompanyGridTotalCntByParam(Map param) {
    return sqlSession.selectOne("companyMapper.countCompanyGridTotal", param);
  }
}
