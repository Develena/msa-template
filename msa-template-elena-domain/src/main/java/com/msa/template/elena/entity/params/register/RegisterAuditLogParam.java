/*
 * UserPostBody.java
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

package com.msa.template.elena.entity.params.register;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Schema(description = "사용자 변경 이력 등록")
@Getter
@Setter
public class RegisterAuditLogParam {

  private Integer userSeq;
  private String userId;
  private String userNm;

  private String act;
  private String updateItem;
  private String beforeValue;
  private String afterValue;

  private String ip;
  private String creatorId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date createdDt;

}
