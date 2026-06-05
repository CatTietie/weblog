package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatusEnum {

    PENDING(0, "待审核"),
    PUBLISHED(1, "已发布");

    private Integer code;
    private String description;

    public static CommentStatusEnum valueOf(Integer code) {
        for (CommentStatusEnum status : CommentStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
