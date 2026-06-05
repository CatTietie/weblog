package com.quanxiaoha.weblog.admin.model.vo.recommendation;

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
@ApiModel(value = "删除推荐配置")
public class DeleteRecommendConfigReqVO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
