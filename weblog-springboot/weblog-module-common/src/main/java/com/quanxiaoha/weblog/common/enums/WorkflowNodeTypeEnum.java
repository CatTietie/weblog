package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkflowNodeTypeEnum {

    TRIGGER("trigger", "触发器"),
    CONDITION("condition", "条件判断"),
    DELAY("delay", "延时等待"),
    ACTION_EMAIL("action_email", "发送邮件"),
    ACTION_WEBHOOK("action_webhook", "调用Webhook"),
    ACTION_NOTIFICATION("action_notification", "站内通知");

    private String code;
    private String description;
}
