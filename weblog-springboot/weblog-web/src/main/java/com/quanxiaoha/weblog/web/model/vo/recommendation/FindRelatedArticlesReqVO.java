package com.quanxiaoha.weblog.web.model.vo.recommendation;

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
@ApiModel(value = "获取相关推荐请求")
public class FindRelatedArticlesReqVO {

    @NotNull(message = "文章ID不能为空")
    private Long articleId;
}
