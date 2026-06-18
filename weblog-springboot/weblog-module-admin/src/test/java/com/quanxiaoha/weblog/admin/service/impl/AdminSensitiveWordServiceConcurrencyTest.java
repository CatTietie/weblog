package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.CommentDO;
import com.quanxiaoha.weblog.common.domain.dos.SensitiveScanTaskDO;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.common.utils.SensitiveWordHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSensitiveWordServiceConcurrencyTest {

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

    @Test
    void startScan_concurrentCalls_shouldOnlyAllowOne() throws InterruptedException {
        // First call sees no running task, subsequent calls see one running
        AtomicInteger insertCount = new AtomicInteger(0);
        when(scanTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenAnswer(inv -> {
            return (long) insertCount.get();
        });
        when(articleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);
        when(commentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(scanTaskMapper.insert(any(SensitiveScanTaskDO.class))).thenAnswer(inv -> {
            SensitiveScanTaskDO task = inv.getArgument(0);
            task.setId((long) insertCount.incrementAndGet());
            return 1;
        });

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rejectCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Response resp = service.startScan();
                    if (resp.isSuccess()) {
                        successCount.incrementAndGet();
                    }
                } catch (BizException e) {
                    rejectCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // With synchronized, only 1 task should be created successfully
        assertEquals(1, successCount.get(), "Only one scan task should be created");
        assertEquals(threadCount - 1, rejectCount.get(), "All other attempts should be rejected");
    }

    @Test
    void startScan_secondCallWhileFirstRunning_shouldReject() {
        // Simulate: first call already created a running task (status=0)
        when(scanTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThrows(BizException.class, () -> service.startScan());
        verify(scanTaskMapper, never()).insert(any());
    }
}
