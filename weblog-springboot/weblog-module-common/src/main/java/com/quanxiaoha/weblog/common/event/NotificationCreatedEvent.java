package com.quanxiaoha.weblog.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class NotificationCreatedEvent extends ApplicationEvent {

    private Long receiverId;
    private Long notificationId;
    private Integer type;
    private String title;
    private String content;
    private Long articleId;
    private Long commentId;
    private Long tenantId;
    private LocalDateTime createTime;

    public NotificationCreatedEvent(Object source, Long receiverId, Long notificationId,
                                    Integer type, String title, String content,
                                    Long articleId, Long commentId, LocalDateTime createTime) {
        super(source);
        this.receiverId = receiverId;
        this.notificationId = notificationId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.articleId = articleId;
        this.commentId = commentId;
        this.createTime = createTime;
    }

    public NotificationCreatedEvent(Object source, Long receiverId, Long notificationId,
                                    Integer type, String title, String content,
                                    Long articleId, Long commentId, Long tenantId, LocalDateTime createTime) {
        super(source);
        this.receiverId = receiverId;
        this.notificationId = notificationId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.articleId = articleId;
        this.commentId = commentId;
        this.tenantId = tenantId;
        this.createTime = createTime;
    }
}
