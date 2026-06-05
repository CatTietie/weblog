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
@ApiModel(value = "调试工作流入参 VO")
public class DebugWorkflowReqVO {

    @NotNull(message = "工作流ID不能为空")
    private Long id;

    @NotBlank(message = "模拟触发数据不能为空")
    private String triggerData;
}
