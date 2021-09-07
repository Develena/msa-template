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
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.UserCompany;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.spec.UserCompanyService;
import com.nuri.green.onm.user.store.UserCompanyStore;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserCompanyLogic implements UserCompanyService {

  @Autowired
  private UserCompanyStore userCompanyStore;

  @Override
  public List<UserCompany> checkUserCompanyByCompSeq(Integer compSeq) {
    HashMap<String, Object> param = new HashMap<>();

    if (compSeq == null) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "compSeq"));
    }
    param.put("compSeq", compSeq);
    return userCompanyStore.getUserCompaniesByParam(param);
  }
}
