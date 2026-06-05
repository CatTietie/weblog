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
@TableName("t_workflow_execution_log")
public class WorkflowExecutionLogDO {

    @TableId(type = IdType.AUTO)
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
