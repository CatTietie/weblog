package com.quanxiaoha.weblog.admin.model.vo.workflow;

import com.quanxiaoha.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询工作流分页列表入参 VO")
public class FindWorkflowPageListReqVO extends BasePageQuery {

    private String name;

    private String triggerType;
}
