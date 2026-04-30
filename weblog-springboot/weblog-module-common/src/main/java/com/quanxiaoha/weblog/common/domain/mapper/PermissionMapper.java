package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.PermissionDO;

import java.util.List;

public interface PermissionMapper extends BaseMapper<PermissionDO> {

    default List<PermissionDO> findAll() {
        LambdaQueryWrapper<PermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PermissionDO::getSort);
        return selectList(wrapper);
    }

    default List<PermissionDO> findByType(String type) {
        LambdaQueryWrapper<PermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionDO::getType, type);
        wrapper.orderByAsc(PermissionDO::getSort);
        return selectList(wrapper);
    }
}
