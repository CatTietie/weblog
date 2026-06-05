package com.quanxiaoha.weblog.web.model.vo.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindShareResumeRspVO {

    private String name;

    private String content;

    private String templateId;

    private String coverData;

    private String languages;
}
