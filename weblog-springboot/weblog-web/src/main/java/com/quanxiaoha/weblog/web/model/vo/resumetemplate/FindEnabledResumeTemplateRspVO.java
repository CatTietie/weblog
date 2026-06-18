package com.quanxiaoha.weblog.web.model.vo.resumetemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindEnabledResumeTemplateRspVO {

    private Long id;

    private String name;

    private String componentName;

    private String thumbnail;

    private String description;
}
