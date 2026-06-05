package com.quanxiaoha.weblog.admin.model.vo.workflow;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新工作流入参 VO")
public class UpdateWorkflowReqVO {

    @NotNull(message = "工作流ID不能为空")
    private Long id;

    @NotBlank(message = "工作流名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "触发类型不能为空")
    private String triggerType;

    @NotBlank(message = "工作流定义不能为空")
    private String definitionJson;
}
