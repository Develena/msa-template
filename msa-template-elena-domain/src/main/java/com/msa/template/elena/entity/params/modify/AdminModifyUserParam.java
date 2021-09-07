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
public class AdminModifyUserParam {

  @Schema(description = "사용자 아이디", example = "gildong", hidden = true)
  private String userId;
  @Schema(description = "사용자 이름", example = "홍길동")
  private String userNm;
  @Schema(description = "사용자 전화번호", example = "021234567")
  private String telephone;
  @Schema(description = "사용자 휴대 전화번호", example = "01012345678")
  private String cellphone;
  @Schema(description = "사용자 패스워드", example = "1q2w3e4r", hidden = true)
  private String password;
  @Schema(description = "사용자 이메일", example = "gildong@nuriflex.co.kr")
  private String email;
  @Schema(description = "사용자 생일", example = "19940418")
  private String birthday;
  @Schema(description = "사용자 성별", example = "남")
  private String gender;
  @Schema(description = "사용자 우편번호", example = "06552")
  private String zipcode;
  @Schema(description = "사용자 주소", example = "서울특별시 서초구 사평대로 16")
  private String addr;
  @Schema(description = "사용자 상세주소", example = "7층 기술연구소")
  private String addr2;
  @Schema(description = "서비스 상태", example = "US000")
  private String serviceStatus;

  @Schema(description = "수정자아이디", example = "", hidden = true)
  private String updaterId;
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  @Schema(description = "수정일시", example = "", hidden = true)
  private Date updatedDt;

  @Schema(hidden = true)
  public boolean isEmpty(){
    return userNm == null
        && telephone == null
        && cellphone == null
        && email == null
        && birthday == null
        && gender == null
        && zipcode == null
        && addr == null
        && addr2 == null
        && serviceStatus == null;
  }

}
