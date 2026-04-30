package com.quanxiaoha.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "角色响应 VO")
public class RoleRspVO {

    private Long id;

    private String name;

    private String code;

    private String description;
}
