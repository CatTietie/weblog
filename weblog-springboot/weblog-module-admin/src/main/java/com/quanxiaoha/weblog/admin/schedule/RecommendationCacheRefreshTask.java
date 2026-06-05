package com.quanxiaoha.weblog.admin.schedule;

import com.quanxiaoha.weblog.admin.cache.RecommendationCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RecommendationCacheRefreshTask {

    @Autowired
    private RecommendationCacheService recommendationCacheService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        log.info("==> 刷新推荐缓存...");
        recommendationCacheService.refreshAll();
        log.info("==> 推荐缓存刷新完成");
    }
}
