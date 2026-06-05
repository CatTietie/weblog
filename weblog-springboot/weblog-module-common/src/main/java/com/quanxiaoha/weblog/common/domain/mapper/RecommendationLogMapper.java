package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.RecommendationLogDO;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface RecommendationLogMapper extends BaseMapper<RecommendationLogDO> {

    default List<RecommendationLogDO> selectByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(Wrappers.<RecommendationLogDO>lambdaQuery()
                .eq(RecommendationLogDO::getUserId, userId)
                .ge(RecommendationLogDO::getRecommendTime, startTime)
                .le(RecommendationLogDO::getRecommendTime, endTime));
    }

    @Select("SELECT COUNT(*) FROM t_recommendation_log WHERE recommend_time >= #{startTime} AND recommend_time < #{endTime}")
    Long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM t_recommendation_log WHERE is_clicked = 1 AND recommend_time >= #{startTime} AND recommend_time < #{endTime}")
    Long countClickedByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    default int updateClickedByUserAndArticle(Long userId, Long articleId, Integer source) {
        RecommendationLogDO log = selectOne(Wrappers.<RecommendationLogDO>lambdaQuery()
                .eq(RecommendationLogDO::getUserId, userId)
                .eq(RecommendationLogDO::getArticleId, articleId)
                .eq(RecommendationLogDO::getSource, source)
                .eq(RecommendationLogDO::getIsClicked, false)
                .orderByDesc(RecommendationLogDO::getRecommendTime)
                .last("LIMIT 1"));
        if (log != null) {
            log.setIsClicked(true);
            log.setClickTime(LocalDateTime.now());
            return updateById(log);
        }
        return 0;
    }
}
