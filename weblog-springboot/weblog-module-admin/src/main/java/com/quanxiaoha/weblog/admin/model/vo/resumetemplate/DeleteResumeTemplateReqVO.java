package com.quanxiaoha.weblog.admin.model.vo.resumetemplate;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "删除简历模板 VO")
public class DeleteResumeTemplateReqVO {

    @NotNull(message = "模板 ID 不能为空")
    private Long id;
}
