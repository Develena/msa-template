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


import com.nuri.green.onm.user.entity.params.modify.ModifyDeviceParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyDevicePushTokenParam;
import com.nuri.green.onm.user.entity.params.register.RegisterDeviceParam;
import com.nuri.green.onm.user.entity.results.AppInfo;
import com.nuri.green.onm.user.entity.results.DeviceResult;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DeviceMapperStore implements DeviceStore {

  @Autowired
  private SqlSessionTemplate sqlSession;

  @Override
  public DeviceResult getDeviceInfo(String udid) throws Exception {
    return sqlSession.selectOne("deviceMapper.selectDeviceInfo", udid);
  }

  @Override
  public int registerDeviceInfo(RegisterDeviceParam param) throws Exception {
    return sqlSession.insert("deviceMapper.insertDeviceInfo", param);
  }

  @Override
  public int modifyDevicePushToken(ModifyDevicePushTokenParam param) throws Exception {
    return sqlSession.update("deviceMapper.updateDevicePushToken", param);
  }

  @Override
  public int modifyDeviceInfo(ModifyDeviceParam param) throws Exception {
    return sqlSession.update("deviceMapper.updateDeviceInfo", param);
  }

  @Override
  public int modifyUserDetailMobileUseYn(int userSeq) throws Exception {
    return sqlSession.update("deviceMapper.modifyUserDetailMobileUseYn", userSeq);
  }

  @Override
  public List<AppInfo> getAppInfo(Map<String, Object> param) throws Exception {
    return sqlSession.selectList("deviceMapper.selectAppInfo", param);
  }

  @Override
  public int modifyAppInfo(Map<String, Object> param) throws Exception {
    return sqlSession.update("deviceMapper.updateAppInfo", param);
  }
}
