package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.ApplicationReminderLogDO;

public interface ApplicationReminderLogMapper extends BaseMapper<ApplicationReminderLogDO> {

    default Page<ApplicationReminderLogDO> selectPageList(long current, long size) {
        Page<ApplicationReminderLogDO> page = new Page<>(current, size);
        LambdaQueryWrapper<ApplicationReminderLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApplicationReminderLogDO::getExecuteTime);
        return selectPage(page, wrapper);
    }
}
