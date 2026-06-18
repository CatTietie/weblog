package com.quanxiaoha.weblog.admin.schedule;

import com.quanxiaoha.weblog.common.domain.mapper.UserBehaviorEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class BehaviorEventCleanupTask {

    @Autowired
    private UserBehaviorEventMapper userBehaviorEventMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void execute() {
        log.info("==> 开始清理过期行为数据...");
        LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
        int deleted = userBehaviorEventMapper.deleteByCreateTimeBefore(cutoff);
        log.info("==> 清理完成, 删除行为事件数: {}", deleted);
    }
}
