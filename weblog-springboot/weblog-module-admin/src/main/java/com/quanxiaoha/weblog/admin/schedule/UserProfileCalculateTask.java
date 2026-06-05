package com.quanxiaoha.weblog.admin.schedule;

import com.alibaba.fastjson2.JSON;
import com.quanxiaoha.weblog.common.domain.dos.ArticleTagRelDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorEventDO;
import com.quanxiaoha.weblog.common.domain.dos.UserProfileDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserBehaviorEventMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserProfileMapper;
import com.quanxiaoha.weblog.common.enums.BehaviorEventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserProfileCalculateTask {

    @Autowired
    private UserBehaviorEventMapper userBehaviorEventMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private TagMapper tagMapper;

    private static final int PROFILE_DAYS = 30;
    private static final int MAX_TAGS = 20;
    private static final double DECAY_FACTOR = 0.95;

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        log.info("==> 开始计算用户画像...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusDays(PROFILE_DAYS);

        List<Long> userIds = userBehaviorEventMapper.selectDistinctUserIds(startTime);
        log.info("==> 需要计算画像的用户数: {}", userIds.size());

        Map<String, Long> tagNameToIdMap = buildTagNameMap();

        int count = 0;
        for (Long userId : userIds) {
            try {
                calculateAndSaveProfile(userId, startTime, now, tagNameToIdMap);
                count++;
            } catch (Exception e) {
                log.error("==> 计算用户画像失败, userId: {}", userId, e);
            }
        }

        log.info("==> 用户画像计算完成, 成功: {}/{}", count, userIds.size());
    }

    private void calculateAndSaveProfile(Long userId, LocalDateTime startTime, LocalDateTime now, Map<String, Long> tagNameToIdMap) {
        List<UserBehaviorEventDO> events = userBehaviorEventMapper.selectByUserIdAndTimeRange(userId, startTime, now);
        if (CollectionUtils.isEmpty(events)) return;

        Map<Long, Double> tagScores = new HashMap<>();

        for (UserBehaviorEventDO event : events) {
            long daysAgo = ChronoUnit.DAYS.between(event.getCreateTime(), now);
            double decay = Math.pow(DECAY_FACTOR, daysAgo);

            if (BehaviorEventTypeEnum.READ.getCode().equals(event.getEventType())) {
                int duration = event.getDurationSeconds() != null ? event.getDurationSeconds() : 0;
                double score = (1.0 + Math.min(duration / 120.0, 3.0)) * decay;
                if (event.getArticleId() != null) {
                    List<ArticleTagRelDO> rels = articleTagRelMapper.selectByArticleId(event.getArticleId());
                    for (ArticleTagRelDO rel : rels) {
                        tagScores.merge(rel.getTagId(), score, Double::sum);
                    }
                }
            } else if (BehaviorEventTypeEnum.TAG_CLICK.getCode().equals(event.getEventType())) {
                if (event.getTagId() != null) {
                    tagScores.merge(event.getTagId(), 2.0 * decay, Double::sum);
                }
            } else if (BehaviorEventTypeEnum.SEARCH.getCode().equals(event.getEventType())) {
                if (event.getKeyword() != null) {
                    String keyword = event.getKeyword().trim().toLowerCase();
                    for (Map.Entry<String, Long> entry : tagNameToIdMap.entrySet()) {
                        if (entry.getKey().contains(keyword) || keyword.contains(entry.getKey())) {
                            tagScores.merge(entry.getValue(), 1.5 * decay, Double::sum);
                        }
                    }
                }
            }
        }

        if (tagScores.isEmpty()) return;

        double maxScore = tagScores.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        List<TagWeight> tagWeights = tagScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(MAX_TAGS)
                .map(e -> new TagWeight(e.getKey(), Math.round(e.getValue() / maxScore * 100.0) / 100.0))
                .collect(Collectors.toList());

        String tagWeightsJson = JSON.toJSONString(tagWeights);

        UserProfileDO existing = userProfileMapper.selectByUserId(userId);
        if (existing != null) {
            existing.setTagWeights(tagWeightsJson);
            existing.setLastCalculatedTime(now);
            existing.setUpdateTime(now);
            userProfileMapper.updateById(existing);
        } else {
            UserProfileDO profile = UserProfileDO.builder()
                    .userId(userId)
                    .tagWeights(tagWeightsJson)
                    .lastCalculatedTime(now)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            userProfileMapper.insert(profile);
        }
    }

    private Map<String, Long> buildTagNameMap() {
        List<TagDO> tags = tagMapper.selectList(null);
        Map<String, Long> map = new HashMap<>();
        for (TagDO tag : tags) {
            map.put(tag.getName().toLowerCase(), tag.getId());
        }
        return map;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TagWeight {
        private Long tagId;
        private Double weight;
    }
}
