package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.RoleDO;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleDO> {

    default RoleDO findByCode(String code) {
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getCode, code);
        return selectOne(wrapper);
    }

    default List<RoleDO> findAll() {
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(RoleDO::getSort);
        return selectList(wrapper);
    }
}
