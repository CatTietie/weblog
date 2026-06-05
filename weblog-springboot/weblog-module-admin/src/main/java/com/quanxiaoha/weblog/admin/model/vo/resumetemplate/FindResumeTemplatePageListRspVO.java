package com.quanxiaoha.weblog.admin.model.vo.resumetemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindResumeTemplatePageListRspVO {

    private Long id;

    private String name;

    private String componentName;

    private String thumbnail;

    private String description;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createTime;
}
