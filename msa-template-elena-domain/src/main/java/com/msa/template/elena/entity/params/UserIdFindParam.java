package com.msa.template.elena.entity.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사용자 아이디 찾기")
@Getter
@Setter
public class UserIdFindParam {

  @Schema(description = "이름", example = "홍길동", required = true)
  private String userName;
  @Schema(description = "본인인증 받은 수단", example = "sms", required = true)
  private String authType;
  @Schema(description = "휴대번호", example = "01012345678", required = true)
  private String authInfo;
}
