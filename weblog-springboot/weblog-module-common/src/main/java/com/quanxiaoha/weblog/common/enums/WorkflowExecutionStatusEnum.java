package com.quanxiaoha.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkflowExecutionStatusEnum {

    RUNNING("RUNNING", "运行中"),
    WAITING_DELAY("WAITING_DELAY", "等待延时"),
    COMPLETED("COMPLETED", "已完成"),
    FAILED("FAILED", "失败");

    private String code;
    private String description;
}
