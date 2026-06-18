package com.quanxiaoha.weblog.admin.model.vo.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDetailRspVO {

    private Long id;

    private String name;

    private String content;

    private String templateId;

    private String coverData;

    private String languages;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
