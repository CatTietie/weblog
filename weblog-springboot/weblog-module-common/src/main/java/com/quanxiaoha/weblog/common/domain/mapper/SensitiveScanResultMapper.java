package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quanxiaoha.weblog.common.domain.dos.SensitiveScanResultDO;

public interface SensitiveScanResultMapper extends BaseMapper<SensitiveScanResultDO> {

    default Page<SensitiveScanResultDO> selectPageByTaskId(Long current, Long size, Long taskId, Integer handled) {
        Page<SensitiveScanResultDO> page = new Page<>(current, size);
        LambdaQueryWrapper<SensitiveScanResultDO> wrapper = new LambdaQueryWrapper<SensitiveScanResultDO>()
                .eq(SensitiveScanResultDO::getTaskId, taskId)
                .eq(handled != null, SensitiveScanResultDO::getHandled, handled)
                .orderByDesc(SensitiveScanResultDO::getCreateTime);
        return selectPage(page, wrapper);
    }
}
