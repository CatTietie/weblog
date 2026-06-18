package com.quanxiaoha.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PublishCommentEvent extends ApplicationEvent {

    private Long commentId;
    private Long articleId;
    private Long commentUserId;
    private String content;
    private Long parentId;
    private Long replyToUserId;

    public PublishCommentEvent(Object source, Long commentId, Long articleId,
                               Long commentUserId, String content,
                               Long parentId, Long replyToUserId) {
        super(source);
        this.commentId = commentId;
        this.articleId = articleId;
        this.commentUserId = commentUserId;
        this.content = content;
        this.parentId = parentId;
        this.replyToUserId = replyToUserId;
    }
}
