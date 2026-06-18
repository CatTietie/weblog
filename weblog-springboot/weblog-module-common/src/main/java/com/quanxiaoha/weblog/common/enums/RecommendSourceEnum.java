package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecommendSourceEnum {

    HOME(1, "首页推荐"),
    DETAIL_RELATED(2, "详情页相关推荐");

    private Integer code;
    private String description;

    public static RecommendSourceEnum valueOf(Integer code) {
        for (RecommendSourceEnum type : RecommendSourceEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
