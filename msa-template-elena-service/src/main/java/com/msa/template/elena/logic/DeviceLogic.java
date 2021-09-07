/*
 * UserLogic.java
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

import com.nuri.green.onm.user.entity.params.modify.ModifyDeviceParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyDevicePushTokenParam;
import com.nuri.green.onm.user.entity.params.register.RegisterDeviceParam;
import com.nuri.green.onm.user.entity.results.AppInfoResult;
import com.nuri.green.onm.user.entity.results.DeviceResult;
import com.nuri.green.onm.user.spec.DeviceService;
import com.nuri.green.onm.user.store.DeviceStore;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class DeviceLogic implements DeviceService {

  @Autowired
  private DeviceStore deviceStore;


  @Override
  public DeviceResult getDeviceInfo(String udid) throws Exception {
    return deviceStore.getDeviceInfo(udid);
  }

  @Override
  public int registerDeviceInfo(RegisterDeviceParam param) throws Exception {
    return deviceStore.registerDeviceInfo(param);
  }

  @Override
  public int modifyDevicePushToken(ModifyDevicePushTokenParam param) throws Exception {
    return deviceStore.modifyDevicePushToken(param);
  }

  @Override
  public int modifyDeviceInfo(ModifyDeviceParam param) throws Exception {
    int i = deviceStore.modifyDeviceInfo(param);
    if (i == 1) {
      deviceStore.modifyUserDetailMobileUseYn(param.getUserSeq());
    }
    return i;
  }

  @Override
  public List<AppInfoResult> getAppInfo(Map<String, Object> param) throws Exception {
    return deviceStore.getAppInfo(param).stream().map(m -> m.toResponse()).collect(Collectors.toList());
  }

  @Override
  public int modifyAppInfo(Map<String, Object> param) throws Exception {
    return deviceStore.modifyAppInfo(param);
  }
}
