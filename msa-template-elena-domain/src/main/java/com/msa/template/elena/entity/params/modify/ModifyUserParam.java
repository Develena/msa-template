package com.msa.template.elena.entity.params.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "내 정보 변경")
@Getter
@Setter
public class ModifyUserParam {

  @Schema(description = "사용자 이름", example = "홍길동")
  private String userNm;
  @Schema(description = "사용자 휴대 전화번호", example = "01012345678")
  private String cellphone;
  @Schema(description = "사용자 이메일", example = "gildong@nuriflex.co.kr")
  private String email;
  @Schema(description = "사용자 생일", example = "19940418")
  private String birthday;

}
