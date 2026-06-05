package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.RecommendationConfigDO;

import java.util.List;

public interface RecommendationConfigMapper extends BaseMapper<RecommendationConfigDO> {

    default List<RecommendationConfigDO> selectActiveByType(Integer configType) {
        return selectList(Wrappers.<RecommendationConfigDO>lambdaQuery()
                .eq(RecommendationConfigDO::getConfigType, configType)
                .eq(RecommendationConfigDO::getIsActive, true));
    }

    default List<RecommendationConfigDO> selectAllActive() {
        return selectList(Wrappers.<RecommendationConfigDO>lambdaQuery()
                .eq(RecommendationConfigDO::getIsActive, true));
    }
}
