package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.web.model.vo.notification.NotificationPushVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void pushToUser(Long userId, NotificationPushVO payload) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notifications",
                payload
        );
        log.info("==> WebSocket推送通知给用户: userId={}, notificationId={}", userId, payload.getId());
    }

    public void pushToTenant(Long tenantId, NotificationPushVO payload) {
        messagingTemplate.convertAndSend("/topic/tenant/" + tenantId + "/global", payload);
        log.info("==> WebSocket广播租户通知: tenantId={}, notificationId={}", tenantId, payload.getId());
    }

    public void pushToAll(NotificationPushVO payload) {
        messagingTemplate.convertAndSend("/topic/global", payload);
        log.info("==> WebSocket广播全局通知: notificationId={}", payload.getId());
    }
}
