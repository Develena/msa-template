package com.msa.template.elena.entity.enums;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BooleanResultType {

  SUCCESS("success"),
  FAILURE("failure"),
  OK("ok"),
  FAIL("fail"),
  VALID("valid"),
  INVALID("invalid"),
  Y("1"),
  N("0");

  @Getter
  private String msg;

  private static final Map<String, BooleanResultType> lookup = Maps.uniqueIndex(
      Arrays.asList(BooleanResultType.values()),
      BooleanResultType::getMsg
  );

  public static BooleanResultType get(String msg) {
    return lookup.get(msg);
  }
}

