package com.quanxiaoha.weblog.admin.model.vo.recommendation;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "添加推荐配置")
public class AddRecommendConfigReqVO {

    @NotNull(message = "配置类型不能为空")
    private Integer configType;

    private Long articleId;

    private Long tagId;

    private BigDecimal weightAdjustment;
}
