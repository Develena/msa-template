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

package com.msa.template.elena.entity.params.modify;


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
@Schema(description = "디바이스 Push Token 업데이트")
public class ModifyDevicePushTokenParam {

  @Schema(description = "UDID or UUID", example = "fghjklydyt56797hjnghi")
  private String udid;
  @Schema(description = "PUSH 토큰", example = "aaaaaaaaaaaaaaaaa")
  private String pushToken;
  @Schema(description = "수정자 아이디", example = "system", hidden = true)
  private String updaterId;
  @Schema(description = "수정일시", hidden = true)
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private Date updatedDt;
}
