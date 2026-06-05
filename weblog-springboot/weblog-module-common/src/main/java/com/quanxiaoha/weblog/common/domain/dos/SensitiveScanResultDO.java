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
@TableName("t_sensitive_scan_result")
public class SensitiveScanResultDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Integer targetType;

    private Long targetId;

    private String hitWords;

    private String context;

    private Integer handled;

    private LocalDateTime createTime;

    private Long tenantId;
}
