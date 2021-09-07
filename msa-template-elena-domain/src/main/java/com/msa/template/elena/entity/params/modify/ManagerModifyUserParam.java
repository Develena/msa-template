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

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Schema(description = "사용자 수정")
@Getter
@Setter
public class ManagerModifyUserParam {

  @Schema(description = "사용자 이름", example = "홍길동")
  private String userNm;
  @Schema(description = "사용자 휴대 전화번호", example = "01012345678")
  private String cellphone;
  @Schema(description = "사용자 이메일", example = "gildong@nuriflex.co.kr")
  private String email;
  @Schema(description = "사용자 생일", example = "19940418")
  private String birthday;
  @Schema(description = "서비스 상태", example = "US000")
  private String serviceStatus;

  @Schema(hidden = true)
  public boolean isEmpty(){
    return userNm == null
        && cellphone == null
        && email == null
        && birthday == null
        && serviceStatus == null;
  }

}
