package com.quanxiaoha.weblog.admin.model.vo.staticsite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TriggerGenerationReqVO {

    @NotBlank(message = "生成类型不能为空")
    private String taskType;

    private Boolean autoDeploy;
}
