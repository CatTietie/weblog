package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.common.utils.SensitiveWordFilter;
import com.quanxiaoha.weblog.common.utils.SensitiveWordHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSensitiveWordServiceImplScanTest {

    @InjectMocks
    private AdminSensitiveWordServiceImpl service;

    @Mock
    private SensitiveWordMapper sensitiveWordMapper;
    @Mock
    private SensitiveScanTaskMapper scanTaskMapper;
    @Mock
    private SensitiveScanResultMapper scanResultMapper;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private ArticleContentMapper articleContentMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private SensitiveWordHelper sensitiveWordHelper;

    private SensitiveWordFilter realFilter;

    @BeforeEach
    void setUp() {
        realFilter = new SensitiveWordFilter();
        realFilter.addWords(Arrays.asList("敏感词", "违禁"));
    }

    @Test
    void startScan_shouldRejectWhenTaskAlreadyRunning() {
        when(scanTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BizException ex = assertThrows(BizException.class, () -> service.startScan());
        assertEquals(ResponseCodeEnum.SENSITIVE_SCAN_IN_PROGRESS.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void startScan_shouldCreateTaskAndReturnId() {
        when(scanTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(articleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(commentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);
        when(scanTaskMapper.insert(any(SensitiveScanTaskDO.class))).thenAnswer(inv -> {
            SensitiveScanTaskDO task = inv.getArgument(0);
            task.setId(100L);
            return 1;
        });

        Response resp = service.startScan();

        assertTrue(resp.isSuccess());
        assertEquals(100L, resp.getData());
        verify(scanTaskMapper).insert(any(SensitiveScanTaskDO.class));
    }

    @Test
    void executeScanAsync_shouldDetectSensitiveArticleTitle() {
        Long taskId = 1L;
        SensitiveScanTaskDO task = SensitiveScanTaskDO.builder()
                .id(taskId).status(0).scannedCount(0).hitCount(0)
                .totalArticles(1).totalComments(0).build();
        when(scanTaskMapper.selectById(taskId)).thenReturn(task);
        when(sensitiveWordHelper.getFilter()).thenReturn(realFilter);

        ArticleDO article = new ArticleDO();
        article.setId(10L);
        article.setTitle("这是敏感词标题");
        Page<ArticleDO> articlePage = new Page<>(1, 100);
        articlePage.setRecords(Collections.singletonList(article));
        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(articlePage);

        ArticleContentDO content = new ArticleContentDO();
        content.setContent("正常正文内容");
        when(articleContentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(content);

        when(sensitiveWordHelper.extractContext(any(), any(), eq(20))).thenReturn("...敏感词...");

        Page<CommentDO> emptyComments = new Page<>(1, 100);
        emptyComments.setRecords(Collections.emptyList());
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyComments);

        service.executeScanAsync(taskId);

        verify(scanResultMapper, atLeastOnce()).insert(any(SensitiveScanResultDO.class));
        verify(scanTaskMapper, atLeastOnce()).updateById(argThat(t -> t.getStatus() == 1));
    }

    @Test
    void executeScanAsync_shouldDetectSensitiveComment() {
        Long taskId = 2L;
        SensitiveScanTaskDO task = SensitiveScanTaskDO.builder()
                .id(taskId).status(0).scannedCount(0).hitCount(0)
                .totalArticles(0).totalComments(1).build();
        when(scanTaskMapper.selectById(taskId)).thenReturn(task);
        when(sensitiveWordHelper.getFilter()).thenReturn(realFilter);

        Page<ArticleDO> emptyArticles = new Page<>(1, 100);
        emptyArticles.setRecords(Collections.emptyList());
        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyArticles);

        CommentDO comment = new CommentDO();
        comment.setId(20L);
        comment.setContent("评论里有违禁内容");
        Page<CommentDO> commentPage = new Page<>(1, 100);
        commentPage.setRecords(Collections.singletonList(comment));
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(commentPage);
        when(sensitiveWordHelper.extractContext(any(), any(), eq(20))).thenReturn("...违禁...");

        service.executeScanAsync(taskId);

        verify(scanResultMapper).insert(argThat(r -> r.getTargetType() == 3 && r.getTargetId().equals(20L)));
        verify(scanTaskMapper, atLeastOnce()).updateById(argThat(t -> t.getStatus() == 1));
    }

    @Test
    void executeScanAsync_shouldCompleteWithNoHitsOnCleanData() {
        Long taskId = 3L;
        SensitiveScanTaskDO task = SensitiveScanTaskDO.builder()
                .id(taskId).status(0).scannedCount(0).hitCount(0)
                .totalArticles(1).totalComments(0).build();
        when(scanTaskMapper.selectById(taskId)).thenReturn(task);
        when(sensitiveWordHelper.getFilter()).thenReturn(realFilter);

        ArticleDO article = new ArticleDO();
        article.setId(10L);
        article.setTitle("正常标题");
        Page<ArticleDO> articlePage = new Page<>(1, 100);
        articlePage.setRecords(Collections.singletonList(article));
        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(articlePage);

        ArticleContentDO content = new ArticleContentDO();
        content.setContent("正常正文");
        when(articleContentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(content);

        Page<CommentDO> emptyComments = new Page<>(1, 100);
        emptyComments.setRecords(Collections.emptyList());
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyComments);

        service.executeScanAsync(taskId);

        verify(scanResultMapper, never()).insert(any());
        verify(scanTaskMapper, atLeastOnce()).updateById(argThat(t -> t.getStatus() == 1 && t.getHitCount() == 0));
    }

    @Test
    void executeScanAsync_shouldHandleEmptyDatabase() {
        Long taskId = 4L;
        SensitiveScanTaskDO task = SensitiveScanTaskDO.builder()
                .id(taskId).status(0).scannedCount(0).hitCount(0)
                .totalArticles(0).totalComments(0).build();
        when(scanTaskMapper.selectById(taskId)).thenReturn(task);
        when(sensitiveWordHelper.getFilter()).thenReturn(realFilter);

        Page<ArticleDO> emptyArticles = new Page<>(1, 100);
        emptyArticles.setRecords(Collections.emptyList());
        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyArticles);

        Page<CommentDO> emptyComments = new Page<>(1, 100);
        emptyComments.setRecords(Collections.emptyList());
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyComments);

        service.executeScanAsync(taskId);

        verify(scanResultMapper, never()).insert(any());
        verify(scanTaskMapper, atLeastOnce()).updateById(argThat(t -> t.getStatus() == 1));
    }

    @Test
    void executeScanAsync_shouldMarkTaskFailedOnException() {
        Long taskId = 5L;
        SensitiveScanTaskDO task = SensitiveScanTaskDO.builder()
                .id(taskId).status(0).scannedCount(0).hitCount(0)
                .totalArticles(1).totalComments(0).build();
        when(scanTaskMapper.selectById(taskId)).thenReturn(task);
        when(sensitiveWordHelper.getFilter()).thenThrow(new RuntimeException("模拟异常"));

        service.executeScanAsync(taskId);

        verify(scanTaskMapper, atLeastOnce()).updateById(argThat(t -> t.getStatus() == 2));
    }

    @Test
    void executeScanAsync_shouldHandleNullArticleContent() {
        Long taskId = 6L;
        SensitiveScanTaskDO task = SensitiveScanTaskDO.builder()
                .id(taskId).status(0).scannedCount(0).hitCount(0)
                .totalArticles(1).totalComments(0).build();
        when(scanTaskMapper.selectById(taskId)).thenReturn(task);
        when(sensitiveWordHelper.getFilter()).thenReturn(realFilter);

        ArticleDO article = new ArticleDO();
        article.setId(10L);
        article.setTitle("正常标题");
        Page<ArticleDO> articlePage = new Page<>(1, 100);
        articlePage.setRecords(Collections.singletonList(article));
        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(articlePage);
        when(articleContentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        Page<CommentDO> emptyComments = new Page<>(1, 100);
        emptyComments.setRecords(Collections.emptyList());
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyComments);

        assertDoesNotThrow(() -> service.executeScanAsync(taskId));
        verify(scanTaskMapper, atLeastOnce()).updateById(argThat(t -> t.getStatus() == 1));
    }
}
