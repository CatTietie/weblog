package com.quanxiaoha.weblog.admin.model.vo.resumeapplication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindResumeApplicationPageListRspVO {

    private Long id;

    private Long resumeId;

    private String company;

    private String position;

    private LocalDateTime applyTime;

    private String channel;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
