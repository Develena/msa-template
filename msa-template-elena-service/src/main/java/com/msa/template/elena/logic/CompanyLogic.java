/*
 * CompanyLogic.java
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

package com.msa.template.elena.logic;

import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.results.Company;
import com.nuri.green.onm.user.entity.results.CompanyReturnResult;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.spec.CompanyService;
import com.nuri.green.onm.user.store.CompanyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
public class CompanyLogic implements CompanyService {

  @Autowired
  private CompanyStore companyStore;

  @Override
  public int registerCompany(Map param) {
    return companyStore.registerCompany(param);
  }

  @Override
  public int modifyCompany(Map param) {
    return companyStore.modifyCompany(param);
  }

  @Override
  public int deleteCompany(Integer compSeq) {
    return companyStore.deleteCompany(compSeq);
  }

  @Override
  public Company getCompanyByCompSeq(Integer compSeq) {
    return companyStore.getCompanyByCompSeq(compSeq);
  }

  @Override
  public Company getCompanyByCompNm(String compNm) {
    return companyStore.getCompanyByCompNm(compNm);
  }

  @Override
  public Company checkCompany(String bizRegNo, String corpNo) {
    HashMap<String, String> param = new HashMap<>();
    if (StringUtils.isEmpty(bizRegNo)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "bizRegNo"));
    }
    if (StringUtils.isEmpty(corpNo)) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "corpNo"));
    }
    param.put("bizRegNo", bizRegNo);
    param.put("corpNo", corpNo);
    return companyStore.getCompanyByParam(param);
  }

  @Override
  public List<CompanyReturnResult> getCompanyGridListByParam(Map param) {
    return companyStore.getCompanyGridListByParam(param);
  }

  @Override
  public Integer getCompanyGridTotalCntByParam(Map param) {
    return companyStore.getCompanyGridTotalCntByParam(param);
  }
}
