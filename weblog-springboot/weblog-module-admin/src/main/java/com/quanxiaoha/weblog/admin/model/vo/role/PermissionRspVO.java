package com.quanxiaoha.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "权限响应 VO")
public class PermissionRspVO {

    @ApiModelProperty(value = "权限ID")
    private Long id;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "权限编码")
    private String code;

    @ApiModelProperty(value = "类型：menu-菜单，button-按钮，api-接口")
    private String type;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "描述")
    private String description;
}
