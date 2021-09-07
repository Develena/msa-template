/*
 * UserPutBody.java
 *
 *
 * 21. 3. 18. 오후 3:56
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

package com.msa.template.elena.entity.params.modify;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Schema(description = "업체 수정")
@Getter
@Setter
public class ModifyCompanyParam {

  @Schema(description = "업체 이름", example = "누리플렉스")
  String compNm;
  @Schema(description = "법인번호", example = "1234-56-789abc d")
  String corpNo;
  @Schema(description = "사업자번호", example = "123-45-6789a")
  String bizRegNo;
  @Schema(description = "업종", example = "정보통신")
  String industry;
  @Schema(description = "주소", example = "서울특별시 서초구 사평대로 16 누리빌딩")
  String compAddr;
  @Schema(description = "대표전화번호", example = "02-781-0777")
  String compTelephone;
  @Schema(description = "담당자명", example = "김누리")
  String manager;
  @Schema(description = "담당자연락처", example = "010-1234-5678")
  String managerTelephone;

  @Schema(hidden = true)
  private String updaterId;
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  @Schema(hidden = true)
  private Date updatedDt;

}
