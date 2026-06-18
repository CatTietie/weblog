package com.quanxiaoha.weblog.admin.model.vo.resume;

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
@ApiModel(value = "创建简历入参 VO")
public class CreateResumeReqVO {

    @NotBlank(message = "简历名称不能为空")
    private String name;
}
