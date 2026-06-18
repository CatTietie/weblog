package com.quanxiaoha.weblog.admin.model.vo.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTenantReqVO {

    @NotNull(message = "租户ID不能为空")
    private Long id;

    private String name;

    private String domain;

    private String logo;

    private String description;

    private Integer status;
}
