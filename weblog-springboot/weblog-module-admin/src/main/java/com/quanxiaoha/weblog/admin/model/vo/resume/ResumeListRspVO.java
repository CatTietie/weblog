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
public class ResumeListRspVO {

    private Long id;

    private String name;

    private String templateId;

    private Boolean shareEnabled;

    private String shareCode;

    private LocalDateTime updateTime;
}
