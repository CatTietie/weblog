package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecommendConfigTypeEnum {

    PIN_TOP(1, "置顶文章"),
    TAG_BLOCK(2, "标签屏蔽"),
    WEIGHT_ADJUST(3, "权重调整");

    private Integer code;
    private String description;

    public static RecommendConfigTypeEnum valueOf(Integer code) {
        for (RecommendConfigTypeEnum type : RecommendConfigTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
