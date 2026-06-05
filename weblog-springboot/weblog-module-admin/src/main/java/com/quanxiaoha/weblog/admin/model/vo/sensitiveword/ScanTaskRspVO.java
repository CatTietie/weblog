package com.quanxiaoha.weblog.admin.model.vo.sensitiveword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScanTaskRspVO {

    private Long id;

    private Integer status;

    private Integer totalArticles;

    private Integer totalComments;

    private Integer scannedCount;

    private Integer hitCount;

    private LocalDateTime createTime;

    private LocalDateTime finishTime;
}
