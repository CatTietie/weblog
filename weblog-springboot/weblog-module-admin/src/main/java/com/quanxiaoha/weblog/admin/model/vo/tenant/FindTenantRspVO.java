package com.quanxiaoha.weblog.admin.model.vo.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTenantRspVO {

    private Long id;

    private String name;

    private String domain;

    private String logo;

    private String description;

    private Integer status;

    private String adminUsername;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
