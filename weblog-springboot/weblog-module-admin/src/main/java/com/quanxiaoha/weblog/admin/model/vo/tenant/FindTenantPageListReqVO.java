package com.quanxiaoha.weblog.admin.model.vo.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTenantPageListReqVO {

    private Long current = 1L;

    private Long size = 10L;

    private String name;

    private Integer status;
}
