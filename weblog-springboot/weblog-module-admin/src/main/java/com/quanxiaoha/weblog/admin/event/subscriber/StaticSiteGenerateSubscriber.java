package com.quanxiaoha.weblog.admin.event.subscriber;

import com.quanxiaoha.weblog.admin.event.StaticSiteGenerateEvent;
import com.quanxiaoha.weblog.admin.staticsite.StaticSiteGenerator;
import com.quanxiaoha.weblog.admin.staticsite.deploy.AliyunOSSDeployer;
import com.quanxiaoha.weblog.admin.staticsite.deploy.GitHubPagesDeployer;
import com.quanxiaoha.weblog.common.domain.dos.StaticGenTaskDO;
import com.quanxiaoha.weblog.common.domain.dos.StaticSiteConfigDO;
import com.quanxiaoha.weblog.common.domain.mapper.StaticGenTaskMapper;
import com.quanxiaoha.weblog.common.domain.mapper.StaticSiteConfigMapper;
import com.quanxiaoha.weblog.common.enums.StaticGenTaskStatusEnum;
import com.quanxiaoha.weblog.common.enums.StaticGenTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class StaticSiteGenerateSubscriber {

    @Autowired
    private StaticSiteGenerator generator;

    @Autowired
    private GitHubPagesDeployer githubDeployer;

    @Autowired
    private AliyunOSSDeployer ossDeployer;

    @Autowired
    private StaticGenTaskMapper taskMapper;

    @Autowired
    private StaticSiteConfigMapper configMapper;

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onStaticSiteGenerate(StaticSiteGenerateEvent event) {
        log.info("收到静态站点生成事件: type={}, autoDeploy={}", event.getTaskType(), event.getAutoDeploy());

        String taskType = event.getTaskType();

        if ("DEPLOY".equals(taskType)) {
            executeDeploy();
            return;
        }

        // 创建任务记录
        StaticGenTaskDO task = StaticGenTaskDO.builder()
                .taskType(taskType)
                .status(StaticGenTaskStatusEnum.PENDING.getCode())
                .createTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);

        // 执行生成
        if (StaticGenTaskTypeEnum.FULL.getCode().equals(taskType)) {
            generator.generateFullSite(task.getId());
        } else {
            generator.generateIncrementalSite(task.getId());
        }

        // 部署
        if (Boolean.TRUE.equals(event.getAutoDeploy())) {
            executeDeploy();
        }
    }

    private void executeDeploy() {
        StaticSiteConfigDO config = configMapper.selectById(1L);
        if (config == null) return;

        String outputDir = generator.getOutputDir();

        try {
            if (Boolean.TRUE.equals(config.getGithubEnabled())) {
                githubDeployer.deploy(outputDir, config);
                log.info("GitHub Pages 部署完成");
            }
            if (Boolean.TRUE.equals(config.getOssEnabled())) {
                ossDeployer.deploy(outputDir, config);
                log.info("OSS 部署完成");
            }
        } catch (Exception e) {
            log.error("部署失败", e);
        }
    }
}
