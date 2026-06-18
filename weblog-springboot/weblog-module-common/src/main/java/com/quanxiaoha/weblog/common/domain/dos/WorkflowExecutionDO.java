package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_workflow_execution")
public class WorkflowExecutionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workflowId;

    private String status;

    private String triggerData;

    private String currentNodeId;

    private String contextJson;

    private LocalDateTime delayUntil;

    private String errorMessage;

    private Boolean isDebug;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
