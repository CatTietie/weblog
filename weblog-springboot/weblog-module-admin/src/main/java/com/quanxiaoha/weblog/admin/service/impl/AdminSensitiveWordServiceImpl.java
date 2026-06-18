package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.sensitiveword.*;
import com.quanxiaoha.weblog.admin.service.AdminSensitiveWordService;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.common.utils.SensitiveWordFilter;
import com.quanxiaoha.weblog.common.utils.SensitiveWordHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminSensitiveWordServiceImpl implements AdminSensitiveWordService {

    private final Object scanLock = new Object();

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;
    @Autowired
    private SensitiveScanTaskMapper scanTaskMapper;
    @Autowired
    private SensitiveScanResultMapper scanResultMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveWordHelper sensitiveWordHelper;

    @Override
    public Response addSensitiveWord(AddSensitiveWordReqVO vo) {
        Long count = sensitiveWordMapper.selectCount(
                Wrappers.<SensitiveWordDO>lambdaQuery().eq(SensitiveWordDO::getWord, vo.getWord().trim()));
        if (count > 0) {
            throw new BizException(ResponseCodeEnum.SENSITIVE_WORD_EXISTED);
        }

        SensitiveWordDO wordDO = SensitiveWordDO.builder()
                .word(vo.getWord().trim().toLowerCase())
                .category(vo.getCategory() != null ? vo.getCategory() : "默认")
                .createTime(LocalDateTime.now())
                .build();
        sensitiveWordMapper.insert(wordDO);

        sensitiveWordHelper.refresh(0L);
        return Response.success();
    }

    @Override
    public Response deleteSensitiveWord(Long id) {
        sensitiveWordMapper.deleteById(id);
        sensitiveWordHelper.refresh(0L);
        return Response.success();
    }

    @Override
    public Response findSensitiveWordPageList(FindSensitiveWordPageListReqVO vo) {
        Page<SensitiveWordDO> page = sensitiveWordMapper.selectPageList(vo.getCurrent(), vo.getSize(), vo.getKeyword());

        List<FindSensitiveWordPageListRspVO> vos = page.getRecords().stream().map(d ->
                FindSensitiveWordPageListRspVO.builder()
                        .id(d.getId())
                        .word(d.getWord())
                        .category(d.getCategory())
                        .createTime(d.getCreateTime())
                        .build()
        ).collect(Collectors.toList());

        return PageResponse.success(page, vos);
    }

    @Override
    public Response batchImport(BatchImportSensitiveWordReqVO vo) {
        String category = vo.getCategory() != null ? vo.getCategory() : "默认";
        List<String> existingWords = sensitiveWordMapper.selectAllWords(0L);
        Set<String> existingSet = new HashSet<>(existingWords);

        List<SensitiveWordDO> toInsert = vo.getWords().stream()
                .map(w -> w.trim().toLowerCase())
                .filter(w -> !w.isEmpty() && !existingSet.contains(w))
                .distinct()
                .map(w -> SensitiveWordDO.builder()
                        .word(w)
                        .category(category)
                        .createTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        if (!toInsert.isEmpty()) {
            for (SensitiveWordDO wordDO : toInsert) {
                sensitiveWordMapper.insert(wordDO);
            }
        }

        sensitiveWordHelper.refresh(0L);
        return Response.success(toInsert.size());
    }

    @Override
    public Response startScan() {
        synchronized (scanLock) {
            Long runningCount = scanTaskMapper.selectCount(
                    Wrappers.<SensitiveScanTaskDO>lambdaQuery().eq(SensitiveScanTaskDO::getStatus, 0));
            if (runningCount > 0) {
                throw new BizException(ResponseCodeEnum.SENSITIVE_SCAN_IN_PROGRESS);
            }

            Long articleCount = articleMapper.selectCount(
                    Wrappers.<ArticleDO>lambdaQuery().eq(ArticleDO::getIsDeleted, false));
            Long commentCount = commentMapper.selectCount(
                    Wrappers.<CommentDO>lambdaQuery().eq(CommentDO::getIsDeleted, false));

            SensitiveScanTaskDO taskDO = SensitiveScanTaskDO.builder()
                    .status(0)
                    .totalArticles(articleCount.intValue())
                    .totalComments(commentCount.intValue())
                    .scannedCount(0)
                    .hitCount(0)
                    .createTime(LocalDateTime.now())
                    .build();
            scanTaskMapper.insert(taskDO);

            executeScanAsync(taskDO.getId());

            return Response.success(taskDO.getId());
        }
    }

    @Async
    public void executeScanAsync(Long taskId) {
        try {
            SensitiveScanTaskDO task = scanTaskMapper.selectById(taskId);
            SensitiveWordFilter filter = sensitiveWordHelper.getFilter();
            int scanned = 0;
            int hits = 0;

            // 扫描文章标题和内容
            int pageSize = 100;
            long articlePage = 1;
            while (true) {
                Page<ArticleDO> articles = articleMapper.selectPage(
                        new Page<>(articlePage, pageSize),
                        Wrappers.<ArticleDO>lambdaQuery().eq(ArticleDO::getIsDeleted, false));

                if (articles.getRecords().isEmpty()) break;

                for (ArticleDO article : articles.getRecords()) {
                    // 检查标题
                    Set<String> titleHits = filter.detect(article.getTitle());
                    if (!titleHits.isEmpty()) {
                        String context = sensitiveWordHelper.extractContext(article.getTitle(), titleHits.iterator().next(), 20);
                        SensitiveScanResultDO result = SensitiveScanResultDO.builder()
                                .taskId(taskId)
                                .targetType(1)
                                .targetId(article.getId())
                                .hitWords(String.join(",", titleHits))
                                .context(context)
                                .handled(0)
                                .createTime(LocalDateTime.now())
                                .build();
                        scanResultMapper.insert(result);
                        hits++;
                    }

                    // 检查正文
                    ArticleContentDO content = articleContentMapper.selectOne(
                            Wrappers.<ArticleContentDO>lambdaQuery().eq(ArticleContentDO::getArticleId, article.getId()));
                    if (content != null && content.getContent() != null) {
                        Set<String> contentHits = filter.detect(content.getContent());
                        if (!contentHits.isEmpty()) {
                            String ctx = sensitiveWordHelper.extractContext(content.getContent(), contentHits.iterator().next(), 20);
                            SensitiveScanResultDO result = SensitiveScanResultDO.builder()
                                    .taskId(taskId)
                                    .targetType(2)
                                    .targetId(article.getId())
                                    .hitWords(String.join(",", contentHits))
                                    .context(ctx)
                                    .handled(0)
                                    .createTime(LocalDateTime.now())
                                    .build();
                            scanResultMapper.insert(result);
                            hits++;
                        }
                    }
                    scanned++;
                }

                task.setScannedCount(scanned);
                task.setHitCount(hits);
                scanTaskMapper.updateById(task);

                if (articles.getRecords().size() < pageSize) break;
                articlePage++;
            }

            // 扫描评论
            long commentPage = 1;
            while (true) {
                Page<CommentDO> comments = commentMapper.selectPage(
                        new Page<>(commentPage, pageSize),
                        Wrappers.<CommentDO>lambdaQuery().eq(CommentDO::getIsDeleted, false));

                if (comments.getRecords().isEmpty()) break;

                for (CommentDO comment : comments.getRecords()) {
                    Set<String> commentHits = filter.detect(comment.getContent());
                    if (!commentHits.isEmpty()) {
                        String ctx = sensitiveWordHelper.extractContext(comment.getContent(), commentHits.iterator().next(), 20);
                        SensitiveScanResultDO result = SensitiveScanResultDO.builder()
                                .taskId(taskId)
                                .targetType(3)
                                .targetId(comment.getId())
                                .hitWords(String.join(",", commentHits))
                                .context(ctx)
                                .handled(0)
                                .createTime(LocalDateTime.now())
                                .build();
                        scanResultMapper.insert(result);
                        hits++;
                    }
                    scanned++;
                }

                task.setScannedCount(scanned);
                task.setHitCount(hits);
                scanTaskMapper.updateById(task);

                if (comments.getRecords().size() < pageSize) break;
                commentPage++;
            }

            task.setStatus(1);
            task.setScannedCount(scanned);
            task.setHitCount(hits);
            task.setFinishTime(LocalDateTime.now());
            scanTaskMapper.updateById(task);

        } catch (Exception e) {
            log.error("扫描任务执行失败, taskId: {}", taskId, e);
            SensitiveScanTaskDO task = scanTaskMapper.selectById(taskId);
            if (task != null) {
                task.setStatus(2);
                task.setFinishTime(LocalDateTime.now());
                scanTaskMapper.updateById(task);
            }
        }
    }

    @Override
    public Response getScanTaskList() {
        List<SensitiveScanTaskDO> tasks = scanTaskMapper.selectList(
                Wrappers.<SensitiveScanTaskDO>lambdaQuery().orderByDesc(SensitiveScanTaskDO::getCreateTime).last("LIMIT 20"));

        List<ScanTaskRspVO> vos = tasks.stream().map(t ->
                ScanTaskRspVO.builder()
                        .id(t.getId())
                        .status(t.getStatus())
                        .totalArticles(t.getTotalArticles())
                        .totalComments(t.getTotalComments())
                        .scannedCount(t.getScannedCount())
                        .hitCount(t.getHitCount())
                        .createTime(t.getCreateTime())
                        .finishTime(t.getFinishTime())
                        .build()
        ).collect(Collectors.toList());

        return Response.success(vos);
    }

    @Override
    public Response getScanTaskProgress(Long taskId) {
        SensitiveScanTaskDO task = scanTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException(ResponseCodeEnum.SENSITIVE_SCAN_TASK_NOT_FOUND);
        }
        ScanTaskRspVO vo = ScanTaskRspVO.builder()
                .id(task.getId())
                .status(task.getStatus())
                .totalArticles(task.getTotalArticles())
                .totalComments(task.getTotalComments())
                .scannedCount(task.getScannedCount())
                .hitCount(task.getHitCount())
                .createTime(task.getCreateTime())
                .finishTime(task.getFinishTime())
                .build();
        return Response.success(vo);
    }

    @Override
    public Response findScanResultPageList(FindScanResultPageListReqVO vo) {
        Page<SensitiveScanResultDO> page = scanResultMapper.selectPageByTaskId(
                vo.getCurrent(), vo.getSize(), vo.getTaskId(), vo.getHandled());

        List<FindScanResultPageListRspVO> vos = page.getRecords().stream().map(r -> {
            String title = "";
            if (r.getTargetType() == 1 || r.getTargetType() == 2) {
                ArticleDO article = articleMapper.selectById(r.getTargetId());
                title = article != null ? article.getTitle() : "已删除";
            } else if (r.getTargetType() == 3) {
                CommentDO comment = commentMapper.selectById(r.getTargetId());
                title = comment != null ? comment.getContent() : "已删除";
                if (title.length() > 50) title = title.substring(0, 50) + "...";
            }
            return FindScanResultPageListRspVO.builder()
                    .id(r.getId())
                    .taskId(r.getTaskId())
                    .targetType(r.getTargetType())
                    .targetId(r.getTargetId())
                    .hitWords(r.getHitWords())
                    .context(r.getContext())
                    .handled(r.getHandled())
                    .createTime(r.getCreateTime())
                    .targetTitle(title)
                    .build();
        }).collect(Collectors.toList());

        return PageResponse.success(page, vos);
    }

    @Override
    public Response handleScanResult(HandleScanResultReqVO vo) {
        List<Long> ids = vo.getIds();
        Integer action = vo.getAction();

        if (action == 2) {
            // 删除对应的文章或评论
            for (Long id : ids) {
                SensitiveScanResultDO result = scanResultMapper.selectById(id);
                if (result == null) continue;
                if (result.getTargetType() == 1 || result.getTargetType() == 2) {
                    ArticleDO article = articleMapper.selectById(result.getTargetId());
                    if (article != null) {
                        article.setIsDeleted(true);
                        articleMapper.updateById(article);
                    }
                } else if (result.getTargetType() == 3) {
                    CommentDO comment = commentMapper.selectById(result.getTargetId());
                    if (comment != null) {
                        comment.setIsDeleted(true);
                        commentMapper.updateById(comment);
                    }
                }
                result.setHandled(action);
                scanResultMapper.updateById(result);
            }
        } else {
            // 忽略
            for (Long id : ids) {
                SensitiveScanResultDO result = scanResultMapper.selectById(id);
                if (result == null) continue;
                result.setHandled(action);
                scanResultMapper.updateById(result);
            }
        }
        return Response.success();
    }
}
