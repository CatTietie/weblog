package com.quanxiaoha.weblog.web.model.vo.behavior;

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
@ApiModel(value = "上报推荐点击")
public class ReportRecommendClickReqVO {

    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    @NotNull(message = "推荐来源不能为空")
    private Integer source;
}
