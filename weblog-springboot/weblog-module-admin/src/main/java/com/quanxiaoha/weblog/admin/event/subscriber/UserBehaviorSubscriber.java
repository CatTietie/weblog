package com.quanxiaoha.weblog.admin.event.subscriber;

import com.quanxiaoha.weblog.admin.event.UserBehaviorEvent;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorEventDO;
import com.quanxiaoha.weblog.common.domain.mapper.UserBehaviorEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class UserBehaviorSubscriber implements ApplicationListener<UserBehaviorEvent> {

    @Autowired
    private UserBehaviorEventMapper userBehaviorEventMapper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(UserBehaviorEvent event) {
        Long userId = event.getUserId();
        List<UserBehaviorEvent.BehaviorItem> items = event.getEvents();

        log.info("==> 用户行为事件消费, userId: {}, eventCount: {}", userId, items.size());

        LocalDateTime now = LocalDateTime.now();
        for (UserBehaviorEvent.BehaviorItem item : items) {
            UserBehaviorEventDO eventDO = UserBehaviorEventDO.builder()
                    .userId(userId)
                    .eventType(item.getEventType())
                    .articleId(item.getArticleId())
                    .tagId(item.getTagId())
                    .keyword(item.getKeyword())
                    .durationSeconds(item.getDurationSeconds())
                    .createTime(now)
                    .build();
            userBehaviorEventMapper.insert(eventDO);
        }

        log.info("==> 用户行为事件持久化成功, userId: {}, count: {}", userId, items.size());
    }
}
