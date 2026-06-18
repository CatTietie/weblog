package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.event.StaticSiteGenerateEvent;
import com.quanxiaoha.weblog.admin.model.vo.staticsite.*;
import com.quanxiaoha.weblog.admin.service.AdminStaticSiteService;
import com.quanxiaoha.weblog.common.domain.dos.StaticGenTaskDO;
import com.quanxiaoha.weblog.common.domain.dos.StaticSiteConfigDO;
import com.quanxiaoha.weblog.common.domain.mapper.StaticGenTaskMapper;
import com.quanxiaoha.weblog.common.domain.mapper.StaticSiteConfigMapper;
import com.quanxiaoha.weblog.common.enums.StaticGenTaskStatusEnum;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class AdminStaticSiteServiceImpl implements AdminStaticSiteService {

    @Autowired
    private StaticSiteConfigMapper staticSiteConfigMapper;

    @Autowired
    private StaticGenTaskMapper staticGenTaskMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final Long CONFIG_ID = 1L;

    @Override
    public Response updateConfig(UpdateStaticSiteConfigReqVO vo) {
        StaticSiteConfigDO config = staticSiteConfigMapper.selectById(CONFIG_ID);
        boolean isNew = (config == null);
        if (isNew) {
            config = new StaticSiteConfigDO();
            config.setId(CONFIG_ID);
        }

        config.setCommentProvider(vo.getCommentProvider());
        config.setCommentConfig(vo.getCommentConfig());
        config.setGithubEnabled(vo.getGithubEnabled());
        config.setGithubToken(vo.getGithubToken());
        config.setGithubRepo(vo.getGithubRepo());
        config.setGithubBranch(vo.getGithubBranch());
        config.setGithubCname(vo.getGithubCname());
        config.setOssEnabled(vo.getOssEnabled());
        config.setOssEndpoint(vo.getOssEndpoint());
        config.setOssAccessKeyId(vo.getOssAccessKeyId());
        config.setOssAccessKeySecret(vo.getOssAccessKeySecret());
        config.setOssBucketName(vo.getOssBucketName());
        config.setOssBasePath(vo.getOssBasePath());
        config.setAutoDeploy(vo.getAutoDeploy());
        config.setOutputBaseUrl(vo.getOutputBaseUrl());

        if (isNew) {
            staticSiteConfigMapper.insert(config);
        } else {
            staticSiteConfigMapper.updateById(config);
        }
        return Response.success();
    }

    @Override
    public Response findConfigDetail() {
        StaticSiteConfigDO config = staticSiteConfigMapper.selectById(CONFIG_ID);
        if (config == null) {
            return Response.success(FindStaticSiteConfigRspVO.builder().build());
        }

        FindStaticSiteConfigRspVO rspVO = FindStaticSiteConfigRspVO.builder()
                .commentProvider(config.getCommentProvider())
                .commentConfig(config.getCommentConfig())
                .githubEnabled(config.getGithubEnabled())
                .githubToken(maskToken(config.getGithubToken()))
                .githubRepo(config.getGithubRepo())
                .githubBranch(config.getGithubBranch())
                .githubCname(config.getGithubCname())
                .ossEnabled(config.getOssEnabled())
                .ossEndpoint(config.getOssEndpoint())
                .ossAccessKeyId(maskToken(config.getOssAccessKeyId()))
                .ossAccessKeySecret(maskToken(config.getOssAccessKeySecret()))
                .ossBucketName(config.getOssBucketName())
                .ossBasePath(config.getOssBasePath())
                .autoDeploy(config.getAutoDeploy())
                .outputBaseUrl(config.getOutputBaseUrl())
                .build();

        return Response.success(rspVO);
    }

    @Override
    public Response triggerGeneration(TriggerGenerationReqVO vo) {
        StaticGenTaskDO runningTask = staticGenTaskMapper.selectOne(
                Wrappers.<StaticGenTaskDO>lambdaQuery()
                        .eq(StaticGenTaskDO::getStatus, StaticGenTaskStatusEnum.RUNNING.getCode())
                        .last("LIMIT 1")
        );
        if (runningTask != null) {
            return Response.fail("已有生成任务正在执行中，请等待完成");
        }

        Boolean autoDeploy = Boolean.TRUE.equals(vo.getAutoDeploy());
        eventPublisher.publishEvent(new StaticSiteGenerateEvent(this, vo.getTaskType(), autoDeploy));
        return Response.success();
    }

    @Override
    public Response triggerDeploy() {
        StaticGenTaskDO lastSuccess = staticGenTaskMapper.selectOne(
                Wrappers.<StaticGenTaskDO>lambdaQuery()
                        .eq(StaticGenTaskDO::getStatus, StaticGenTaskStatusEnum.SUCCESS.getCode())
                        .orderByDesc(StaticGenTaskDO::getCreateTime)
                        .last("LIMIT 1")
        );
        if (lastSuccess == null || lastSuccess.getOutputPath() == null) {
            return Response.fail("没有可用的生成结果，请先执行生成");
        }

        eventPublisher.publishEvent(new StaticSiteGenerateEvent(this, "DEPLOY", true));
        return Response.success();
    }

    @Override
    public void downloadZip(HttpServletResponse response) {
        StaticGenTaskDO lastSuccess = staticGenTaskMapper.selectOne(
                Wrappers.<StaticGenTaskDO>lambdaQuery()
                        .eq(StaticGenTaskDO::getStatus, StaticGenTaskStatusEnum.SUCCESS.getCode())
                        .orderByDesc(StaticGenTaskDO::getCreateTime)
                        .last("LIMIT 1")
        );

        if (lastSuccess == null || lastSuccess.getOutputPath() == null) {
            throw new RuntimeException("没有可用的生成结果");
        }

        Path outputDir = Paths.get(lastSuccess.getOutputPath());
        if (!Files.exists(outputDir)) {
            throw new RuntimeException("生成目录不存在");
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=static-site.zip");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            Files.walk(outputDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            String entryName = outputDir.relativize(file).toString().replace("\\", "/");
                            zos.putNextEntry(new ZipEntry(entryName));
                            Files.copy(file, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            log.error("打包文件失败: {}", file, e);
                        }
                    });
        } catch (IOException e) {
            log.error("生成 ZIP 失败", e);
            throw new RuntimeException("生成 ZIP 失败");
        }
    }

    @Override
    public Response findTaskPageList(FindGenTaskPageListReqVO vo) {
        Page<StaticGenTaskDO> page = staticGenTaskMapper.selectPage(
                new Page<>(vo.getCurrent(), vo.getSize()),
                Wrappers.<StaticGenTaskDO>lambdaQuery()
                        .orderByDesc(StaticGenTaskDO::getCreateTime)
        );

        List<FindGenTaskPageListRspVO> list = page.getRecords().stream()
                .map(task -> FindGenTaskPageListRspVO.builder()
                        .id(task.getId())
                        .taskType(task.getTaskType())
                        .status(task.getStatus())
                        .deployTarget(task.getDeployTarget())
                        .totalPages(task.getTotalPages())
                        .generatedPages(task.getGeneratedPages())
                        .errorMessage(task.getErrorMessage())
                        .deployedUrl(task.getDeployedUrl())
                        .durationMs(task.getDurationMs())
                        .startTime(task.getStartTime())
                        .endTime(task.getEndTime())
                        .createTime(task.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(page, list);
    }

    @Override
    public Response findTaskProgress() {
        StaticGenTaskDO runningTask = staticGenTaskMapper.selectOne(
                Wrappers.<StaticGenTaskDO>lambdaQuery()
                        .eq(StaticGenTaskDO::getStatus, StaticGenTaskStatusEnum.RUNNING.getCode())
                        .orderByDesc(StaticGenTaskDO::getCreateTime)
                        .last("LIMIT 1")
        );

        if (runningTask == null) {
            return Response.success(GenTaskProgressRspVO.builder().status("IDLE").build());
        }

        return Response.success(GenTaskProgressRspVO.builder()
                .taskId(runningTask.getId())
                .status(runningTask.getStatus())
                .totalPages(runningTask.getTotalPages())
                .generatedPages(runningTask.getGeneratedPages())
                .errorMessage(runningTask.getErrorMessage())
                .build());
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return token == null ? "" : "****";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}
