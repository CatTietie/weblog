package com.quanxiaoha.weblog.admin.cache;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleTagRelDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendationConfigDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendationConfigMapper;
import com.quanxiaoha.weblog.common.enums.RecommendConfigTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationCacheService {

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RecommendationConfigMapper recommendationConfigMapper;

    private final Cache<String, Map<Long, List<Long>>> articleTagMapCache = Caffeine.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final Cache<String, ConfigSnapshot> configCache = Caffeine.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    private final Cache<String, List<Long>> hotArticlesCache = Caffeine.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    public Map<Long, List<Long>> getArticleTagMap() {
        return articleTagMapCache.get("ALL", k -> loadArticleTagMap());
    }

    public ConfigSnapshot getConfigSnapshot() {
        return configCache.get("CONFIG", k -> loadConfigSnapshot());
    }

    public List<Long> getHotArticleIds() {
        return hotArticlesCache.get("HOT", k -> loadHotArticles());
    }

    public void refreshAll() {
        articleTagMapCache.invalidateAll();
        configCache.invalidateAll();
        hotArticlesCache.invalidateAll();
        getArticleTagMap();
        getConfigSnapshot();
        getHotArticleIds();
    }

    private Map<Long, List<Long>> loadArticleTagMap() {
        List<ArticleTagRelDO> allRels = articleTagRelMapper.selectList(null);
        if (CollectionUtils.isEmpty(allRels)) return Collections.emptyMap();
        return allRels.stream().collect(Collectors.groupingBy(
                ArticleTagRelDO::getArticleId,
                Collectors.mapping(ArticleTagRelDO::getTagId, Collectors.toList())));
    }

    private ConfigSnapshot loadConfigSnapshot() {
        List<RecommendationConfigDO> configs = recommendationConfigMapper.selectAllActive();
        ConfigSnapshot snapshot = new ConfigSnapshot();

        for (RecommendationConfigDO config : configs) {
            if (RecommendConfigTypeEnum.PIN_TOP.getCode().equals(config.getConfigType())) {
                if (config.getArticleId() != null) {
                    snapshot.getPinnedArticleIds().add(config.getArticleId());
                }
            } else if (RecommendConfigTypeEnum.TAG_BLOCK.getCode().equals(config.getConfigType())) {
                if (config.getTagId() != null) {
                    snapshot.getBlockedTagIds().add(config.getTagId());
                }
            } else if (RecommendConfigTypeEnum.WEIGHT_ADJUST.getCode().equals(config.getConfigType())) {
                if (config.getArticleId() != null && config.getWeightAdjustment() != null) {
                    snapshot.getArticleWeightAdjustments().put(
                            config.getArticleId(), config.getWeightAdjustment().doubleValue());
                }
            }
        }
        return snapshot;
    }

    private List<Long> loadHotArticles() {
        List<ArticleDO> articles = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getIsDeleted, false)
                        .eq(ArticleDO::getStatus, 1)
                        .orderByDesc(ArticleDO::getReadNum)
                        .last("LIMIT 20"));
        return articles.stream().map(ArticleDO::getId).collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfigSnapshot {
        private List<Long> pinnedArticleIds = new ArrayList<>();
        private Set<Long> blockedTagIds = new HashSet<>();
        private Map<Long, Double> articleWeightAdjustments = new HashMap<>();
    }
}
