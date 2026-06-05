package com.quanxiaoha.weblog.admin.model.vo.resumeapplication;

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
@ApiModel(value = "删除投递记录入参 VO")
public class DeleteResumeApplicationReqVO {

    @NotNull(message = "记录 ID 不能为空")
    private Long id;
}
