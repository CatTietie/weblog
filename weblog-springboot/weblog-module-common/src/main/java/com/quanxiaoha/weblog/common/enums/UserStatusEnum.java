package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    ENABLED(0, "启用"),
    DISABLED(1, "禁用");

    private Integer code;
    private String description;

    public static UserStatusEnum valueOf(Integer code) {
        for (UserStatusEnum status : UserStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
