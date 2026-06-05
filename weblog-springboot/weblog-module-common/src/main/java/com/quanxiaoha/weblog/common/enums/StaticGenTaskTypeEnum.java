package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaticGenTaskTypeEnum {

    FULL("FULL", "全量生成"),
    INCREMENTAL("INCREMENTAL", "增量生成");

    private final String code;
    private final String description;
}
