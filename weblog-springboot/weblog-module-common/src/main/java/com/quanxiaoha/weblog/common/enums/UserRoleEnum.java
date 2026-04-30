package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    ROLE_ADMIN("ROLE_ADMIN", "管理员"),
    ROLE_USER("ROLE_USER", "普通用户");

    private String code;
    private String description;

    public static UserRoleEnum valueOfCode(String code) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
