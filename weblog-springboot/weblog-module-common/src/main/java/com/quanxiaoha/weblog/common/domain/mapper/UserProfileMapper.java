package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.UserProfileDO;

public interface UserProfileMapper extends BaseMapper<UserProfileDO> {

    default UserProfileDO selectByUserId(Long userId) {
        return selectOne(Wrappers.<UserProfileDO>lambdaQuery()
                .eq(UserProfileDO::getUserId, userId));
    }
}
