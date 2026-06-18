package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkflowTriggerTypeEnum {

    ARTICLE_PUBLISHED("ARTICLE_PUBLISHED", "文章发布"),
    COMMENT_RECEIVED("COMMENT_RECEIVED", "收到评论"),
    USER_REGISTERED("USER_REGISTERED", "新用户注册");

    private String code;
    private String description;
}
