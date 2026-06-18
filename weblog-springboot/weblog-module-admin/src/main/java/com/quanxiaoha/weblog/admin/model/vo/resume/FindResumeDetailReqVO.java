package com.quanxiaoha.weblog.admin.model.vo.resume;

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
@ApiModel(value = "查询简历详情入参 VO")
public class FindResumeDetailReqVO {

    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;
}
