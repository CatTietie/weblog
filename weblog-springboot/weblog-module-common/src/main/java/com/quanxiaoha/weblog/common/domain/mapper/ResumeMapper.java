package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.ResumeDO;

import java.util.List;

public interface ResumeMapper extends BaseMapper<ResumeDO> {

    default List<ResumeDO> selectByUserId(Long userId) {
        LambdaQueryWrapper<ResumeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeDO::getUserId, userId)
                .orderByDesc(ResumeDO::getUpdateTime);
        return selectList(wrapper);
    }

    default ResumeDO selectByIdAndUserId(Long id, Long userId) {
        LambdaQueryWrapper<ResumeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeDO::getId, id)
                .eq(ResumeDO::getUserId, userId);
        return selectOne(wrapper);
    }

    default ResumeDO selectByShareCode(String shareCode) {
        LambdaQueryWrapper<ResumeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeDO::getShareCode, shareCode);
        return selectOne(wrapper);
    }
}
