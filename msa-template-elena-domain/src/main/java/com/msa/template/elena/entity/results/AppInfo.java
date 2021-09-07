package com.msa.template.elena.entity.results;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AppInfo {

  private String appId;
  private String appNm;
  private String os;
  private String appVer;
  private String minVer;
  private boolean inApproval;
  private String creatorId;
  private String updaterId;

//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
//  private Date createdDt;
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
//  private Date updatedDt;

  public AppInfoResult toResponse() {
    return AppInfoResult.builder()
        .appId(this.appId)
        .os(this.os)
        .version(this.appVer)
        .inApproval(this.inApproval)
        .build();
  }

}
