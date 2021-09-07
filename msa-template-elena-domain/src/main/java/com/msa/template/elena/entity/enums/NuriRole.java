package com.msa.template.elena.entity.enums;

import lombok.Getter;

@Getter
public enum NuriRole {
  R001("admin"),
  R002("manager"),
  R003("operator");

  private String roleName;

  NuriRole(String roleName) {
    this.roleName = roleName;
  }

  public static String getRoleNameByCode(String code){
    for(NuriRole role : values()){
      if(role.name().equals(code)){
        return role.getRoleName();
      }
    }
    return null;
  }
}
