package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.reminderlog.FindReminderLogPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.reminderlog.FindReminderLogPageListRspVO;
import com.quanxiaoha.weblog.admin.service.AdminReminderLogService;
import com.quanxiaoha.weblog.common.domain.dos.ApplicationReminderLogDO;
import com.quanxiaoha.weblog.common.domain.mapper.ApplicationReminderLogMapper;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminReminderLogServiceImpl implements AdminReminderLogService {

    @Autowired
    private ApplicationReminderLogMapper reminderLogMapper;

    @Override
    public Response findReminderLogPageList(FindReminderLogPageListReqVO findReminderLogPageListReqVO) {
        Long current = findReminderLogPageListReqVO.getCurrent();
        Long size = findReminderLogPageListReqVO.getSize();

        Page<ApplicationReminderLogDO> page = reminderLogMapper.selectPageList(current, size);
        List<ApplicationReminderLogDO> records = page.getRecords();

        List<FindReminderLogPageListRspVO> voList = records.stream().map(record ->
                FindReminderLogPageListRspVO.builder()
                        .id(record.getId())
                        .executeTime(record.getExecuteTime())
                        .scannedCount(record.getScannedCount())
                        .userCount(record.getUserCount())
                        .sentCount(record.getSentCount())
                        .failedCount(record.getFailedCount())
                        .build()
        ).collect(Collectors.toList());

        return PageResponse.success(page, voList);
    }
}
