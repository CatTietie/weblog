package com.quanxiaoha.weblog.admin.event.subscriber;

import com.quanxiaoha.weblog.admin.event.ApplicationStatusChangedEvent;
import com.quanxiaoha.weblog.common.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ApplicationStatusChangedSubscriber implements ApplicationListener<ApplicationStatusChangedEvent> {

    private static final Map<Integer, String> STATUS_LABEL_MAP = new HashMap<>();

    static {
        STATUS_LABEL_MAP.put(0, "已投递");
        STATUS_LABEL_MAP.put(1, "面试中");
        STATUS_LABEL_MAP.put(2, "已录用");
        STATUS_LABEL_MAP.put(3, "已拒绝");
        STATUS_LABEL_MAP.put(4, "已放弃");
    }

    @Autowired
    private MailService mailService;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(ApplicationStatusChangedEvent event) {
        String email = event.getUserEmail();
        if (StringUtils.isBlank(email)) {
            log.warn("==> 投递状态变更通知跳过：用户未设置邮箱，公司: {}", event.getCompany());
            return;
        }

        String statusLabel = STATUS_LABEL_MAP.getOrDefault(event.getNewStatus(), "未知状态");
        log.info("==> 投递状态变更事件消费: 公司={}, 职位={}, {} -> {}",
                event.getCompany(), event.getPosition(),
                STATUS_LABEL_MAP.getOrDefault(event.getOldStatus(), "未知"),
                statusLabel);

        mailService.sendApplicationStatusNotification(email, event.getCompany(), event.getPosition(), statusLabel);
    }
}
