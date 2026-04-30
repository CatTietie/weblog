package com.quanxiaoha.weblog.admin.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "编辑用户 VO")
public class UpdateUserReqVO {

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "状态：0-启用，1-禁用")
    private Integer status;
}
