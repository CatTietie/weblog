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
@ApiModel(value = "更新用户画像")
public class UpdateUserProfileReqVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "标签权重不能为空")
    private String tagWeights;
}
