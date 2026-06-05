package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_tenant")
public class TenantDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String domain;

    private String logo;

    private String description;

    private Integer status;

    private Long adminUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
