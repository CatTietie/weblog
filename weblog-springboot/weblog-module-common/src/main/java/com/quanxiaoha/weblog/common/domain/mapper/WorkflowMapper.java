package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowDO;

import java.util.List;

public interface WorkflowMapper extends BaseMapper<WorkflowDO> {

    default List<WorkflowDO> selectEnabledByTriggerType(String triggerType) {
        return selectList(Wrappers.<WorkflowDO>lambdaQuery()
                .eq(WorkflowDO::getTriggerType, triggerType)
                .eq(WorkflowDO::getIsEnabled, true)
                .eq(WorkflowDO::getIsDeleted, false));
    }

    default Page<WorkflowDO> selectPageList(Long current, Long size) {
        Page<WorkflowDO> page = new Page<>(current, size);
        return selectPage(page, Wrappers.<WorkflowDO>lambdaQuery()
                .eq(WorkflowDO::getIsDeleted, false)
                .orderByDesc(WorkflowDO::getCreateTime));
    }
}
