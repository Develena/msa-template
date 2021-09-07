package com.msa.template.elena.utils;

import com.nuri.utils.RestUtil;
import com.nuri.utils.RsaEncodingUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UmsUtils {

  @Value("${rsa.publicKeyString}")
  private String publicKeyString;

  private ResponseEntity<String> sendToUMS(String url, HttpMethod method, String data)
      throws Exception {
    JSONObject sendData = new JSONObject();
    sendData.put("url", url);
    sendData.put("method", method);
    sendData.put("content-type", "application/json");
    sendData.put("params", RsaEncodingUtil.CreateUmsRsaData(data, publicKeyString));

    ResponseEntity<String> sendResult = RestUtil.sendRestApi(sendData);
    return sendResult;
  }
}