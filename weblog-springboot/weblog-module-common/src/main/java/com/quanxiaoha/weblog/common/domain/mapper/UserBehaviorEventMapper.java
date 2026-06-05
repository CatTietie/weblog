package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorEventDO;

import java.time.LocalDateTime;
import java.util.List;

public interface UserBehaviorEventMapper extends BaseMapper<UserBehaviorEventDO> {

    default List<UserBehaviorEventDO> selectByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(Wrappers.<UserBehaviorEventDO>lambdaQuery()
                .eq(UserBehaviorEventDO::getUserId, userId)
                .ge(UserBehaviorEventDO::getCreateTime, startTime)
                .le(UserBehaviorEventDO::getCreateTime, endTime));
    }

    default List<UserBehaviorEventDO> selectByUserIdAndArticleId(Long userId, Long articleId) {
        return selectList(Wrappers.<UserBehaviorEventDO>lambdaQuery()
                .eq(UserBehaviorEventDO::getUserId, userId)
                .eq(UserBehaviorEventDO::getArticleId, articleId));
    }

    default int deleteByCreateTimeBefore(LocalDateTime time) {
        return delete(Wrappers.<UserBehaviorEventDO>lambdaQuery()
                .lt(UserBehaviorEventDO::getCreateTime, time));
    }

    default List<Long> selectDistinctUserIds(LocalDateTime startTime) {
        return selectList(Wrappers.<UserBehaviorEventDO>lambdaQuery()
                .ge(UserBehaviorEventDO::getCreateTime, startTime)
                .select(UserBehaviorEventDO::getUserId)
                .groupBy(UserBehaviorEventDO::getUserId))
                .stream()
                .map(UserBehaviorEventDO::getUserId)
                .collect(java.util.stream.Collectors.toList());
    }
}
