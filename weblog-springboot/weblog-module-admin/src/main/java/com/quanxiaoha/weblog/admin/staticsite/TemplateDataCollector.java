package com.quanxiaoha.weblog.admin.staticsite;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TemplateDataCollector {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    public BlogData collectBlogSettings() {
        BlogSettingsDO globalSettings = blogSettingsMapper.selectByUsername("global");
        BlogData data = new BlogData();
        if (globalSettings != null) {
            data.setName(globalSettings.getName());
            data.setLogo(globalSettings.getLogo());
            data.setIntroduction(globalSettings.getIntroduction());
            data.setAuthor(globalSettings.getAuthor());
            data.setAvatar(globalSettings.getAvatar());
        }
        return data;
    }

    public List<ArticleData> collectPublishedArticles() {
        List<ArticleDO> articles = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getStatus, 1)
                        .eq(ArticleDO::getIsDeleted, false)
                        .orderByDesc(ArticleDO::getCreateTime)
        );

        Map<Long, String> contentMap = new HashMap<>();
        List<ArticleContentDO> contents = articleContentMapper.selectList(Wrappers.emptyWrapper());
        for (ArticleContentDO content : contents) {
            contentMap.put(content.getArticleId(), content.getContent());
        }

        Map<Long, Long> articleCategoryMap = new HashMap<>();
        List<ArticleCategoryRelDO> catRels = articleCategoryRelMapper.selectList(Wrappers.emptyWrapper());
        for (ArticleCategoryRelDO rel : catRels) {
            articleCategoryMap.put(rel.getArticleId(), rel.getCategoryId());
        }

        Map<Long, String> categoryNameMap = new HashMap<>();
        List<CategoryDO> allCategories = categoryMapper.selectList(
                Wrappers.<CategoryDO>lambdaQuery().eq(CategoryDO::getIsDeleted, false)
        );
        for (CategoryDO cat : allCategories) {
            categoryNameMap.put(cat.getId(), cat.getName());
        }

        Map<Long, List<TagInfo>> articleTagsMap = new HashMap<>();
        List<ArticleTagRelDO> tagRels = articleTagRelMapper.selectList(Wrappers.emptyWrapper());
        Map<Long, String> tagNameMap = new HashMap<>();
        List<TagDO> allTags = tagMapper.selectList(
                Wrappers.<TagDO>lambdaQuery().eq(TagDO::getIsDeleted, false)
        );
        for (TagDO tag : allTags) {
            tagNameMap.put(tag.getId(), tag.getName());
        }
        for (ArticleTagRelDO rel : tagRels) {
            articleTagsMap.computeIfAbsent(rel.getArticleId(), k -> new ArrayList<>())
                    .add(new TagInfo(rel.getTagId(), tagNameMap.getOrDefault(rel.getTagId(), "")));
        }

        List<ArticleData> result = new ArrayList<>();
        for (ArticleDO article : articles) {
            ArticleData data = new ArticleData();
            data.setId(article.getId());
            data.setTitle(article.getTitle());
            data.setCover(article.getCover());
            data.setSummary(article.getSummary());
            data.setCreateTime(article.getCreateTime());
            data.setUpdateTime(article.getUpdateTime());
            data.setReadNum(article.getReadNum());
            data.setContent(contentMap.get(article.getId()));

            Long categoryId = articleCategoryMap.get(article.getId());
            data.setCategoryId(categoryId);
            data.setCategoryName(categoryId != null ? categoryNameMap.getOrDefault(categoryId, "") : "");

            data.setTags(articleTagsMap.getOrDefault(article.getId(), Collections.emptyList()));
            result.add(data);
        }

        return result;
    }

    public List<CategoryData> collectCategories() {
        List<CategoryDO> categories = categoryMapper.selectList(
                Wrappers.<CategoryDO>lambdaQuery().eq(CategoryDO::getIsDeleted, false)
        );

        List<ArticleCategoryRelDO> catRels = articleCategoryRelMapper.selectList(Wrappers.emptyWrapper());
        Set<Long> publishedArticleIds = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getStatus, 1)
                        .eq(ArticleDO::getIsDeleted, false)
                        .select(ArticleDO::getId)
        ).stream().map(ArticleDO::getId).collect(Collectors.toSet());

        Map<Long, Integer> catCountMap = new HashMap<>();
        for (ArticleCategoryRelDO rel : catRels) {
            if (publishedArticleIds.contains(rel.getArticleId())) {
                catCountMap.merge(rel.getCategoryId(), 1, Integer::sum);
            }
        }

        return categories.stream().map(cat -> {
            CategoryData data = new CategoryData();
            data.setId(cat.getId());
            data.setName(cat.getName());
            data.setArticleCount(catCountMap.getOrDefault(cat.getId(), 0));
            return data;
        }).filter(c -> c.getArticleCount() > 0).collect(Collectors.toList());
    }

    public List<TagData> collectTags() {
        List<TagDO> tags = tagMapper.selectList(
                Wrappers.<TagDO>lambdaQuery().eq(TagDO::getIsDeleted, false)
        );

        List<ArticleTagRelDO> tagRels = articleTagRelMapper.selectList(Wrappers.emptyWrapper());
        Set<Long> publishedArticleIds = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getStatus, 1)
                        .eq(ArticleDO::getIsDeleted, false)
                        .select(ArticleDO::getId)
        ).stream().map(ArticleDO::getId).collect(Collectors.toSet());

        Map<Long, Integer> tagCountMap = new HashMap<>();
        for (ArticleTagRelDO rel : tagRels) {
            if (publishedArticleIds.contains(rel.getArticleId())) {
                tagCountMap.merge(rel.getTagId(), 1, Integer::sum);
            }
        }

        return tags.stream().map(tag -> {
            TagData data = new TagData();
            data.setId(tag.getId());
            data.setName(tag.getName());
            data.setArticleCount(tagCountMap.getOrDefault(tag.getId(), 0));
            return data;
        }).filter(t -> t.getArticleCount() > 0).collect(Collectors.toList());
    }

    @Data
    public static class BlogData {
        private String name;
        private String logo;
        private String author;
        private String avatar;
        private String introduction;
    }

    @Data
    public static class ArticleData {
        private Long id;
        private String title;
        private String cover;
        private String summary;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;
        private Long readNum;
        private String content;
        private Long categoryId;
        private String categoryName;
        private List<TagInfo> tags;
    }

    @Data
    public static class CategoryData {
        private Long id;
        private String name;
        private Integer articleCount;
    }

    @Data
    public static class TagData {
        private Long id;
        private String name;
        private Integer articleCount;
    }

    @Data
    public static class TagInfo {
        private Long id;
        private String name;

        public TagInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
