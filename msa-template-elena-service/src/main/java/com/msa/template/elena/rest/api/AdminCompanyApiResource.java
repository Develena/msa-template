/*
 * UserResource2.java
 *
 * 15/06/21, 02:00 PM
 *
 * Copyright (c) 2021 NURIFLEX, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of NURIFLEX, Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURIFLEX, Inc.
 *
 * For more information on this product, please see
 * http://www.nuriflex.co.kr
 *
 */

package com.msa.template.elena.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.template.elena.rest.AbstractBaseResource;
import com.nuri.green.onm.user.entity.enums.BooleanResultType;
import com.nuri.green.onm.user.entity.enums.NuriRole;
import com.nuri.green.onm.user.entity.enums.ResultErrorCode;
import com.nuri.green.onm.user.entity.enums.ResultResCode;
import com.nuri.green.onm.user.entity.jwt.User;
import com.nuri.green.onm.user.entity.page.OrderByMap;
import com.nuri.green.onm.user.entity.params.CompanyPageParam;
import com.nuri.green.onm.user.entity.params.modify.ModifyCompanyParam;
import com.nuri.green.onm.user.entity.params.register.RegisterCompanyParam;
import com.nuri.green.onm.user.entity.results.AdminCompanyGridExcelResult;
import com.nuri.green.onm.user.entity.results.Company;
import com.nuri.green.onm.user.entity.results.CompanyReturnResult;
import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.green.onm.user.entity.results.PagingGridResult;
import com.nuri.green.onm.user.entity.results.ResultMessage;
import com.nuri.green.onm.user.entity.results.UserCompany;
import com.nuri.green.onm.user.exception.InvalidRequestException;
import com.nuri.green.onm.user.exception.OperationException;
import com.nuri.green.onm.user.exception.UnauthenticatedException;
import com.nuri.green.onm.user.spec.CompanyService;
import com.nuri.green.onm.user.spec.UserCompanyService;
import com.nuri.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "AdminUserApiResource", description = "Admin User Service API")
@RestController
public class AdminCompanyApiResource extends AbstractBaseResource {

  @Value("${project.code}")
  private String svcName;

  @Autowired
  private CompanyService companyService;

  @Autowired
  private UserCompanyService userCompanyService;

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "업체 리스트 조회(Excel)",
      description =
          "<b>orders 가능 컬럼</b>(아닐경우 무시)<br>"
              + "- createdDt<br>"
              + "- compNm")
  @GetMapping("/api/gnd-onm-user/v1.0/admin/company/excel")
  public ResponseEntity<ResultMessage<AdminCompanyGridExcelResult>> getCompanyGridExcelAdmin(
      @ParameterObject CompanyPageParam companyPageParam) throws OperationException {

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    ResultMessage message = new ResultMessage();

    // 2. 필수 파라미터 validation

    try {
      // 3. Make param
      if (!StringUtils.isEmpty(companyPageParam.getOrders())) {
        companyPageParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(companyPageParam.getOrderMap(),
            "createdDt", "compNm");
        companyPageParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      companyPageParam.setOffset(null);
      companyPageParam.setLimit(null);
      HashMap<String, Object> param = mapper.convertValue(companyPageParam, HashMap.class);

      // 4. Get grid data
      List<CompanyReturnResult> gridList = companyService.getCompanyGridListByParam(param);

      // 5.  Make Excel
      Workbook workbook = new HSSFWorkbook();

      Sheet sheet = workbook.createSheet("User List");
      Row row = sheet.createRow(0);
      int cellIdx = 0;
      row.createCell(cellIdx++).setCellValue("번호");
      row.createCell(cellIdx++).setCellValue("업체명");
      row.createCell(cellIdx++).setCellValue("법인번호");
      row.createCell(cellIdx++).setCellValue("사업자번호");
      row.createCell(cellIdx++).setCellValue("업종");
      row.createCell(cellIdx++).setCellValue("주소");
      row.createCell(cellIdx++).setCellValue("대표전화번호");
      row.createCell(cellIdx++).setCellValue("담당자명");
      row.createCell(cellIdx++).setCellValue("담당자연락처");
      row.createCell(cellIdx++).setCellValue("등록일");

      for (int i = 0; i < gridList.size(); i++) {
        row = sheet.createRow(i + 1);
        CompanyReturnResult elem = gridList.get(i);
        cellIdx = 0;
        row.createCell(cellIdx++).setCellValue(gridList.size() - i);
        row.createCell(cellIdx++).setCellValue(elem.getCompNm());
        row.createCell(cellIdx++).setCellValue(elem.getCorpNo());
        row.createCell(cellIdx++).setCellValue(elem.getBizRegNo());
        row.createCell(cellIdx++).setCellValue(elem.getIndustry());
        row.createCell(cellIdx++).setCellValue(elem.getCompAddr());
        row.createCell(cellIdx++).setCellValue(elem.getCompTelephone());
        row.createCell(cellIdx++).setCellValue(elem.getManager());
        row.createCell(cellIdx++).setCellValue(elem.getManagerTelephone());
        row.createCell(cellIdx++).setCellValue(elem.getCreatedDt());
      }

      String fileName = "CompanyList_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".xls";
      File rolePath = new File(NuriRole.R001.getRoleName());
      if (!rolePath.exists()) {
        rolePath.mkdir();
      }
      File userIdPath = new File(rolePath + File.separator + accessUser.getUserId());
      if (!userIdPath.exists()) {
        userIdPath.mkdir();
      }
      File f = new File(userIdPath.getPath() + File.separator + fileName);
      FileOutputStream fos = new FileOutputStream(f, false);
      workbook.write(fos);
      fos.close();

      // 6. Return
      AdminCompanyGridExcelResult result = new AdminCompanyGridExcelResult();
      result.setCallbackURL("/api/gnd-onm-user/v1.0/admin/excel/download/" + fileName);

      message.setPayload(result);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[업체 리스트(엑셀)조회] 실패", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "업체 리스트(엑셀)"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "업체 리스트 조회(Grid)",
      description =
          "<b>orders 가능 컬럼</b>(아닐경우 무시)<br>"
              + "- createdDt<br>"
              + "- compNm")
  @GetMapping("/api/gnd-onm-user/v1.0/admin/company")
  public ResponseEntity<ResultMessage<PagingGridResult<CompanyReturnResult>>> getCompanyGridAdmin(
      @ParameterObject CompanyPageParam companyPageParam) throws OperationException {
    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    ResultMessage message = new ResultMessage();

    // 2. 필수 파라미터 validation

    try {
      // 3. Make param
      if (!StringUtils.isEmpty(companyPageParam.getOrders())) {
        companyPageParam.convertToQueryField();
        List<OrderByMap> orderByMapList = orderValidation(companyPageParam.getOrderMap(),
            "createdDt", "compNm");
        companyPageParam.setOrderMap(orderByMapList);
      }
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> param = mapper.convertValue(companyPageParam, HashMap.class);

      // 4. Get grid data
      List<CompanyReturnResult> gridList = companyService.getCompanyGridListByParam(param);
      Integer gridTotal = companyService.getCompanyGridTotalCntByParam(param);
      gridList.forEach(elem -> {
        if (elem.getManagerTelephone() != null) {
          elem.setManagerTelephone(elem.getManagerTelephone().replaceAll(".{4}$", "****"));
        }
      });

      // 5. Return
      PagingGridResult<CompanyReturnResult> pagingGridResult = new PagingGridResult();
      if (companyPageParam.getOffset() != null) {
        pagingGridResult.setOffset(companyPageParam.getOffset());
      }
      if (companyPageParam.getLimit() != null) {
        pagingGridResult.setLimit(companyPageParam.getLimit());
      }
      pagingGridResult.setTotalCnt(gridTotal);
      pagingGridResult.setDataCnt(gridList.size());
      pagingGridResult.setData(gridList);

      message.setPayload(pagingGridResult);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[업체 리스트 조회] 실패", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "업체 리스트"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER)
  })
  @Operation(summary = "업체 등록")
  @PostMapping("/api/gnd-onm-user/v1.0/admin/company")
  public ResponseEntity<ResultMessage<String>> registerCompanyAdmin(
      @RequestBody RegisterCompanyParam registerCompanyParam) throws OperationException {
    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User accessUser = (User) authentication.getPrincipal();

    ResultMessage message = new ResultMessage();

    // 2. 필수 파라미터 validation
    if (StringUtils.isEmpty(registerCompanyParam.getCompNm())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "compNm"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getBizRegNo())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "bizRegNo"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getCorpNo())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "corpNo"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getIndustry())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "industry"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getCompAddr())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "compAddr"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getCompTelephone())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "compTelephone"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getManager())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(), "manager"));
    }
    if (StringUtils.isEmpty(registerCompanyParam.getManagerTelephone())) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(),
              "managerTelephone"));
    }

    // 3. 회사 중복 검사
    Company company = companyService
        .checkCompany(registerCompanyParam.getBizRegNo(), registerCompanyParam.getCorpNo());
    log.debug("company : {}", company);
    if (company != null) {
      log.error("[업체 등록] 중복된 업체");
      throw new OperationException(ResultResCode.US5005.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.DUPLICATED_ERROR.name(),
              registerCompanyParam.getCompNm()));
    }

    try {
      // 4. 회사 생성.
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> companyParam = mapper
          .convertValue(registerCompanyParam, HashMap.class);
      companyParam.put("serviceStatus", 1);
      companyParam.put("creatorId", accessUser.getUserId());
      companyParam.put("createdDt", DateUtil.getCurrentDateTime());
      int result = companyService.registerCompany(companyParam);
      if (result > 0) {
        message.setPayload(BooleanResultType.SUCCESS.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[업체 등록] 실패 - 쿼리 결과 : 0");
        throw new OperationException(ResultResCode.US5001.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "업체"));
      }

    } catch (Exception e) {
      log.error("[업체 등록] 실패", e);
      throw new OperationException(ResultResCode.US5001.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.REGISTRATION_FAILED.name(), "업체"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "업체 정보 조회")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "compSeq", description = "업체 관리키", example = "1", in = ParameterIn.PATH)
  })
  @GetMapping("/api/gnd-onm-user/v1.0/admin/company/{compSeq}")
  public ResponseEntity<ResultMessage<CompanyReturnResult>> getCompanyAdmin(
      @PathVariable("compSeq") Integer compSeq) throws OperationException {

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }

    ResultMessage<CompanyReturnResult> message = new ResultMessage();

    // 2. Company 검색
    Company company = companyService.getCompanyByCompSeq(compSeq);
    if (company == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), "해당 업체"));
    }

    try {
      // 3. Result 생성
      CompanyReturnResult companyReturnResult = CompanyReturnResult.builder()
          .compSeq(company.getCompSeq())
          .compNm(company.getCompNm())
          .corpNo(company.getCorpNo())
          .bizRegNo(company.getBizRegNo())
          .industry(company.getIndustry())
          .compAddr(company.getCompAddr())
          .compTelephone(company.getCompTelephone())
          .manager(company.getManager())
          .managerTelephone(company.getManagerTelephone())
          .createdDt(company.getCreatedDt())
          .build();

      message.setPayload(companyReturnResult);
      message.setCode(svcName + ResultResCode.US2000.getCode());
      message.setMessage(ResultResCode.US2000.getKoMsg());

    } catch (Exception e) {
      log.error("[업체 정보 조회] 실패", e);
      throw new OperationException(ResultResCode.US5000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.QUERY_FAILED.name(), "해당 업체"));
    }

    // 4. 리턴
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "업체 정보 변경")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "compSeq", description = "업체 관리키", example = "1", in = ParameterIn.PATH)
  })
  @PutMapping("/api/gnd-onm-user/v1.0/admin/company/{compSeq}")
  public ResponseEntity<ResultMessage<String>> modifyCompanyAdmin(
      @PathVariable("compSeq") Integer compSeq,
      @RequestBody ModifyCompanyParam modifyCompanyParam,
      HttpServletRequest request) throws OperationException {
    log.info("Controller - modifyCompany");

    ResultMessage message = new ResultMessage();

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User user = (User) authentication.getPrincipal();

    // 2. req 확인.
    if (modifyCompanyParam == null) {
      throw new InvalidRequestException(ResultResCode.US4000.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.MANDATORY_PARAM_ERROR.name(),
              "modifyCompanyParam"));
    }

    Company company = companyService.getCompanyByCompSeq(compSeq);
    if (company == null) {
      throw new OperationException(ResultResCode.US5006.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), "해당 업체"));
    }

    try {
      // 3. 수정사항 반영 전처리
      HashMap<String, Object> modifyParam = new HashMap<>();
      if (updateValidation(company.getCompNm(), modifyCompanyParam.getCompNm())) {
        modifyParam.put("compNm", modifyCompanyParam.getCompNm());
      }
      if (updateValidation(company.getCorpNo(), modifyCompanyParam.getCorpNo())) {
        modifyParam.put("corpNo", modifyCompanyParam.getCorpNo());
      }
      if (updateValidation(company.getBizRegNo(), modifyCompanyParam.getBizRegNo())) {
        modifyParam.put("bizRegNo", modifyCompanyParam.getBizRegNo());
      }
      if (updateValidation(company.getIndustry(), modifyCompanyParam.getIndustry())) {
        modifyParam.put("industry", modifyCompanyParam.getIndustry());
      }
      if (updateValidation(company.getCompAddr(), modifyCompanyParam.getCompAddr())) {
        modifyParam.put("compAddr", modifyCompanyParam.getCompAddr());
      }
      if (updateValidation(company.getCompTelephone(), modifyCompanyParam.getCompTelephone())) {
        modifyParam.put("compTelephone", modifyCompanyParam.getCompTelephone());
      }
      if (updateValidation(company.getManager(), modifyCompanyParam.getManager())) {
        modifyParam.put("manager", modifyCompanyParam.getManager());
      }
      if (updateValidation(company.getManagerTelephone(),
          modifyCompanyParam.getManagerTelephone())) {
        modifyParam.put("managerTelephone", modifyCompanyParam.getManagerTelephone());
      }

      // 4. 수정사항 반영
      Date updateDt = DateUtil.getCurrentDateTime();
      modifyParam.put("compSeq", compSeq);
      modifyParam.put("updaterId", user.getUserId());
      modifyParam.put("updatedDt", updateDt);
      int result = companyService.modifyCompany(modifyParam);

      if (result > 0) {
        // 5. ResultMessage 생성
        message.setPayload(BooleanResultType.SUCCESS.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[업체 정보 변경] 실패 - 쿼리 결과 : 0");
        throw new OperationException(ResultResCode.US5003.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "해당 업체 정보"));
      }
    } catch (Exception e) {
      log.error("[업체 정보 변경] 실패", e);
      throw new OperationException(ResultResCode.US5003.getCode(),
          ExceptionMessage
              .makeExceptionMessage(ResultErrorCode.MODIFICATION_FAILED.name(), "해당 업체 정보"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @SecurityRequirement(name = "access_token")
  @Operation(summary = "업체 삭제")
  @Parameters(value = {
      @Parameter(name = "x-access-token", description = "AccessToken", in = ParameterIn.HEADER),
      @Parameter(name = "compSeq", description = "업체 관리키", example = "1", in = ParameterIn.PATH)
  })
  @DeleteMapping("/api/gnd-onm-user/v1.0/admin/company/{compSeq}")
  public ResponseEntity<ResultMessage<String>> removeCompanyAdmin(
      @PathVariable("compSeq") Integer compSeq,
      HttpServletResponse response) throws OperationException {

    ResultMessage message = new ResultMessage();

    // 1. access_token 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthenticatedException(ResultResCode.US4010.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.LOGIN_REQUIRED.name()));
    }
    User user = (User) authentication.getPrincipal();

    // 2. req 확인.
    try {
      List<UserCompany> userCompanies = userCompanyService.checkUserCompanyByCompSeq(compSeq);
      if (userCompanies != null && !userCompanies.isEmpty()) {
        log.error("[업체 삭제] 실패 - 업체에 등록된 사용자들이 존재.");
        throw new OperationException(ResultResCode.US5004.getCode(),
            ExceptionMessage.makeExceptionMessage(ResultErrorCode.REMOVAL_FAILED.name(), "해당 업체"));
      }

      // 3. 삭제
      int result = companyService.deleteCompany(compSeq);
      if (result > 0) {
        // 4. ResultMessage 생성
        message.setPayload(BooleanResultType.SUCCESS.getMsg());
        message.setCode(svcName + ResultResCode.US2000.getCode());
        message.setMessage(ResultResCode.US2000.getKoMsg());
      } else {
        log.error("[업체 삭제] 실패 - 업체가 존재하지 않음.");
        throw new OperationException(ResultResCode.US5006.getCode(),
            ExceptionMessage
                .makeExceptionMessage(ResultErrorCode.ENTITY_NOT_FOUND.name(), "해당 업체"));
      }
    } catch (Exception e) {
      log.error("[업체 삭제] 실패", e);
      throw new OperationException(ResultResCode.US5004.getCode(),
          ExceptionMessage.makeExceptionMessage(ResultErrorCode.REMOVAL_FAILED.name(), "해당 업체"));
    }

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}
