package com.msa.template.elena.entity.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BooleanResultCode {

  SUCCESS("success", Arrays
      .asList(BooleanResultType.SUCCESS, BooleanResultType.OK, BooleanResultType.VALID,
          BooleanResultType.Y)),
  FAILURE("failure", Arrays
      .asList(BooleanResultType.FAILURE, BooleanResultType.FAIL, BooleanResultType.INVALID,
          BooleanResultType.N)),
  NONE("none", Collections.EMPTY_LIST);

  @Getter
  private String code;
  @Getter
  private List<BooleanResultType> msgList;

  public static BooleanResultCode lookup(BooleanResultType result) {
    return
        Arrays.stream(BooleanResultCode.values())
            .filter(group -> group.hasMessage(result))
            .findAny()
            .orElse(NONE);
  }

  public boolean hasMessage(BooleanResultType result) {
    return msgList.stream()
        .anyMatch(msg -> msg == result);
  }
}
