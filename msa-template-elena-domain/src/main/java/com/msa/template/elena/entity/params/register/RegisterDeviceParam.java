/*
 * UserParam.java
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

package com.msa.template.elena.entity.params.register;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "모바일 장비 등록 (params)")
public class RegisterDeviceParam {

  /*@Schema(description = "디바이스일련번호")
  private Integer deviceSeq;*/
  /*@Schema(description = "사용자관리키")
  private Integer userSeq;*/
  @Schema(description = "UDID or UUID", example = "fghjklydyt56797hjnghi")
  private String udid;
  @Schema(description = "UDID 타입", example = "UUID")
  private String udidType;
  @Schema(description = "PUSH 토큰", example = "PUSHadkjflsdjaf123231aksdf")
  private String pushToken;
  @Schema(description = "APP 번호", example = "APPID")
  private String appId;
  @Schema(description = "APP 버젼", example = "v1.0")
  private String appVer;
  @Schema(description = "OS 타입", example = "IOS")
  private String osType;
  @Schema(description = "OS 버젼", example = "v1.1212")
  private String osVer;
//  @Schema(description = "상태", example = "1")
//  private String status;
  @Schema(description = "등록자 아이디", example = "system", hidden = true)
  private String creatorId;
  @Schema(description = "등록일시", hidden = true)
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date createdDt;
//  @Schema(description = "수정자 아이디", example = "system")
//  private String updaterId;
//  @Schema(description = "수정일시")
//  private Date updatedDt;

}
