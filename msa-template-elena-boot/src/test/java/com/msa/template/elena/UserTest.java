package com.msa.template.elena;

import com.google.gson.Gson;
import com.nuri.green.onm.user.entity.enums.NuriRole;
import com.nuri.green.onm.user.entity.jwt.User;
import com.nuri.green.onm.user.entity.params.AdminUserPageParam;
import com.nuri.green.onm.user.entity.params.register.AdminRegisterUserParam;
import com.nuri.green.onm.user.entity.results.PagingGridResult;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserGridExcelResult;
import com.nuri.green.onm.user.exception.AuthorizedFailException;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.rest.api.AdminUserApiResource;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class UserTest {

  @Autowired
  AdminUserApiResource adminUserApiResource;

  @Test
  public void getUserGridAdminTest() throws Exception {
    // Test
    // 1. TOKEN_INVALID_ERROR
    log.info("## TOKEN_INVALID_ERROR test");
    AdminUserPageParam param = new AdminUserPageParam();
    param.setRoleId(NuriRole.R001.name());
    param.setOffset(0);
    param.setLimit(20);
    try{
      adminUserApiResource.getUserGridAdmin(param);
    } catch (AuthorizedFailException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    getAccessToken("admin", NuriRole.R001);
    // 2. MANDATORY_PARAM { roleId, offset, limit }
    log.info("## MANDATORY_PARAM test");
    // No roleId
    param = new AdminUserPageParam();
    param.setOffset(0);
    param.setLimit(20);
    try{
      adminUserApiResource.getUserGridAdmin(param);
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
    // No offset
    param = new AdminUserPageParam();
    param.setRoleId(NuriRole.R001.name());
    param.setLimit(20);
    try{
      adminUserApiResource.getUserGridAdmin(param);
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
    // No limit
    param = new AdminUserPageParam();
    param.setRoleId(NuriRole.R001.name());
    param.setOffset(0);
    try{
      adminUserApiResource.getUserGridAdmin(param);
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    // 3. OK
    log.info("## OK case test");
    param = new AdminUserPageParam();
    param.setRoleId(NuriRole.R001.name());
    param.setOffset(0);
    param.setLimit(20);
    try{
      ResponseEntity<ResultMessage<PagingGridResult>> responseEntity = adminUserApiResource.getUserGridAdmin(param);
      Gson gson = new Gson();
      log.info(gson.toJson(responseEntity));
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
  }

  @Test
  public void getUserGridExcelAdminTest() throws Exception {
    // Test
    // 1. TOKEN_INVALID_ERROR
    log.info("## TOKEN_INVALID_ERROR test");
    AdminUserPageParam param = new AdminUserPageParam();
    param.setRoleId(NuriRole.R001.name());
    try{
      adminUserApiResource.getUserGridExcelAdmin(param);
    } catch (AuthorizedFailException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    getAccessToken("admin", NuriRole.R001);
    // 2. MANDATORY_PARAM { roleId }
    log.info("## MANDATORY_PARAM test");
    // No roleId
    param = new AdminUserPageParam();
    try{
      adminUserApiResource.getUserGridExcelAdmin(param);
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    // 3. OK
    log.info("## OK case test");
    param = new AdminUserPageParam();
    param.setRoleId(NuriRole.R001.name());
    try{
      ResponseEntity<ResultMessage<UserGridExcelResult>> responseEntity = adminUserApiResource.getUserGridExcelAdmin(param);
      String callbackUrl = responseEntity.getBody().getPayload().getCallbackURL();
      Gson gson = new Gson();
      log.info(gson.toJson(responseEntity));
      getExcelDownloadAdminTest(callbackUrl);
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
  }

  public void getExcelDownloadAdminTest(String callbackUrl) throws Exception {
    // Test
    String[] urlSplit = callbackUrl.split(File.separator);
    String fileName = urlSplit[urlSplit.length-1];
    // 1. MANDATORY_PARAM
    log.info("## MANDATORY_PARAM test");
    // No fileName
    try{
      adminUserApiResource.getExcelDownLoadAdmin(null);
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    // 2. OK
    log.info("## OK case test");
    try{
      ResponseEntity<Resource> responseEntity = adminUserApiResource.getExcelDownLoadAdmin(fileName);
      log.info(responseEntity.getBody().getDescription());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
  }


  @Ignore
  public void registerUserAdminTest() throws Exception {
    // Test
    // 1. TOKEN_INVALID_ERROR
    log.info("## TOKEN_INVALID_ERROR test");
    try{
      adminUserApiResource.registerUserAdmin(AdminRegisterUserParam.builder()
          .userId("test")
          .userName("test")
          .password("test")
          .cellphone("test")
          .email("test")
          .roleId(NuriRole.R001.name())
          .build());
    } catch (AuthorizedFailException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    getAccessToken("admin", NuriRole.R001);
    // 2. MANDATORY_PARAM { compSeq, birthday } at roleId == R002
    log.info("## MANDATORY_PARAM test");
    // No compSeq
    try{
      adminUserApiResource.registerUserAdmin(AdminRegisterUserParam.builder()
          .userId("test")
          .userName("test")
          .password("test")
          .cellphone("test")
          .email("test")
          .roleId(NuriRole.R002.name())
          .birthday("19700101")
          .build());
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
    // No birthday
    try{
      adminUserApiResource.registerUserAdmin(AdminRegisterUserParam.builder()
          .userId("test")
          .userName("test")
          .password("test")
          .cellphone("test")
          .email("test")
          .roleId(NuriRole.R002.name())
          .compSeq(1)
          .build());
    } catch (InvalidRequestException e){
      log.info(e.getMessage());
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    // 3. OK
    log.info("## OK case test");
    // admin User
    try{
      ResponseEntity<ResultMessage<String>> responseEntity =
          adminUserApiResource.registerUserAdmin(AdminRegisterUserParam.builder()
              .userId("test")
              .userName("test")
              .password("test")
              .cellphone("test")
              .email("test")
              .roleId(NuriRole.R001.name())
              .build());
      Gson gson = new Gson();
      log.info(gson.toJson(responseEntity));
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }

    // manager User
    try{
      ResponseEntity<ResultMessage<String>> responseEntity =
          adminUserApiResource.registerUserAdmin(AdminRegisterUserParam.builder()
              .userId("testmanager")
              .userName("testmanager")
              .password("test")
              .cellphone("test")
              .email("test")
              .roleId(NuriRole.R002.name())
              .compSeq(1)
              .birthday("19700101")
              .build());
      Gson gson = new Gson();
      log.info(gson.toJson(responseEntity));
    } catch (Exception e){
      log.error(e.getMessage(),e);
      throw e;
    }
  }


  public void getAccessToken(String userId, NuriRole role){
    UserDetails userDetails = User.builder()
        .userId(userId)
        .role(role.name())
        .content("") // id, role 이외의 나머지 정보.
        .build();
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
