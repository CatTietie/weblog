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
public class WorkflowExecutionRspVO {

    private Long id;

    private Long workflowId;

    private String status;

    private String triggerData;

    private String currentNodeId;

    private String errorMessage;

    private Boolean isDebug;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
