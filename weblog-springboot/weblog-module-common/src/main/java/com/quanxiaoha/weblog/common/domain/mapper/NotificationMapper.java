package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.config.InsertBatchMapper;
import com.quanxiaoha.weblog.common.domain.dos.NotificationDO;

public interface NotificationMapper extends InsertBatchMapper<NotificationDO> {

    default Long selectUnreadCount(Long receiverId) {
        return selectCount(Wrappers.<NotificationDO>lambdaQuery()
                .eq(NotificationDO::getReceiverId, receiverId)
                .eq(NotificationDO::getIsRead, false));
    }

    default Page<NotificationDO> selectPageByReceiverId(Long current, Long size, Long receiverId) {
        Page<NotificationDO> page = new Page<>(current, size);
        LambdaQueryWrapper<NotificationDO> wrapper = Wrappers.<NotificationDO>lambdaQuery()
                .eq(NotificationDO::getReceiverId, receiverId)
                .orderByDesc(NotificationDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    default int markAllAsRead(Long receiverId) {
        return update(null, Wrappers.<NotificationDO>lambdaUpdate()
                .set(NotificationDO::getIsRead, true)
                .eq(NotificationDO::getReceiverId, receiverId)
                .eq(NotificationDO::getIsRead, false));
    }
}
