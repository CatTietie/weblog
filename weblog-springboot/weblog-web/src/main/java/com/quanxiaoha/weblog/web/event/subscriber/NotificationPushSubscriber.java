package com.quanxiaoha.weblog.web.event.subscriber;

import com.quanxiaoha.weblog.common.event.NotificationCreatedEvent;
import com.quanxiaoha.weblog.web.model.vo.notification.NotificationPushVO;
import com.quanxiaoha.weblog.web.service.WebSocketNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationPushSubscriber implements ApplicationListener<NotificationCreatedEvent> {

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(NotificationCreatedEvent event) {
        NotificationPushVO pushVO = NotificationPushVO.builder()
                .id(event.getNotificationId())
                .type(event.getType())
                .title(event.getTitle())
                .content(event.getContent())
                .articleId(event.getArticleId())
                .commentId(event.getCommentId())
                .createTime(event.getCreateTime())
                .build();

        if (event.getReceiverId() == null) {
            if (event.getTenantId() != null) {
                webSocketNotificationService.pushToTenant(event.getTenantId(), pushVO);
            } else {
                webSocketNotificationService.pushToAll(pushVO);
            }
        } else {
            webSocketNotificationService.pushToUser(event.getReceiverId(), pushVO);
        }
    }
}
