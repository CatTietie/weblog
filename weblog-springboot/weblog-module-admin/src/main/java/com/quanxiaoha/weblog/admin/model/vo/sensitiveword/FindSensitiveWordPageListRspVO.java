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
public class FindSensitiveWordPageListRspVO {

    private Long id;

    private String word;

    private String category;

    private LocalDateTime createTime;
}
