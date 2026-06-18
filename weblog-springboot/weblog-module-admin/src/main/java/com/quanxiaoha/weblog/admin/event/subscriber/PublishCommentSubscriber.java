package com.quanxiaoha.weblog.admin.event.subscriber;

import com.quanxiaoha.weblog.admin.event.PublishCommentEvent;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.NotificationDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.NotificationMapper;
import com.quanxiaoha.weblog.common.event.NotificationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PublishCommentSubscriber implements ApplicationListener<PublishCommentEvent> {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(PublishCommentEvent event) {
        try {
            Long commentUserId = event.getCommentUserId();
            Long replyToUserId = event.getReplyToUserId();

            if (event.getParentId() == null || replyToUserId == null) {
                return;
            }

            if (commentUserId.equals(replyToUserId)) {
                return;
            }

            ArticleDO article = articleMapper.selectById(event.getArticleId());
            String articleTitle = (article != null) ? article.getTitle() : "未知文章";

            String title = "你在文章「" + articleTitle + "」的评论有了新回复";
            if (title.length() > 100) {
                title = title.substring(0, 97) + "...";
            }

            String contentPreview = event.getContent();
            if (contentPreview == null || contentPreview.isEmpty()) {
                contentPreview = "";
            } else if (contentPreview.length() > 100) {
                contentPreview = contentPreview.substring(0, 100) + "...";
            }

            LocalDateTime now = LocalDateTime.now();
            NotificationDO notification = NotificationDO.builder()
                    .receiverId(replyToUserId)
                    .type(1)
                    .title(title)
                    .content(contentPreview)
                    .articleId(event.getArticleId())
                    .commentId(event.getCommentId())
                    .isRead(false)
                    .createTime(now)
                    .build();

            notificationMapper.insert(notification);
            log.info("==> 评论通知已生成, receiverId: {}, commentId: {}", replyToUserId, event.getCommentId());

            eventPublisher.publishEvent(new NotificationCreatedEvent(
                    this, replyToUserId, notification.getId(),
                    1, title, contentPreview,
                    event.getArticleId(), event.getCommentId(), now
            ));
        } catch (Exception e) {
            log.error("==> 评论通知生成失败, commentId: {}, error: {}", event.getCommentId(), e.getMessage(), e);
        }
    }
}
