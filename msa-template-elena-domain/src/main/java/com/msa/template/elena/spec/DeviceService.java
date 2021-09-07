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

import com.msa.template.elena.entity.params.modify.ModifyDeviceParam;
import com.msa.template.elena.entity.params.modify.ModifyDevicePushTokenParam;
import com.msa.template.elena.entity.params.register.RegisterDeviceParam;
import com.msa.template.elena.entity.results.AppInfoResult;
import com.msa.template.elena.entity.results.DeviceResult;

import java.util.List;
import java.util.Map;

public interface DeviceService {

  DeviceResult getDeviceInfo(String udid) throws Exception;

  int registerDeviceInfo(RegisterDeviceParam param) throws Exception;

  int modifyDevicePushToken(ModifyDevicePushTokenParam param) throws Exception;

  int modifyDeviceInfo(ModifyDeviceParam param) throws Exception;

  List<AppInfoResult> getAppInfo(Map<String, Object> param) throws Exception;

  int modifyAppInfo(Map<String, Object> param) throws Exception;
}
