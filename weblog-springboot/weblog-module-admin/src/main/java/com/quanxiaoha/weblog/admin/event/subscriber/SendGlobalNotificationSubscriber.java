package com.quanxiaoha.weblog.admin.event.subscriber;

import com.quanxiaoha.weblog.admin.event.SendGlobalNotificationEvent;
import com.quanxiaoha.weblog.common.domain.dos.NotificationDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.NotificationMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.event.NotificationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SendGlobalNotificationSubscriber implements ApplicationListener<SendGlobalNotificationEvent> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final int BATCH_SIZE = 500;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(SendGlobalNotificationEvent event) {
        try {
            Long senderId = event.getSenderId();
            String title = event.getTitle();
            String content = event.getContent();
            LocalDateTime now = LocalDateTime.now();

            List<UserDO> allUsers = userMapper.selectList(null);
            log.info("==> 开始发送全局通知, 用户总数: {}", allUsers.size());

            List<NotificationDO> batch = new ArrayList<>(BATCH_SIZE);
            Long firstNotificationId = null;

            for (int i = 0; i < allUsers.size(); i++) {
                UserDO user = allUsers.get(i);
                if (user.getId().equals(senderId)) {
                    continue;
                }

                NotificationDO notification = NotificationDO.builder()
                        .receiverId(user.getId())
                        .type(2)
                        .title(title)
                        .content(content)
                        .senderId(senderId)
                        .isRead(false)
                        .createTime(now)
                        .build();
                batch.add(notification);

                if (batch.size() >= BATCH_SIZE || i == allUsers.size() - 1) {
                    if (!batch.isEmpty()) {
                        notificationMapper.insertBatchSomeColumn(batch);
                        if (firstNotificationId == null && !batch.isEmpty()) {
                            firstNotificationId = batch.get(0).getId();
                        }
                        batch.clear();
                    }
                }
            }

            NotificationCreatedEvent pushEvent = new NotificationCreatedEvent(
                    this, null, firstNotificationId,
                    2, title, content,
                    null, null, now
            );
            eventPublisher.publishEvent(pushEvent);

            log.info("==> 全局通知发送完成, senderId: {}", senderId);
        } catch (Exception e) {
            log.error("==> 全局通知发送失败: {}", e.getMessage(), e);
        }
    }
}
