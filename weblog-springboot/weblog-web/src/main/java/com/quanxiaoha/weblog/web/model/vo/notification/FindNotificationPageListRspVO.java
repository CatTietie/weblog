package com.quanxiaoha.weblog.web.model.vo.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindNotificationPageListRspVO {

    private Long id;

    private Integer type;

    private String title;

    private String content;

    private Long articleId;

    private Long commentId;

    private Boolean isRead;

    private LocalDateTime createTime;
}
