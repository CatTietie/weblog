package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BehaviorEventTypeEnum {

    READ(1, "阅读"),
    TAG_CLICK(2, "标签点击"),
    SEARCH(3, "搜索"),
    RESUME_VIEW(4, "简历浏览");

    private Integer code;
    private String description;

    public static BehaviorEventTypeEnum valueOf(Integer code) {
        for (BehaviorEventTypeEnum type : BehaviorEventTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
