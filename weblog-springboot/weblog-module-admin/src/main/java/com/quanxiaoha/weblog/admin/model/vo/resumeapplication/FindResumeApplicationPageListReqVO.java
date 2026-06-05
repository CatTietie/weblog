package com.quanxiaoha.weblog.admin.model.vo.resumeapplication;

import com.quanxiaoha.weblog.common.model.BasePageQuery;
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
@ApiModel(value = "查询投递记录分页数据入参 VO")
public class FindResumeApplicationPageListReqVO extends BasePageQuery {

    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;

    private Integer status;
}
