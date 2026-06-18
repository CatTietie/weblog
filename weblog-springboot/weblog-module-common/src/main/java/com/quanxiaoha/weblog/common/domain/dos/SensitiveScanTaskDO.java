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
@TableName("t_sensitive_scan_task")
public class SensitiveScanTaskDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer status;

    private Integer totalArticles;

    private Integer totalComments;

    private Integer scannedCount;

    private Integer hitCount;

    private LocalDateTime createTime;

    private LocalDateTime finishTime;

    private Long tenantId;
}
