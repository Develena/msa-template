package com.msa.template.elena.entity.results;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "앱정보")
@Getter
@Setter
@ToString
@Builder
@JsonInclude(Include.NON_NULL)
public class AppInfoResult {

  @Schema(description = "AppId", example = "앱 아이디")
  private String appId;
  @Schema(description = "OS", example = "IOS")
  private String os;
  @Schema(description = "앱 버전", example = "1.0.0")
  private String version;
  @Schema(description = "심사중 상태", example = "true")
  boolean inApproval;

}
