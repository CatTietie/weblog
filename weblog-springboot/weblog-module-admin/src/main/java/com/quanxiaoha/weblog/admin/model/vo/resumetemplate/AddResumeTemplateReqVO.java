package com.quanxiaoha.weblog.admin.model.vo.resumetemplate;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "添加简历模板 VO")
public class AddResumeTemplateReqVO {

    @NotBlank(message = "模板名称不能为空")
    private String name;

    @NotBlank(message = "组件标识不能为空")
    private String componentName;

    private String thumbnail;

    private String description;

    private Integer sortOrder;
}
