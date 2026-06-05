package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionDO;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkflowExecutionMapper extends BaseMapper<WorkflowExecutionDO> {

    default List<WorkflowExecutionDO> selectReadyDelayExecutions(LocalDateTime now) {
        return selectList(Wrappers.<WorkflowExecutionDO>lambdaQuery()
                .eq(WorkflowExecutionDO::getStatus, "WAITING_DELAY")
                .le(WorkflowExecutionDO::getDelayUntil, now));
    }

    default Page<WorkflowExecutionDO> selectPageByWorkflowId(Long current, Long size, Long workflowId) {
        Page<WorkflowExecutionDO> page = new Page<>(current, size);
        return selectPage(page, Wrappers.<WorkflowExecutionDO>lambdaQuery()
                .eq(WorkflowExecutionDO::getWorkflowId, workflowId)
                .orderByDesc(WorkflowExecutionDO::getStartTime));
    }
}
