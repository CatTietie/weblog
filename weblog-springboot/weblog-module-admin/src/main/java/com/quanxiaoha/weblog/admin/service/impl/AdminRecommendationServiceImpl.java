package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.AddRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.DeleteRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.UpdateRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.UpdateUserProfileReqVO;
import com.quanxiaoha.weblog.admin.service.AdminRecommendationService;
import com.quanxiaoha.weblog.common.domain.dos.RecommendationConfigDO;
import com.quanxiaoha.weblog.common.domain.dos.UserProfileDO;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendationConfigMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendationLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserProfileMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class AdminRecommendationServiceImpl implements AdminRecommendationService {

    @Autowired
    private RecommendationConfigMapper recommendationConfigMapper;
    @Autowired
    private RecommendationLogMapper recommendationLogMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public Response findDashboard() {
        Map<String, Object> result = new HashMap<>();

        // 近30天每日CTR
        List<Map<String, Object>> dailyCtr = new ArrayList<>();
        LocalDate today = LocalDate.now();
        long totalRecommends = 0;
        long totalClicks = 0;

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            Long dayRecommends = recommendationLogMapper.countByTimeRange(start, end);
            Long dayClicks = recommendationLogMapper.countClickedByTimeRange(start, end);
            dayRecommends = dayRecommends != null ? dayRecommends : 0L;
            dayClicks = dayClicks != null ? dayClicks : 0L;

            double ctr = dayRecommends > 0 ? (double) dayClicks / dayRecommends * 100 : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("recommends", dayRecommends);
            item.put("clicks", dayClicks);
            item.put("ctr", Math.round(ctr * 100.0) / 100.0);
            dailyCtr.add(item);

            totalRecommends += dayRecommends;
            totalClicks += dayClicks;
        }

        result.put("dailyCtr", dailyCtr);
        result.put("totalRecommends", totalRecommends);
        result.put("totalClicks", totalClicks);
        result.put("averageCtr", totalRecommends > 0 ? Math.round((double) totalClicks / totalRecommends * 10000.0) / 100.0 : 0);

        return Response.success(result);
    }

    @Override
    public Response findConfigList() {
        List<RecommendationConfigDO> configs = recommendationConfigMapper.selectList(
                Wrappers.<RecommendationConfigDO>lambdaQuery()
                        .orderByDesc(RecommendationConfigDO::getCreateTime));
        return Response.success(configs);
    }

    @Override
    public Response addConfig(AddRecommendConfigReqVO vo) {
        RecommendationConfigDO config = RecommendationConfigDO.builder()
                .configType(vo.getConfigType())
                .articleId(vo.getArticleId())
                .tagId(vo.getTagId())
                .weightAdjustment(vo.getWeightAdjustment())
                .isActive(true)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        recommendationConfigMapper.insert(config);
        return Response.success();
    }

    @Override
    public Response updateConfig(UpdateRecommendConfigReqVO vo) {
        RecommendationConfigDO config = recommendationConfigMapper.selectById(vo.getId());
        if (config == null) {
            return Response.fail("配置不存在");
        }
        if (vo.getConfigType() != null) config.setConfigType(vo.getConfigType());
        if (vo.getArticleId() != null) config.setArticleId(vo.getArticleId());
        if (vo.getTagId() != null) config.setTagId(vo.getTagId());
        if (vo.getWeightAdjustment() != null) config.setWeightAdjustment(vo.getWeightAdjustment());
        if (vo.getIsActive() != null) config.setIsActive(vo.getIsActive());
        config.setUpdateTime(LocalDateTime.now());
        recommendationConfigMapper.updateById(config);
        return Response.success();
    }

    @Override
    public Response deleteConfig(DeleteRecommendConfigReqVO vo) {
        recommendationConfigMapper.deleteById(vo.getId());
        return Response.success();
    }

    @Override
    public Response findProfileList(Long current, Long size) {
        Page<UserProfileDO> page = new Page<>(current, size);
        Page<UserProfileDO> result = userProfileMapper.selectPage(page,
                Wrappers.<UserProfileDO>lambdaQuery()
                        .orderByDesc(UserProfileDO::getUpdateTime));
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        return Response.success(data);
    }

    @Override
    public Response updateProfile(UpdateUserProfileReqVO vo) {
        UserProfileDO profile = userProfileMapper.selectByUserId(vo.getUserId());
        if (profile == null) {
            profile = UserProfileDO.builder()
                    .userId(vo.getUserId())
                    .tagWeights(vo.getTagWeights())
                    .lastCalculatedTime(LocalDateTime.now())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            userProfileMapper.insert(profile);
        } else {
            profile.setTagWeights(vo.getTagWeights());
            profile.setUpdateTime(LocalDateTime.now());
            userProfileMapper.updateById(profile);
        }
        return Response.success();
    }
}
