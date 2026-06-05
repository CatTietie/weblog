package com.quanxiaoha.weblog.admin.model.vo.staticsite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindGenTaskPageListRspVO {

    private Long id;

    private String taskType;

    private String status;

    private String deployTarget;

    private Integer totalPages;

    private Integer generatedPages;

    private String errorMessage;

    private String deployedUrl;

    private Long durationMs;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createTime;
}
