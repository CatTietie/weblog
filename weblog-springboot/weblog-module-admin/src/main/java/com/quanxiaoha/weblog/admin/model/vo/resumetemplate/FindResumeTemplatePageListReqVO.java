package com.quanxiaoha.weblog.admin.model.vo.resumetemplate;

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
@ApiModel(value = "查询简历模板分页数据入参 VO")
public class FindResumeTemplatePageListReqVO extends BasePageQuery {

    private String name;
}
