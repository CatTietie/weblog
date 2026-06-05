package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentProviderEnum {

    NONE("none", "无评论"),
    GISCUS("giscus", "Giscus"),
    WALINE("waline", "Waline");

    private final String code;
    private final String description;
}
