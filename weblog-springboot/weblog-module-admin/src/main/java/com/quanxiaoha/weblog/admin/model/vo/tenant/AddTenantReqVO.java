package com.quanxiaoha.weblog.admin.model.vo.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddTenantReqVO {

    @NotBlank(message = "租户名称不能为空")
    private String name;

    @NotBlank(message = "域名不能为空")
    private String domain;

    private String logo;

    private String description;

    @NotBlank(message = "管理员用户名不能为空")
    private String adminUsername;

    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;
}
