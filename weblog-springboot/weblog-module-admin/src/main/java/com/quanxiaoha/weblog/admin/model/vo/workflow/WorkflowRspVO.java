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
public class WorkflowRspVO {

    private Long id;

    private String name;

    private String description;

    private String triggerType;

    private String definitionJson;

    private Boolean isEnabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
