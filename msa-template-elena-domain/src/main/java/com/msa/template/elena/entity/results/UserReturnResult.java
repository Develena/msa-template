/*
 * UserResult.java
 *
 *
 * 21. 3. 15. 오전 11:17
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

package com.msa.template.elena.entity.results;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@Schema(description = "사용자 상세 정보")
public class UserReturnResult {

  @Schema(description = "관리키")
  private Integer userSeq;
  @Schema(description = "아이디")
  private String userId;
  @Schema(description = "이름")
  private String userNm;
  @Schema(description = "휴대전화번호")
  private String cellphone;
  @Schema(description = "이메일")
  private String email;
  @Schema(description = "생년월일")
  private String birthday;
  @Schema(description = "계정상태")
  private String serviceStatus;
  @Schema(description = "등록자 아이디")
  private String creatorId;
  @Schema(description = "등록일시")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date createdDt;
  @Schema(description = "수정자 아이디")
  private String updaterId;
  @Schema(description = "수정일시")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date updatedDt;
  @Schema(description = "업체 관리키")
  private Integer compSeq;
  @Schema(description = "업체명")
  private String compNm;
  @Schema(description = "권한아이디")
  private String roleId;
  @Schema(description = "권한명")
  private String roleNm;
}
