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
public class FindScanResultPageListRspVO {

    private Long id;

    private Long taskId;

    private Integer targetType;

    private Long targetId;

    private String hitWords;

    private String context;

    private Integer handled;

    private LocalDateTime createTime;

    private String targetTitle;
}
