package com.quanxiaoha.weblog.admin.model.vo.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowExecutionLogRspVO {

    private Long id;

    private Long executionId;

    private String nodeId;

    private String nodeType;

    private String nodeLabel;

    private String status;

    private String inputData;

    private String outputData;

    private String errorMessage;

    private LocalDateTime executeTime;

    private Long durationMs;
}
