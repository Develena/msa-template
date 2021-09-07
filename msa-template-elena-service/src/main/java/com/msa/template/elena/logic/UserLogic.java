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

import com.msa.template.elena.utils.SvcUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nuri.code.NuriCode;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.NuriRole;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.params.UserCompanyParam;
import com.nuri.green.onm.user.entity.params.UserDetailParam;
import com.nuri.green.onm.user.entity.params.UserRoleParam;
import com.nuri.green.onm.user.entity.params.register.RegisterAuditLogParam;
import com.nuri.green.onm.user.entity.params.register.RegisterUserParam;
import com.nuri.green.onm.user.entity.results.Company;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.UserGridReturnResult;
import com.nuri.green.onm.user.entity.results.Role;
import com.nuri.green.onm.user.entity.results.UserCompany;
import com.nuri.green.onm.user.entity.results.UserInfoResult;
import com.nuri.green.onm.user.entity.results.UserResult;
import com.nuri.green.onm.user.entity.results.UserRoleRes;
import com.nuri.green.onm.user.entity.results.UserToken;
import com.nuri.green.onm.user.entity.results.UserTokenRes;
import com.nuri.green.onm.user.exception.ExternalServerException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.spec.UserService;
import com.nuri.green.onm.user.store.CompanyStore;
import com.nuri.green.onm.user.store.UserAuditLogStore;
import com.nuri.green.onm.user.store.UserCompanyStore;
import com.nuri.green.onm.user.store.UserDetailStore;
import com.nuri.green.onm.user.store.UserRoleStore;
import com.nuri.green.onm.user.store.UserStore;
import com.nuri.utils.RestUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@Transactional
public class UserLogic implements UserService {

  @Value("${project.code}")
  private String svcName;

  @Autowired
  private UserStore userStore;

  @Autowired
  private UserRoleStore userRoleStore;
  @Autowired
  private CompanyStore companyStore;

  @Autowired
  private UserDetailStore userDetailStore;

  @Autowired
  private UserCompanyStore userCompanyStore;

  @Autowired
  private UserAuditLogStore userAuditLogStore;

  @Autowired
  private SvcUtils svcUtils;

  @Value("${rsa.publicKeyString}")
  private String publicKeyString;

  @Value("${msa.url.login}")
  private String loginServiceUrl;

  @Value("${msa.url.auth}")
  private String authServiceUrl;

  @Value("${msa.url.ums}")
  private String umsServiceUrl;

  @Override
  public UserResult checkUserId(String userId) {
    return userStore.findUserById(userId);
  }

  @Override
  public UserInfoResult getUserInfo(String userId) {
    return userStore.getUserInfo(userId);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public UserResult registerUser(RegisterUserParam userParam) throws Exception {

    UserResult result = null;

    // 권한정보 유효 체크
    Role role = null;
    if (userParam.getRoleId() != null) {
      role = userRoleStore.getRoleById(userParam.getRoleId());
    }

    if (role != null) {
      // insert
      int inserted = userStore.registerUser(userParam.toEntity());

      // select
      result = userStore.findUserById(userParam.getUserId());

      if (result != null && role != null) {
        // 그룹 권한 생성
        UserRoleParam userRoleParam = UserRoleParam.builder()
            .userSeq(result.getUserSeq())
            .roleSeq(role.getRoleSeq())
            .creatorId(userParam.getCreatorId())
            .createdDt(result.getCreatedDt())
            .build();

        int roleInserted = userRoleStore.registerUserRole(userRoleParam);

        if (roleInserted == 0) {
          log.error("[회원 생성] 실패 - 사용자_권한 등록 실패");
          throw new OperationException(ResultResCode.US5001.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "회원"));
        }

        // 회사 정보 생성.
        Company company = companyStore.getCompanyByCompSeq(userParam.getCompSeq());
        ;
        if (company != null) {
          UserCompanyParam userCompanyParam = UserCompanyParam.builder()
              .userSeq(result.getUserSeq())
              .compSeq(company.getCompSeq())
              .creatorId(userParam.getCreatorId())
              .createdDt(result.getCreatedDt())
              .build();
          int userCompanyResult = registerUserCompany(userCompanyParam);
          if (userCompanyResult == 0) {
            log.error("[회원 생성] 실패 - 사용자_업체 등록 실패");
            throw new OperationException(ResultResCode.US5001.getCode(),
                ExceptionMessage
                    .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "회원"));
          }
        } else if (!userParam.getRoleId().equals(NuriRole.R001.name())) {
          log.error("[회원 생성] 실패 - 업체 정보 없음");
          throw new OperationException(ResultResCode.US5001.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "회원"));
        }

        // 사용자 추가 정보 생성
        UserDetailParam param = UserDetailParam.builder()
            .userSeq(result.getUserSeq())
            .termsAgree(userParam.getTermsAgree())
            .termsAgreeDt(result.getCreatedDt())
            .mobileUseYn(userParam.getMobileUseYn()) // 디바이스앱 사용여부
            .approvalStatus(NuriCode.UA000.name()) // default : 승인
            .createdDt(result.getCreatedDt())
            .build();

        if (!StringUtils.isEmpty(result.getBirthday())) {
          param.setBirthday(result.getBirthday().substring(4));
          param.setBirthdayType(0); // 양력
        }

        // sms 인증 받았을 경우.
        if (userParam.getSmsVerified() == Integer.valueOf(BooleanResultType.Y.getMsg())) {
          if (!StringUtils.isEmpty(userParam.getTelephone())) {
            param.setSmsVerifiedDt(result.getCreatedDt());
            // sms 수신여부
            if (userParam.getSmsYn() == Integer.valueOf(BooleanResultType.Y.getMsg())) {
              param.setSmsAgreeDt(result.getCreatedDt());
            }
          }
        }

        // email 인증 받았을 경우.
        if (userParam.getEmailVerified() == Integer.valueOf(BooleanResultType.Y.getMsg())) {
          if (!StringUtils.isEmpty(userParam.getEmail())) {
            param.setEmailVerifiedDt(result.getCreatedDt());

            // email 수신여부
            if (userParam.getEmailYn() == Integer.valueOf(BooleanResultType.Y.getMsg())) {
              param.setEmailAgreeDt(result.getCreatedDt());
            }
          }

        }

        int detailInserted = userDetailStore.registerUserDetail(param);

        if (detailInserted == 0) {
          log.error("[회원 생성] 실패 - 사용자_추가정보 등록 실패");
          throw new OperationException(ResultResCode.US5001.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "회원"));
        }

        // 로그인 서비스에 업데이트 요청
        try {
          Map<String, Object> loginParam = new HashMap<String, Object>();
          loginParam.put("userSeq", result.getUserSeq());
          loginParam.put("userId", result.getUserId());
          loginParam.put("passwd", result.getPasswd());
          loginParam.put("serviceStatus", result.getServiceStatus());
          loginParam.put("approvalStatus", param.getApprovalStatus());
          if (!userParam.getRoleId().equals(NuriRole.R001.name())) {
            loginParam.put("compSeq", company.getCompSeq());
          }
          if (!userParam.getRoleId().equals(NuriRole.R001.name())) {
            loginParam.put("compNm", company.getCompNm());
          }

          ResponseEntity<String> loginUpdateResult = requestUpdateAccountToLogin(loginParam);
          log.info(
              "loginUpdateResult.getStatusCode().value() :: " + loginUpdateResult.getStatusCode()
                  .value());
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          log.error("[회원 생성] 실패 - 로그인 서비스 업데이트 실패");
          throw new ExternalServerException(ResultResCode.US5090.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.EXTERNAL_SERVER_ERROR.name(), "LOGIN"));
        }


      } else {
        log.error("[회원 생성] 실패 - 사용자정보 등록 실패");
         throw new OperationException(ResultResCode.US5001.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "회원"));
      }
    } else {
      log.error("[회원 생성] 실패 - 해당 권한 없음.");
       throw new OperationException(ResultResCode.US5001.getCode(),
              ExceptionMessage
                  .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "회원"));
    }

    return result;


  }

  @Override
  public UserTokenRes getUserTokenInfo(String userId) {
    UserToken userInfo = userStore.getUserTokenInfo(userId);
    if (userInfo == null) {
      return null;
    } else {
      JSONObject content = new JSONObject();
      content.put("userNm", userInfo.getUserNm());
      content.put("email", userInfo.getEmail());
      content.put("role", userInfo.getRole());
      content.put("roleNm", userInfo.getRoleNm());
      content.put("compSeq", userInfo.getCompSeq());
      return UserTokenRes.builder()
          .roles(userInfo.getRoleId())
          .content(content)
          .build();
    }
  }


  @Override
  public UserRoleRes getUserRoleInfo(String userId) {
    return userStore.getUserRoleInfo(userId);
  }

  @Override
  public UserResult getUserInfoByName(Map<String, Object> param) {
    return userStore.findUserByUserName(param);
  }

  @Override
  public String getUserPassword(String userId) {
    return userStore.getUserPassword(userId);
  }

  @Override
  public int modifyUser(Map<String, Object> modifyParam) {
    return userStore.modifyUser(modifyParam);
  }

  @Override
  public int registerAuditLog(RegisterAuditLogParam userParam) {

    Map<String, Object> befMap = new Gson().fromJson(
        userParam.getBeforeValue(), new TypeToken<HashMap<String, Object>>() {
        }.getType());
    Map<String, Object> aftMap = new Gson().fromJson(
        userParam.getAfterValue(), new TypeToken<HashMap<String, Object>>() {
        }.getType());

    Map<String, Object> updatedFields = svcUtils.compareUpdatedField(befMap, aftMap);
    JSONObject befObj = new JSONObject();
    JSONObject aftObj = new JSONObject();
    for (String key : updatedFields.keySet()) {
      befObj.put(key, updatedFields.get(key).toString().split("::")[0]);
      aftObj.put(key, updatedFields.get(key).toString().split("::")[1]);
    }

    return userAuditLogStore.registerUserAuditLog(userParam);

  }

  @Override
  public int registerUserCompany(UserCompanyParam userCompanyParam) {
    return userCompanyStore.registerUserCompany(userCompanyParam);
  }

  @Override
  public UserCompany getUserCompany(Integer userSeq) {
    return userCompanyStore.getUserCompanyByUserSeq(userSeq);
  }

  @Override
  public JSONObject requestAuthTokenValidation(String token, String authType, String authInfo)
      throws Exception {
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(authServiceUrl + "/token/validation")
        .queryParam("authType", authType)
        .queryParam("authInfo", authInfo);

    log.debug("url : {}", builder.toUriString());

    JSONObject sendHeaders = new JSONObject();
    sendHeaders.put("x-auth-token", token);

    JSONObject sendData = new JSONObject();
    sendData.put("url", builder.toUriString());
    sendData.put("method", HttpMethod.GET);
    sendData.put("content-type", "application/json");
    sendData.put("headers", sendHeaders);

    ResponseEntity<String> res = RestUtil.sendRestApi(sendData);
    log.info("http code : {}", res.getStatusCode().value());

    JSONParser parser = new JSONParser();
    Object obj = parser.parse(res.getBody());
    JSONObject jsonObj = (JSONObject) obj;

    return jsonObj;
  }

  @Override
  public List<UserGridReturnResult> getUserGridListByParam(Map param) {
    return userStore.getUserGridListByParam(param);
  }

  @Override
  public Integer getUserGridTotalCntByParam(Map param) {
    return userStore.getUserGridTotalCntByParam(param);
  }

  public ResponseEntity<String> requestUpdateAccountToLogin(Map<String, Object> userInfo)
      throws Exception {

    JSONObject body = new JSONObject(userInfo);

    JSONObject sendData = new JSONObject();
    sendData.put("url", loginServiceUrl + "/users");
    sendData.put("method", HttpMethod.POST);
    sendData.put("content-type", "application/json");
    sendData.put("params", body);

    ResponseEntity<String> res = RestUtil.sendRestApi(sendData);
    log.info("http code : {}", res.getStatusCode().value());

    return res;
  }


}
