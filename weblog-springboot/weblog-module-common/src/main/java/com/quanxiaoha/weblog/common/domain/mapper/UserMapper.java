package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;

import java.time.LocalDateTime;

/**
 * @author: Group 5

 * @date: 2023-08-22 17:06
 * @description: TODO
 **/
public interface UserMapper extends BaseMapper<UserDO> {
    default UserDO findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);
        return selectOne(wrapper);
    }

    default int updatePasswordByUsername(String username, String password) {
         LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
         // 设置要更新的字段
         wrapper.set(UserDO::getPassword, password);
         wrapper.set(UserDO::getUpdateTime, LocalDateTime.now());
         // 更新条件
         wrapper.eq(UserDO::getUsername, username);

         return update(null, wrapper);
    }

    default Page<UserDO> selectPageList(long current, long size, String username) {
        // 分页对象(查询第几页、每页多少数据)
        Page<UserDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .like(StringUtils.isNotBlank(username), UserDO::getUsername, username.trim()) // like 模块查询
                .orderByDesc(UserDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }
}
