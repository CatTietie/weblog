package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaticPageTypeEnum {

    ARTICLE("ARTICLE", "文章页"),
    INDEX("INDEX", "首页"),
    CATEGORY("CATEGORY", "分类页"),
    TAG("TAG", "标签页"),
    ARCHIVE("ARCHIVE", "归档页");

    private final String code;
    private final String description;
}
