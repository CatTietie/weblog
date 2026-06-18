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
@TableName("t_static_gen_task")
public class StaticGenTaskDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskType;

    private String status;

    private String deployTarget;

    private Integer totalPages;

    private Integer generatedPages;

    private String errorMessage;

    private String outputPath;

    private String deployedUrl;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long durationMs;

    private LocalDateTime createTime;
}
