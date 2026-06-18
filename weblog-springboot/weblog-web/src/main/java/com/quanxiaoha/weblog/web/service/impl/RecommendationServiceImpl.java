package com.quanxiaoha.weblog.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.admin.cache.RecommendationCacheService;
import com.quanxiaoha.weblog.admin.schedule.UserProfileCalculateTask;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.recommendation.FindRecommendedArticlesRspVO;
import com.quanxiaoha.weblog.web.model.vo.recommendation.FindRelatedArticlesReqVO;
import com.quanxiaoha.weblog.web.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecommendationCacheService cacheService;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RecommendationLogMapper recommendationLogMapper;
    @Autowired
    private UserBehaviorEventMapper userBehaviorEventMapper;

    private static final int HOME_RECOMMEND_COUNT = 10;
    private static final int RELATED_RECOMMEND_COUNT = 6;

    @Override
    public Response findHomeRecommendations() {
        Long userId = getCurrentUserId();

        List<Long> recommendedIds;
        if (userId != null) {
            UserProfileDO profile = userProfileMapper.selectByUserId(userId);
            if (profile != null && profile.getTagWeights() != null) {
                recommendedIds = personalizedRecommend(userId, profile);
            } else {
                recommendedIds = coldStartRecommend();
            }
        } else {
            recommendedIds = coldStartRecommend();
        }

        // 个性化推荐为空时 fallback 到冷启动
        if (CollectionUtils.isEmpty(recommendedIds)) {
            recommendedIds = coldStartRecommend();
        }

        // 冷启动仍为空则直接查最新已发布文章兜底
        if (CollectionUtils.isEmpty(recommendedIds)) {
            recommendedIds = fallbackLatestArticles();
        }

        if (CollectionUtils.isEmpty(recommendedIds)) {
            return Response.success(Collections.emptyList());
        }

        List<FindRecommendedArticlesRspVO> result = buildArticleVOList(recommendedIds);

        // 记录推荐日志
        if (userId != null && !CollectionUtils.isEmpty(recommendedIds)) {
            saveRecommendationLogs(userId, recommendedIds, 1);
        }

        return Response.success(result);
    }

    @Override
    public Response findRelatedArticles(FindRelatedArticlesReqVO vo) {
        Long articleId = vo.getArticleId();
        Map<Long, List<Long>> articleTagMap = cacheService.getArticleTagMap();
        List<Long> currentTags = articleTagMap.getOrDefault(articleId, Collections.emptyList());

        List<Long> relatedIds;
        if (CollectionUtils.isEmpty(currentTags)) {
            // 无标签时 fallback 到最新文章
            relatedIds = fallbackLatestArticles(articleId, RELATED_RECOMMEND_COUNT);
        } else {
            RecommendationCacheService.ConfigSnapshot config = cacheService.getConfigSnapshot();
            Long userId = getCurrentUserId();
            Map<Long, Double> userTagWeights = loadUserTagWeights(userId);

            List<ScoredArticle> scored = new ArrayList<>();
            for (Map.Entry<Long, List<Long>> entry : articleTagMap.entrySet()) {
                Long candidateId = entry.getKey();
                if (candidateId.equals(articleId)) continue;

                List<Long> candidateTags = entry.getValue();
                long sharedTags = candidateTags.stream().filter(currentTags::contains).count();
                if (sharedTags == 0) continue;

                double score = sharedTags * 1.0;

                if (!userTagWeights.isEmpty()) {
                    double affinity = candidateTags.stream()
                            .mapToDouble(t -> userTagWeights.getOrDefault(t, 0.0))
                            .sum();
                    score += affinity * 0.5;
                }

                scored.add(new ScoredArticle(candidateId, score));
            }

            scored.sort((a, b) -> Double.compare(b.score, a.score));
            relatedIds = scored.stream()
                    .limit(RELATED_RECOMMEND_COUNT)
                    .map(s -> s.articleId)
                    .collect(Collectors.toList());

            // 相关推荐不足时用最新文章补齐
            if (relatedIds.size() < RELATED_RECOMMEND_COUNT) {
                List<Long> fallback = fallbackLatestArticles(articleId, RELATED_RECOMMEND_COUNT - relatedIds.size());
                for (Long id : fallback) {
                    if (!relatedIds.contains(id)) {
                        relatedIds.add(id);
                    }
                }
            }
        }

        List<FindRecommendedArticlesRspVO> result = buildArticleVOList(relatedIds);

        Long userId = getCurrentUserId();
        if (userId != null && !CollectionUtils.isEmpty(relatedIds)) {
            saveRecommendationLogs(userId, relatedIds, 2);
        }

        return Response.success(result);
    }

    private List<Long> personalizedRecommend(Long userId, UserProfileDO profile) {
        List<UserProfileCalculateTask.TagWeight> tagWeights = JSON.parseArray(
                profile.getTagWeights(), UserProfileCalculateTask.TagWeight.class);
        if (CollectionUtils.isEmpty(tagWeights)) return coldStartRecommend();

        Map<Long, Double> weightMap = new HashMap<>();
        for (UserProfileCalculateTask.TagWeight tw : tagWeights) {
            weightMap.put(tw.getTagId(), tw.getWeight());
        }

        Map<Long, List<Long>> articleTagMap = cacheService.getArticleTagMap();
        RecommendationCacheService.ConfigSnapshot config = cacheService.getConfigSnapshot();

        // 获取已读文章排除
        Set<Long> readArticleIds = getReadArticleIds(userId);

        LocalDateTime now = LocalDateTime.now();
        // 获取文章创建时间用于计算时效性
        List<ArticleDO> publishedArticles = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getIsDeleted, false)
                        .eq(ArticleDO::getStatus, 1)
                        .ge(ArticleDO::getCreateTime, now.minusDays(90))
                        .select(ArticleDO::getId, ArticleDO::getCreateTime, ArticleDO::getReadNum));

        // 计算阅读量百分位
        List<Long> readNums = publishedArticles.stream()
                .map(ArticleDO::getReadNum)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        long maxReadNum = readNums.isEmpty() ? 1 : readNums.get(readNums.size() - 1);

        List<ScoredArticle> scored = new ArrayList<>();
        for (ArticleDO article : publishedArticles) {
            if (readArticleIds.contains(article.getId())) continue;

            List<Long> tags = articleTagMap.getOrDefault(article.getId(), Collections.emptyList());

            // 检查是否被屏蔽标签
            boolean blocked = tags.stream().anyMatch(t -> config.getBlockedTagIds().contains(t));
            if (blocked) continue;

            // 基于标签权重的得分
            double score = tags.stream()
                    .mapToDouble(t -> weightMap.getOrDefault(t, 0.0))
                    .sum();

            // 时效性加分 (0~0.3)
            long daysAgo = ChronoUnit.DAYS.between(article.getCreateTime(), now);
            double recencyBonus = Math.max(0, 0.3 * (1.0 - daysAgo / 90.0));
            score += recencyBonus;

            // 热门度加分 (0~0.2)
            long readNum = article.getReadNum() != null ? article.getReadNum() : 0;
            double popularityBonus = maxReadNum > 0 ? 0.2 * readNum / maxReadNum : 0;
            score += popularityBonus;

            // 管理员权重调整
            Double adjustment = config.getArticleWeightAdjustments().get(article.getId());
            if (adjustment != null) {
                score += adjustment;
            }

            scored.add(new ScoredArticle(article.getId(), score));
        }

        scored.sort((a, b) -> Double.compare(b.score, a.score));

        List<Long> result = new ArrayList<>();
        // 先加入置顶文章
        for (Long pinnedId : config.getPinnedArticleIds()) {
            if (!readArticleIds.contains(pinnedId)) {
                result.add(pinnedId);
            }
        }

        // 再加入评分最高的文章
        for (ScoredArticle sa : scored) {
            if (result.size() >= HOME_RECOMMEND_COUNT) break;
            if (!result.contains(sa.articleId)) {
                result.add(sa.articleId);
            }
        }

        return result.subList(0, Math.min(result.size(), HOME_RECOMMEND_COUNT));
    }

    private List<Long> coldStartRecommend() {
        RecommendationCacheService.ConfigSnapshot config = cacheService.getConfigSnapshot();
        List<Long> hotIds = cacheService.getHotArticleIds();

        Set<Long> result = new LinkedHashSet<>();

        // 置顶文章（编辑精选）
        result.addAll(config.getPinnedArticleIds());

        // 热门文章
        if (!CollectionUtils.isEmpty(hotIds)) {
            result.addAll(hotIds.subList(0, Math.min(7, hotIds.size())));
        }

        // 始终用最新文章补充到目标数量
        List<ArticleDO> latest = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getIsDeleted, false)
                        .eq(ArticleDO::getStatus, 1)
                        .orderByDesc(ArticleDO::getCreateTime)
                        .last("LIMIT " + HOME_RECOMMEND_COUNT));
        for (ArticleDO a : latest) {
            result.add(a.getId());
        }

        List<Long> list = new ArrayList<>(result);
        return list.subList(0, Math.min(list.size(), HOME_RECOMMEND_COUNT));
    }

    private List<Long> fallbackLatestArticles() {
        return fallbackLatestArticles(null, HOME_RECOMMEND_COUNT);
    }

    private List<Long> fallbackLatestArticles(Long excludeArticleId, int limit) {
        List<ArticleDO> articles = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getIsDeleted, false)
                        .eq(ArticleDO::getStatus, 1)
                        .ne(excludeArticleId != null, ArticleDO::getId, excludeArticleId)
                        .orderByDesc(ArticleDO::getCreateTime)
                        .last("LIMIT " + limit));
        return articles.stream().map(ArticleDO::getId).collect(Collectors.toList());
    }

    private Set<Long> getReadArticleIds(Long userId) {
        List<UserBehaviorEventDO> events = userBehaviorEventMapper.selectByUserIdAndTimeRange(
                userId,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now());
        return events.stream()
                .filter(e -> e.getArticleId() != null)
                .map(UserBehaviorEventDO::getArticleId)
                .collect(Collectors.toSet());
    }

    private Map<Long, Double> loadUserTagWeights(Long userId) {
        if (userId == null) return Collections.emptyMap();
        UserProfileDO profile = userProfileMapper.selectByUserId(userId);
        if (profile == null || profile.getTagWeights() == null) return Collections.emptyMap();
        List<UserProfileCalculateTask.TagWeight> tagWeights = JSON.parseArray(
                profile.getTagWeights(), UserProfileCalculateTask.TagWeight.class);
        if (CollectionUtils.isEmpty(tagWeights)) return Collections.emptyMap();
        Map<Long, Double> map = new HashMap<>();
        for (UserProfileCalculateTask.TagWeight tw : tagWeights) {
            map.put(tw.getTagId(), tw.getWeight());
        }
        return map;
    }

    private List<FindRecommendedArticlesRspVO> buildArticleVOList(List<Long> articleIds) {
        if (CollectionUtils.isEmpty(articleIds)) return Collections.emptyList();

        List<ArticleDO> articles = articleMapper.selectBatchIds(articleIds);
        Map<Long, ArticleDO> articleMap = articles.stream()
                .collect(Collectors.toMap(ArticleDO::getId, a -> a));

        Map<Long, List<Long>> articleTagMap = cacheService.getArticleTagMap();
        // 获取所有涉及的标签名
        Set<Long> allTagIds = new HashSet<>();
        for (Long id : articleIds) {
            List<Long> tagIds = articleTagMap.getOrDefault(id, Collections.emptyList());
            allTagIds.addAll(tagIds);
        }
        Map<Long, String> tagNameMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            List<TagDO> tags = tagMapper.selectBatchIds(new ArrayList<>(allTagIds));
            for (TagDO tag : tags) {
                tagNameMap.put(tag.getId(), tag.getName());
            }
        }

        List<FindRecommendedArticlesRspVO> result = new ArrayList<>();
        for (Long id : articleIds) {
            ArticleDO article = articleMap.get(id);
            if (article == null) continue;

            List<Long> tagIds = articleTagMap.getOrDefault(id, Collections.emptyList());
            List<FindRecommendedArticlesRspVO.TagInfo> tags = tagIds.stream()
                    .map(tagId -> new FindRecommendedArticlesRspVO.TagInfo(tagId, tagNameMap.getOrDefault(tagId, "")))
                    .collect(Collectors.toList());

            FindRecommendedArticlesRspVO vo = FindRecommendedArticlesRspVO.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .cover(article.getCover())
                    .summary(article.getSummary())
                    .createDate(article.getCreateTime() != null ? article.getCreateTime().toLocalDate().toString() : "")
                    .readNum(article.getReadNum())
                    .tags(tags)
                    .build();
            result.add(vo);
        }
        return result;
    }

    private void saveRecommendationLogs(Long userId, List<Long> articleIds, Integer source) {
        LocalDateTime now = LocalDateTime.now();
        for (Long articleId : articleIds) {
            RecommendationLogDO logDO = RecommendationLogDO.builder()
                    .userId(userId)
                    .articleId(articleId)
                    .source(source)
                    .isClicked(false)
                    .recommendTime(now)
                    .build();
            recommendationLogMapper.insert(logDO);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return null;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserDO userDO = userMapper.findByUsername(username);
        return userDO != null ? userDO.getId() : null;
    }

    private static class ScoredArticle {
        Long articleId;
        double score;

        ScoredArticle(Long articleId, double score) {
            this.articleId = articleId;
            this.score = score;
        }
    }
}
