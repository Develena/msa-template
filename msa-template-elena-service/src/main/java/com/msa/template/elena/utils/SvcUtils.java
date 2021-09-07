package com.msa.template.elena.utils;

import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.utils.EncryptionUtil;
import com.nuri.utils.RestUtil;
import com.nuri.utils.RsaEncodingUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SvcUtils {

  @Value("${msa.url.ums}")
  private String umsServiceUrl;

  @Value("${msa.url.login}")
  private String loginServiceUrl;


  @Value("${rsa.publicKeyString}")
  private String publicKeyString;


  public Map<String, Object> compareUpdatedField(Map<String, Object> befMap,
      Map<String, Object> aftMap) {
    Map<String, Object> updatedFields = new HashMap<String, Object>();

    for (String key : befMap.keySet()) {
      if (aftMap.get(key) != null && (befMap.get(key) == null || !befMap.get(key)
          .equals(aftMap.get(key)))) {
        updatedFields.put(key, befMap.get(key) + "::" + aftMap.get(key));
      }
    }

    for (String key : updatedFields.keySet()) {
      log.info("변경 사항 : {} - {}", key, updatedFields.get(key));
    }
    return updatedFields;
  }

  public ResponseEntity<String> sendPasswdToLoginService(String userId, String pass, Date updateDt)
      throws Exception {
    JSONObject sendData = new JSONObject();
    sendData.put("url", loginServiceUrl + "/users/" + userId + "/password/init");
    sendData.put("method", HttpMethod.PUT);
    sendData.put("content-type", "application/json");
    JSONObject paramdata = new JSONObject();
    paramdata.put("passwd", pass);
    paramdata.put("tempPasswdIssuedYn", true);
    sendData.put("params", paramdata);

    ResponseEntity<String> loginResult = RestUtil.sendRestApi(sendData);
    return loginResult;
  }

  public ResponseEntity<String> sendUserStatusToLoginService(String userId, String status, Date updatedDt)
      throws Exception {
    JSONObject sendData = new JSONObject();
    sendData.put("url", loginServiceUrl + "/users/" + userId + "/status");
    sendData.put("method", HttpMethod.PUT);
    sendData.put("content-type", "application/json");
    JSONObject paramdata = new JSONObject();
    paramdata.put("serviceStatus", status);
    paramdata.put("updatedDt", updatedDt);
    sendData.put("params", paramdata);

    ResponseEntity<String> loginResult = RestUtil.sendRestApi(sendData);
    return loginResult;
  }

  public ResponseEntity<String> sendUmsService(String callNumber, String pass, String userId)
      throws Exception {
    JSONObject sendData = new JSONObject();
    sendData.put("url", umsServiceUrl + "/noticeTalk/send");
    sendData.put("method", HttpMethod.POST);
    sendData.put("content-type", "application/json");

    String text =
        "{\"urlDevision\":\"NuribillNoticeTalk\",\"id\":\"" + callNumber
            + "\",\"contents\":\"-\",\"project\":\"스마트전력\""
            + ",\"templateType\":\"template2\",\"userNm\":\"" + userId + "\",\"tempPwd\":\"" + pass
            + "\"}";

    System.out.println("UMS 보내기전 파라메타");
    System.out.println(text);
    String data = null;
    String key = null;
    // 암호화
    try {
      data = EncryptionUtil.aesEncrypt(text, "1234567891011123");
      key = RsaEncodingUtil.Base64AndRSAencryp(publicKeyString, "1234567891011123");
    } catch (Exception ex) {
      // 복호화 실패 Excetion 이르키기
      //throw new UnknownException(ExceptionMessage.makeExceptionMessage(ResultErrorCode.DECRYPT_ERROR.name()));
    }

    JSONObject ob = new JSONObject();
    ob.put("data", data);
    ob.put("key", key);

    sendData.put("params", ob);

    ResponseEntity<String> loginResult = RestUtil.sendRestApi(sendData);
    return loginResult;
  }
}
