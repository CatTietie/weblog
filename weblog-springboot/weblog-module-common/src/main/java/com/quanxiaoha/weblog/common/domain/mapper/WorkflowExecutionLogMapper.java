package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionLogDO;

import java.util.List;

public interface WorkflowExecutionLogMapper extends BaseMapper<WorkflowExecutionLogDO> {

    default List<WorkflowExecutionLogDO> selectByExecutionId(Long executionId) {
        return selectList(Wrappers.<WorkflowExecutionLogDO>lambdaQuery()
                .eq(WorkflowExecutionLogDO::getExecutionId, executionId)
                .orderByAsc(WorkflowExecutionLogDO::getExecuteTime));
    }
}
