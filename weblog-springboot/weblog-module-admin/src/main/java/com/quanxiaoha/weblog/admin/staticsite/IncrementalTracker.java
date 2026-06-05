package com.quanxiaoha.weblog.admin.staticsite;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.StaticGenPageDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.StaticGenPageMapper;
import com.quanxiaoha.weblog.common.enums.StaticPageTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IncrementalTracker {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Autowired
    private StaticGenPageMapper pageMapper;

    public ChangeSet detectChanges() {
        ChangeSet changeSet = new ChangeSet();

        // 获取所有已发布文章
        List<ArticleDO> publishedArticles = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getStatus, 1)
                        .eq(ArticleDO::getIsDeleted, false)
        );
        Set<Long> publishedIds = publishedArticles.stream()
                .map(ArticleDO::getId)
                .collect(Collectors.toSet());

        // 获取已生成的文章页记录
        List<StaticGenPageDO> generatedArticlePages = pageMapper.selectList(
                Wrappers.<StaticGenPageDO>lambdaQuery()
                        .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.ARTICLE.getCode())
        );
        Map<Long, StaticGenPageDO> generatedMap = new HashMap<>();
        for (StaticGenPageDO page : generatedArticlePages) {
            if (page.getRefId() != null) {
                generatedMap.put(page.getRefId(), page);
            }
        }

        // 检测新增文章（已发布但未生成）
        for (ArticleDO article : publishedArticles) {
            if (!generatedMap.containsKey(article.getId())) {
                changeSet.getNewArticleIds().add(article.getId());
            }
        }

        // 检测修改文章（updateTime > lastGeneratedTime）
        for (ArticleDO article : publishedArticles) {
            StaticGenPageDO generatedPage = generatedMap.get(article.getId());
            if (generatedPage != null && article.getUpdateTime() != null) {
                LocalDateTime lastGen = generatedPage.getLastGeneratedTime();
                if (lastGen == null || article.getUpdateTime().isAfter(lastGen)) {
                    changeSet.getModifiedArticleIds().add(article.getId());
                }
            }
        }

        // 检测删除文章（已生成但不在已发布列表中）
        for (Long generatedId : generatedMap.keySet()) {
            if (!publishedIds.contains(generatedId)) {
                changeSet.getDeletedArticleIds().add(generatedId);
            }
        }

        // 计算受影响的分类和标签
        Set<Long> allChangedArticleIds = new HashSet<>();
        allChangedArticleIds.addAll(changeSet.getNewArticleIds());
        allChangedArticleIds.addAll(changeSet.getModifiedArticleIds());
        allChangedArticleIds.addAll(changeSet.getDeletedArticleIds());

        if (!allChangedArticleIds.isEmpty()) {
            changeSet.setIndexDirty(true);
            changeSet.setArchiveDirty(true);

            // 找出受影响的分类
            articleCategoryRelMapper.selectList(Wrappers.emptyWrapper()).stream()
                    .filter(rel -> allChangedArticleIds.contains(rel.getArticleId()))
                    .forEach(rel -> changeSet.getAffectedCategoryIds().add(rel.getCategoryId()));

            // 找出受影响的标签
            articleTagRelMapper.selectList(Wrappers.emptyWrapper()).stream()
                    .filter(rel -> allChangedArticleIds.contains(rel.getArticleId()))
                    .forEach(rel -> changeSet.getAffectedTagIds().add(rel.getTagId()));
        }

        log.info("增量检测完成: 新增={}, 修改={}, 删除={}, 受影响分类={}, 受影响标签={}",
                changeSet.getNewArticleIds().size(),
                changeSet.getModifiedArticleIds().size(),
                changeSet.getDeletedArticleIds().size(),
                changeSet.getAffectedCategoryIds().size(),
                changeSet.getAffectedTagIds().size());

        return changeSet;
    }

    public boolean hasAnyChanges(ChangeSet changeSet) {
        return !changeSet.getNewArticleIds().isEmpty()
                || !changeSet.getModifiedArticleIds().isEmpty()
                || !changeSet.getDeletedArticleIds().isEmpty();
    }

    @Data
    public static class ChangeSet {
        private Set<Long> newArticleIds = new HashSet<>();
        private Set<Long> modifiedArticleIds = new HashSet<>();
        private Set<Long> deletedArticleIds = new HashSet<>();
        private Set<Long> affectedCategoryIds = new HashSet<>();
        private Set<Long> affectedTagIds = new HashSet<>();
        private boolean indexDirty = false;
        private boolean archiveDirty = false;
    }
}
