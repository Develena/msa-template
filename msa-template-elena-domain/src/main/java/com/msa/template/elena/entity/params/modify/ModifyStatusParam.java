package com.msa.template.elena.entity.params.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사용자 상태 변경")
@Getter
@Setter
public class ModifyStatusParam {

  @Schema(description = "서비스 상태", example = "US000")
  private String serviceStatus;

}
