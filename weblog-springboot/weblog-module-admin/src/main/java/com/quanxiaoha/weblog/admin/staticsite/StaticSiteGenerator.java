package com.quanxiaoha.weblog.admin.staticsite;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.StaticGenPageDO;
import com.quanxiaoha.weblog.common.domain.dos.StaticGenTaskDO;
import com.quanxiaoha.weblog.common.domain.dos.StaticSiteConfigDO;
import com.quanxiaoha.weblog.common.domain.mapper.StaticGenPageMapper;
import com.quanxiaoha.weblog.common.domain.mapper.StaticGenTaskMapper;
import com.quanxiaoha.weblog.common.domain.mapper.StaticSiteConfigMapper;
import com.quanxiaoha.weblog.common.enums.StaticGenTaskStatusEnum;
import com.quanxiaoha.weblog.common.enums.StaticPageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StaticSiteGenerator {

    @Autowired
    private TemplateDataCollector dataCollector;

    @Autowired
    private CommentWidgetRenderer commentRenderer;

    @Autowired
    private StaticSiteConfigMapper configMapper;

    @Autowired
    private StaticGenTaskMapper taskMapper;

    @Autowired
    private StaticGenPageMapper pageMapper;

    @Autowired
    private IncrementalTracker incrementalTracker;

    @Value("${weblog.static-site.output-dir:#{systemProperties['user.home'] + '/.weblog/static-output'}}")
    private String outputDir;

    private TemplateEngine templateEngine;
    private Parser markdownParser;
    private HtmlRenderer htmlRenderer;

    private static final int PAGE_SIZE = 10;

    @PostConstruct
    public void init() {
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCheckExistence(true);
        templateEngine.setTemplateResolver(resolver);

        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                HeadingAnchorExtension.create(),
                ImageAttributesExtension.create(),
                TaskListItemsExtension.create()
        );
        markdownParser = Parser.builder().extensions(extensions).build();
        htmlRenderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    private String convertMarkdownToHtml(String markdown) {
        Node document = markdownParser.parse(markdown);
        return htmlRenderer.render(document);
    }

    public void generateFullSite(Long taskId) {
        StaticGenTaskDO task = taskMapper.selectById(taskId);
        task.setStatus(StaticGenTaskStatusEnum.RUNNING.getCode());
        task.setStartTime(LocalDateTime.now());
        taskMapper.updateById(task);

        try {
            Path output = Paths.get(outputDir);
            if (Files.exists(output)) {
                deleteDirectory(output);
            }
            Files.createDirectories(output);

            // 清除旧的页面记录
            pageMapper.delete(null);

            // 收集数据
            TemplateDataCollector.BlogData blogData = dataCollector.collectBlogSettings();
            List<TemplateDataCollector.ArticleData> articles = dataCollector.collectPublishedArticles();
            List<TemplateDataCollector.CategoryData> categories = dataCollector.collectCategories();
            List<TemplateDataCollector.TagData> tags = dataCollector.collectTags();

            StaticSiteConfigDO config = configMapper.selectById(1L);
            String baseUrl = config != null && config.getOutputBaseUrl() != null ? config.getOutputBaseUrl() : "/";
            if (!baseUrl.endsWith("/")) baseUrl += "/";

            int totalPages = calculateTotalPages(articles, categories, tags);
            task.setTotalPages(totalPages);
            taskMapper.updateById(task);

            int generated = 0;

            // 复制静态资源
            copyAssets(output);

            // 转换 Markdown 为 HTML
            for (TemplateDataCollector.ArticleData article : articles) {
                if (article.getContent() != null) {
                    article.setContent(convertMarkdownToHtml(article.getContent()));
                }
            }

            // 生成首页（分页）
            generated += generateIndexPages(output, articles, categories, tags, blogData, baseUrl, config);
            updateProgress(task, generated);

            // 生成文章页
            generated += generateArticlePages(output, articles, categories, tags, blogData, baseUrl, config);
            updateProgress(task, generated);

            // 生成分类列表页
            generated += generateCategoryListPage(output, categories, tags, blogData, baseUrl);
            updateProgress(task, generated);

            // 生成各分类文章页
            generated += generateCategoryArticlePages(output, articles, categories, tags, blogData, baseUrl, config);
            updateProgress(task, generated);

            // 生成标签列表页
            generated += generateTagListPage(output, categories, tags, blogData, baseUrl);
            updateProgress(task, generated);

            // 生成各标签文章页
            generated += generateTagArticlePages(output, articles, categories, tags, blogData, baseUrl, config);
            updateProgress(task, generated);

            // 生成归档页
            generated += generateArchivePages(output, articles, categories, tags, blogData, baseUrl);
            updateProgress(task, generated);

            // 完成
            task.setStatus(StaticGenTaskStatusEnum.SUCCESS.getCode());
            task.setEndTime(LocalDateTime.now());
            task.setOutputPath(output.toString());
            task.setGeneratedPages(generated);
            task.setDurationMs(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
            taskMapper.updateById(task);

            log.info("静态站点生成完成，共 {} 页，耗时 {}ms", generated, task.getDurationMs());

        } catch (Exception e) {
            log.error("静态站点生成失败", e);
            task.setStatus(StaticGenTaskStatusEnum.FAILED.getCode());
            task.setEndTime(LocalDateTime.now());
            task.setErrorMessage(e.getMessage());
            if (task.getStartTime() != null) {
                task.setDurationMs(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
            }
            taskMapper.updateById(task);
        }
    }

    public void generateIncrementalSite(Long taskId) {
        StaticGenTaskDO task = taskMapper.selectById(taskId);
        task.setStatus(StaticGenTaskStatusEnum.RUNNING.getCode());
        task.setStartTime(LocalDateTime.now());
        taskMapper.updateById(task);

        try {
            IncrementalTracker.ChangeSet changeSet = incrementalTracker.detectChanges();

            if (!incrementalTracker.hasAnyChanges(changeSet)) {
                log.info("没有检测到变更，跳过增量生成");
                task.setStatus(StaticGenTaskStatusEnum.SUCCESS.getCode());
                task.setEndTime(LocalDateTime.now());
                task.setGeneratedPages(0);
                task.setDurationMs(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
                taskMapper.updateById(task);
                return;
            }

            Path output = Paths.get(outputDir);
            Files.createDirectories(output);

            // 收集数据
            TemplateDataCollector.BlogData blogData = dataCollector.collectBlogSettings();
            List<TemplateDataCollector.ArticleData> allArticles = dataCollector.collectPublishedArticles();
            List<TemplateDataCollector.CategoryData> categories = dataCollector.collectCategories();
            List<TemplateDataCollector.TagData> tags = dataCollector.collectTags();

            StaticSiteConfigDO config = configMapper.selectById(1L);
            String baseUrl = config != null && config.getOutputBaseUrl() != null ? config.getOutputBaseUrl() : "/";
            if (!baseUrl.endsWith("/")) baseUrl += "/";

            // 转换所有文章的 Markdown（用于首页/分类/标签列表渲染）
            for (TemplateDataCollector.ArticleData article : allArticles) {
                if (article.getContent() != null) {
                    article.setContent(convertMarkdownToHtml(article.getContent()));
                }
            }

            int generated = 0;

            // 1. 处理删除的文章：删除输出文件和页面记录
            for (Long deletedId : changeSet.getDeletedArticleIds()) {
                Path articleFile = output.resolve("article/" + deletedId + "/index.html");
                if (Files.exists(articleFile)) {
                    Files.delete(articleFile);
                    Path parentDir = articleFile.getParent();
                    if (parentDir != null && isDirectoryEmpty(parentDir)) {
                        Files.delete(parentDir);
                    }
                }
                pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                        .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.ARTICLE.getCode())
                        .eq(StaticGenPageDO::getRefId, deletedId));
            }

            // 2. 生成新增和修改的文章页
            Set<Long> dirtyArticleIds = new HashSet<>();
            dirtyArticleIds.addAll(changeSet.getNewArticleIds());
            dirtyArticleIds.addAll(changeSet.getModifiedArticleIds());

            if (!dirtyArticleIds.isEmpty()) {
                List<TemplateDataCollector.ArticleData> dirtyArticles = allArticles.stream()
                        .filter(a -> dirtyArticleIds.contains(a.getId()))
                        .collect(Collectors.toList());

                for (TemplateDataCollector.ArticleData article : dirtyArticles) {
                    Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
                    variables.put("article", article);

                    // 上一篇/下一篇
                    int idx = allArticles.indexOf(article);
                    if (idx > 0) {
                        Map<String, Object> preArticle = new HashMap<>();
                        preArticle.put("id", allArticles.get(idx - 1).getId());
                        preArticle.put("title", allArticles.get(idx - 1).getTitle());
                        variables.put("preArticle", preArticle);
                    }
                    if (idx < allArticles.size() - 1) {
                        Map<String, Object> nextArticle = new HashMap<>();
                        nextArticle.put("id", allArticles.get(idx + 1).getId());
                        nextArticle.put("title", allArticles.get(idx + 1).getTitle());
                        variables.put("nextArticle", nextArticle);
                    }

                    // 评论
                    if (config != null) {
                        commentRenderer.populateCommentVariables(variables, config.getCommentProvider(), config.getCommentConfig());
                    }

                    String html = templateEngine.process("static/article", new Context(Locale.CHINA, variables));
                    Path filePath = output.resolve("article/" + article.getId() + "/index.html");
                    writeFile(filePath, html);

                    // 更新或插入页面记录
                    pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                            .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.ARTICLE.getCode())
                            .eq(StaticGenPageDO::getRefId, article.getId()));
                    savePageRecord(StaticPageTypeEnum.ARTICLE.getCode(), article.getId(), 1, filePath.toString());
                    generated++;
                }
            }

            // 3. 重建首页（如果有变动，首页一定脏）
            if (changeSet.isIndexDirty()) {
                // 删除旧的首页记录
                pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                        .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.INDEX.getCode()));
                generated += generateIndexPages(output, allArticles, categories, tags, blogData, baseUrl, config);
            }

            // 4. 重建受影响的分类页
            if (!changeSet.getAffectedCategoryIds().isEmpty()) {
                // 删除受影响分类的旧记录
                for (Long categoryId : changeSet.getAffectedCategoryIds()) {
                    pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                            .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.CATEGORY.getCode())
                            .eq(StaticGenPageDO::getRefId, categoryId));
                }
                // 重建分类列表页
                pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                        .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.CATEGORY.getCode())
                        .isNull(StaticGenPageDO::getRefId));
                generated += generateCategoryListPage(output, categories, tags, blogData, baseUrl);

                // 仅重建受影响分类的文章列表
                for (TemplateDataCollector.CategoryData category : categories) {
                    if (changeSet.getAffectedCategoryIds().contains(category.getId())) {
                        generated += generateSingleCategoryArticlePages(output, allArticles, category, categories, tags, blogData, baseUrl, config);
                    }
                }
            }

            // 5. 重建受影响的标签页
            if (!changeSet.getAffectedTagIds().isEmpty()) {
                for (Long tagId : changeSet.getAffectedTagIds()) {
                    pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                            .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.TAG.getCode())
                            .eq(StaticGenPageDO::getRefId, tagId));
                }
                // 重建标签列表页
                pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                        .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.TAG.getCode())
                        .isNull(StaticGenPageDO::getRefId));
                generated += generateTagListPage(output, categories, tags, blogData, baseUrl);

                // 仅重建受影响标签的文章列表
                for (TemplateDataCollector.TagData tag : tags) {
                    if (changeSet.getAffectedTagIds().contains(tag.getId())) {
                        generated += generateSingleTagArticlePages(output, allArticles, tag, categories, tags, blogData, baseUrl, config);
                    }
                }
            }

            // 6. 重建归档页
            if (changeSet.isArchiveDirty()) {
                pageMapper.delete(Wrappers.<StaticGenPageDO>lambdaQuery()
                        .eq(StaticGenPageDO::getPageType, StaticPageTypeEnum.ARCHIVE.getCode()));
                generated += generateArchivePages(output, allArticles, categories, tags, blogData, baseUrl);
            }

            // 完成
            task.setStatus(StaticGenTaskStatusEnum.SUCCESS.getCode());
            task.setEndTime(LocalDateTime.now());
            task.setOutputPath(output.toString());
            task.setGeneratedPages(generated);
            task.setDurationMs(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
            taskMapper.updateById(task);

            log.info("增量生成完成，共重建 {} 页，耗时 {}ms", generated, task.getDurationMs());

        } catch (Exception e) {
            log.error("增量生成失败", e);
            task.setStatus(StaticGenTaskStatusEnum.FAILED.getCode());
            task.setEndTime(LocalDateTime.now());
            task.setErrorMessage(e.getMessage());
            if (task.getStartTime() != null) {
                task.setDurationMs(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
            }
            taskMapper.updateById(task);
        }
    }

    private int generateSingleCategoryArticlePages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                                    TemplateDataCollector.CategoryData category,
                                                    List<TemplateDataCollector.CategoryData> categories,
                                                    List<TemplateDataCollector.TagData> tags,
                                                    TemplateDataCollector.BlogData blogData, String baseUrl,
                                                    StaticSiteConfigDO config) throws IOException {
        int generated = 0;
        List<TemplateDataCollector.ArticleData> catArticles = articles.stream()
                .filter(a -> category.getId().equals(a.getCategoryId()))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) catArticles.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        for (int page = 1; page <= totalPages; page++) {
            int fromIndex = (page - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, catArticles.size());

            Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
            variables.put("categoryName", category.getName());
            variables.put("articles", catArticles.subList(fromIndex, toIndex));
            variables.put("currentPage", page);
            variables.put("totalPages", totalPages);
            variables.put("paginationBaseUrl", baseUrl + "category/" + category.getId() + "/");

            String html = templateEngine.process("static/category-articles", new Context(Locale.CHINA, variables));

            Path filePath;
            if (page == 1) {
                filePath = output.resolve("category/" + category.getId() + "/index.html");
            } else {
                filePath = output.resolve("category/" + category.getId() + "/page/" + page + "/index.html");
            }
            writeFile(filePath, html);
            savePageRecord(StaticPageTypeEnum.CATEGORY.getCode(), category.getId(), page, filePath.toString());
            generated++;
        }
        return generated;
    }

    private int generateSingleTagArticlePages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                               TemplateDataCollector.TagData tag,
                                               List<TemplateDataCollector.CategoryData> categories,
                                               List<TemplateDataCollector.TagData> tags,
                                               TemplateDataCollector.BlogData blogData, String baseUrl,
                                               StaticSiteConfigDO config) throws IOException {
        int generated = 0;
        List<TemplateDataCollector.ArticleData> tagArticles = articles.stream()
                .filter(a -> a.getTags() != null && a.getTags().stream().anyMatch(t -> tag.getId().equals(t.getId())))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) tagArticles.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        for (int page = 1; page <= totalPages; page++) {
            int fromIndex = (page - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, tagArticles.size());

            Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
            variables.put("tagName", tag.getName());
            variables.put("articles", tagArticles.subList(fromIndex, toIndex));
            variables.put("currentPage", page);
            variables.put("totalPages", totalPages);
            variables.put("paginationBaseUrl", baseUrl + "tag/" + tag.getId() + "/");

            String html = templateEngine.process("static/tag-articles", new Context(Locale.CHINA, variables));

            Path filePath;
            if (page == 1) {
                filePath = output.resolve("tag/" + tag.getId() + "/index.html");
            } else {
                filePath = output.resolve("tag/" + tag.getId() + "/page/" + page + "/index.html");
            }
            writeFile(filePath, html);
            savePageRecord(StaticPageTypeEnum.TAG.getCode(), tag.getId(), page, filePath.toString());
            generated++;
        }
        return generated;
    }

    private boolean isDirectoryEmpty(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            return !stream.iterator().hasNext();
        }
    }

    private int generateIndexPages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                   List<TemplateDataCollector.CategoryData> categories,
                                   List<TemplateDataCollector.TagData> tags,
                                   TemplateDataCollector.BlogData blogData, String baseUrl,
                                   StaticSiteConfigDO config) throws IOException {
        int totalArticles = articles.size();
        int totalPages = (int) Math.ceil((double) totalArticles / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        int generated = 0;

        for (int page = 1; page <= totalPages; page++) {
            int fromIndex = (page - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, totalArticles);
            List<TemplateDataCollector.ArticleData> pageArticles = articles.subList(fromIndex, toIndex);

            Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
            variables.put("articles", pageArticles);
            variables.put("currentPage", page);
            variables.put("totalPages", totalPages);
            variables.put("paginationBaseUrl", baseUrl);

            String html = templateEngine.process("static/index", new Context(Locale.CHINA, variables));

            Path filePath;
            if (page == 1) {
                filePath = output.resolve("index.html");
            } else {
                filePath = output.resolve("page/" + page + "/index.html");
            }
            writeFile(filePath, html);

            savePageRecord(StaticPageTypeEnum.INDEX.getCode(), null, page, filePath.toString());
            generated++;
        }
        return generated;
    }

    private int generateArticlePages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                     List<TemplateDataCollector.CategoryData> categories,
                                     List<TemplateDataCollector.TagData> tags,
                                     TemplateDataCollector.BlogData blogData, String baseUrl,
                                     StaticSiteConfigDO config) throws IOException {
        int generated = 0;
        for (int i = 0; i < articles.size(); i++) {
            TemplateDataCollector.ArticleData article = articles.get(i);

            Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
            variables.put("article", article);

            // 上一篇/下一篇
            if (i > 0) {
                Map<String, Object> preArticle = new HashMap<>();
                preArticle.put("id", articles.get(i - 1).getId());
                preArticle.put("title", articles.get(i - 1).getTitle());
                variables.put("preArticle", preArticle);
            }
            if (i < articles.size() - 1) {
                Map<String, Object> nextArticle = new HashMap<>();
                nextArticle.put("id", articles.get(i + 1).getId());
                nextArticle.put("title", articles.get(i + 1).getTitle());
                variables.put("nextArticle", nextArticle);
            }

            // 评论
            if (config != null) {
                commentRenderer.populateCommentVariables(variables, config.getCommentProvider(), config.getCommentConfig());
            }

            String html = templateEngine.process("static/article", new Context(Locale.CHINA, variables));

            Path filePath = output.resolve("article/" + article.getId() + "/index.html");
            writeFile(filePath, html);

            savePageRecord(StaticPageTypeEnum.ARTICLE.getCode(), article.getId(), 1, filePath.toString());
            generated++;
        }
        return generated;
    }

    private int generateCategoryListPage(Path output, List<TemplateDataCollector.CategoryData> categories,
                                         List<TemplateDataCollector.TagData> tags,
                                         TemplateDataCollector.BlogData blogData, String baseUrl) throws IOException {
        Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
        String html = templateEngine.process("static/category-list", new Context(Locale.CHINA, variables));
        Path filePath = output.resolve("category/index.html");
        writeFile(filePath, html);
        savePageRecord(StaticPageTypeEnum.CATEGORY.getCode(), null, 1, filePath.toString());
        return 1;
    }

    private int generateCategoryArticlePages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                             List<TemplateDataCollector.CategoryData> categories,
                                             List<TemplateDataCollector.TagData> tags,
                                             TemplateDataCollector.BlogData blogData, String baseUrl,
                                             StaticSiteConfigDO config) throws IOException {
        int generated = 0;
        for (TemplateDataCollector.CategoryData category : categories) {
            List<TemplateDataCollector.ArticleData> catArticles = articles.stream()
                    .filter(a -> category.getId().equals(a.getCategoryId()))
                    .collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) catArticles.size() / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;

            for (int page = 1; page <= totalPages; page++) {
                int fromIndex = (page - 1) * PAGE_SIZE;
                int toIndex = Math.min(fromIndex + PAGE_SIZE, catArticles.size());

                Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
                variables.put("categoryName", category.getName());
                variables.put("articles", catArticles.subList(fromIndex, toIndex));
                variables.put("currentPage", page);
                variables.put("totalPages", totalPages);
                variables.put("paginationBaseUrl", baseUrl + "category/" + category.getId() + "/");

                String html = templateEngine.process("static/category-articles", new Context(Locale.CHINA, variables));

                Path filePath;
                if (page == 1) {
                    filePath = output.resolve("category/" + category.getId() + "/index.html");
                } else {
                    filePath = output.resolve("category/" + category.getId() + "/page/" + page + "/index.html");
                }
                writeFile(filePath, html);
                savePageRecord(StaticPageTypeEnum.CATEGORY.getCode(), category.getId(), page, filePath.toString());
                generated++;
            }
        }
        return generated;
    }

    private int generateTagListPage(Path output, List<TemplateDataCollector.CategoryData> categories,
                                    List<TemplateDataCollector.TagData> tags,
                                    TemplateDataCollector.BlogData blogData, String baseUrl) throws IOException {
        Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
        String html = templateEngine.process("static/tag-list", new Context(Locale.CHINA, variables));
        Path filePath = output.resolve("tag/index.html");
        writeFile(filePath, html);
        savePageRecord(StaticPageTypeEnum.TAG.getCode(), null, 1, filePath.toString());
        return 1;
    }

    private int generateTagArticlePages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                        List<TemplateDataCollector.CategoryData> categories,
                                        List<TemplateDataCollector.TagData> tags,
                                        TemplateDataCollector.BlogData blogData, String baseUrl,
                                        StaticSiteConfigDO config) throws IOException {
        int generated = 0;
        for (TemplateDataCollector.TagData tag : tags) {
            List<TemplateDataCollector.ArticleData> tagArticles = articles.stream()
                    .filter(a -> a.getTags() != null && a.getTags().stream().anyMatch(t -> tag.getId().equals(t.getId())))
                    .collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) tagArticles.size() / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;

            for (int page = 1; page <= totalPages; page++) {
                int fromIndex = (page - 1) * PAGE_SIZE;
                int toIndex = Math.min(fromIndex + PAGE_SIZE, tagArticles.size());

                Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
                variables.put("tagName", tag.getName());
                variables.put("articles", tagArticles.subList(fromIndex, toIndex));
                variables.put("currentPage", page);
                variables.put("totalPages", totalPages);
                variables.put("paginationBaseUrl", baseUrl + "tag/" + tag.getId() + "/");

                String html = templateEngine.process("static/tag-articles", new Context(Locale.CHINA, variables));

                Path filePath;
                if (page == 1) {
                    filePath = output.resolve("tag/" + tag.getId() + "/index.html");
                } else {
                    filePath = output.resolve("tag/" + tag.getId() + "/page/" + page + "/index.html");
                }
                writeFile(filePath, html);
                savePageRecord(StaticPageTypeEnum.TAG.getCode(), tag.getId(), page, filePath.toString());
                generated++;
            }
        }
        return generated;
    }

    private int generateArchivePages(Path output, List<TemplateDataCollector.ArticleData> articles,
                                     List<TemplateDataCollector.CategoryData> categories,
                                     List<TemplateDataCollector.TagData> tags,
                                     TemplateDataCollector.BlogData blogData, String baseUrl) throws IOException {
        // 按月分组
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy年M月");
        Map<String, List<TemplateDataCollector.ArticleData>> groupedByMonth = new LinkedHashMap<>();
        for (TemplateDataCollector.ArticleData article : articles) {
            String month = article.getCreateTime().format(monthFormatter);
            groupedByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(article);
        }

        List<Map<String, Object>> archiveGroups = new ArrayList<>();
        for (Map.Entry<String, List<TemplateDataCollector.ArticleData>> entry : groupedByMonth.entrySet()) {
            Map<String, Object> group = new HashMap<>();
            group.put("month", entry.getKey());
            group.put("articles", entry.getValue());
            archiveGroups.add(group);
        }

        int totalPages = (int) Math.ceil((double) archiveGroups.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        int generated = 0;

        for (int page = 1; page <= totalPages; page++) {
            int fromIndex = (page - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, archiveGroups.size());

            Map<String, Object> variables = buildBaseVariables(blogData, categories, tags, baseUrl);
            variables.put("archiveGroups", archiveGroups.subList(fromIndex, toIndex));
            variables.put("currentPage", page);
            variables.put("totalPages", totalPages);
            variables.put("paginationBaseUrl", baseUrl + "archive/");

            String html = templateEngine.process("static/archive", new Context(Locale.CHINA, variables));

            Path filePath;
            if (page == 1) {
                filePath = output.resolve("archive/index.html");
            } else {
                filePath = output.resolve("archive/page/" + page + "/index.html");
            }
            writeFile(filePath, html);
            savePageRecord(StaticPageTypeEnum.ARCHIVE.getCode(), null, page, filePath.toString());
            generated++;
        }
        return generated;
    }

    private Map<String, Object> buildBaseVariables(TemplateDataCollector.BlogData blogData,
                                                   List<TemplateDataCollector.CategoryData> categories,
                                                   List<TemplateDataCollector.TagData> tags,
                                                   String baseUrl) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("blogName", blogData.getName() != null ? blogData.getName() : "博客");
        variables.put("blogLogo", blogData.getLogo());
        variables.put("blogAuthor", blogData.getAuthor());
        variables.put("blogAvatar", blogData.getAvatar());
        variables.put("blogIntroduction", blogData.getIntroduction());
        variables.put("baseUrl", baseUrl);
        variables.put("categories", categories);
        variables.put("tags", tags);
        return variables;
    }

    private int calculateTotalPages(List<TemplateDataCollector.ArticleData> articles,
                                    List<TemplateDataCollector.CategoryData> categories,
                                    List<TemplateDataCollector.TagData> tags) {
        int total = 0;
        // 首页分页
        total += (int) Math.ceil((double) articles.size() / PAGE_SIZE);
        // 文章页
        total += articles.size();
        // 分类列表
        total += 1;
        // 各分类页 (简化估算)
        total += categories.size();
        // 标签列表
        total += 1;
        // 各标签页
        total += tags.size();
        // 归档页
        total += 1;
        return Math.max(total, 1);
    }

    private void writeFile(Path filePath, String content) throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
    }

    private void copyAssets(Path output) throws IOException {
        Path assetsDir = output.resolve("assets/css");
        Files.createDirectories(assetsDir);

        String articleCss = loadArticleCss();
        Files.write(assetsDir.resolve("article.css"), articleCss.getBytes(StandardCharsets.UTF_8));
    }

    private String loadArticleCss() {
        return "/* 文章内容样式 */\n" +
                ".article-content h1,.article-content h2,.article-content h3,.article-content h4,.article-content h5,.article-content h6{color:#292525;line-height:150%;font-family:PingFang SC,Helvetica Neue,Helvetica,Hiragino Sans GB,Microsoft YaHei,Arial,sans-serif}\n" +
                ".article-content h2{line-height:1.5;font-weight:700;font-size:24px;margin-top:40px;margin-bottom:26px;border-bottom:1px solid rgb(241 245 249);padding-bottom:15px}\n" +
                ".article-content h3{font-size:20px;margin-top:40px;margin-bottom:16px;font-weight:600}\n" +
                ".article-content h4{font-size:18px;margin-top:30px;margin-bottom:16px;font-weight:600}\n" +
                ".article-content h5,.article-content h6{font-size:16px;margin-top:30px;margin-bottom:14px;font-weight:600}\n" +
                ".article-content p{letter-spacing:.3px;margin:0 0 20px;line-height:30px;color:#4c4e4d;font-weight:400;word-break:normal;word-wrap:break-word;font-family:-apple-system,BlinkMacSystemFont,PingFang SC,Hiragino Sans GB,Microsoft Yahei,Arial,sans-serif}\n" +
                ".article-content blockquote{border-left:2.3px solid rgb(52,152,219);quotes:none;background:rgb(236,240,241);color:#777;font-size:16px;margin:2em 0;padding:24px}\n" +
                ".article-content blockquote p:last-child{margin-bottom:0}\n" +
                ".article-content em{color:#c849ff}\n" +
                ".article-content a{color:#167bc2}\n" +
                ".article-content a:hover{text-decoration:underline}\n" +
                ".article-content ul{padding-left:2rem}\n" +
                ".article-content ul li{list-style-type:disc;padding-top:5px;padding-bottom:5px;font-size:16px}\n" +
                ".article-content ol{list-style-type:decimal;padding-left:2rem}\n" +
                ".article-content img{max-width:100%;overflow:hidden;display:block;margin:0 auto;border-radius:8px}\n" +
                ".article-content img:hover{box-shadow:2px 2px 10px 0 rgba(0,0,0,.15)}\n" +
                ".article-content code:not(pre code){padding:2px 4px;margin:0 2px;font-size:95%!important;border-radius:4px;color:rgb(41,128,185);background-color:rgba(27,31,35,.05);font-family:Operator Mono,Consolas,Monaco,Menlo,monospace}\n" +
                "pre code.hljs{padding-top:2rem;padding-left:.5rem;padding-right:.5rem;border-radius:6px}\n" +
                "pre{position:relative}\n" +
                "pre:before{background:#fc625d;border-radius:50%;box-shadow:20px 0 #fdbc40,40px 0 #35cd4b;content:' ';height:10px;margin-top:10px;margin-left:10px;position:absolute;width:10px}\n";
    }

    private void savePageRecord(String pageType, Long refId, int pageNumber, String outputPath) {
        StaticGenPageDO page = new StaticGenPageDO();
        page.setPageType(pageType);
        page.setRefId(refId);
        page.setPageNumber(pageNumber);
        page.setOutputPath(outputPath);
        page.setLastGeneratedTime(LocalDateTime.now());
        pageMapper.insert(page);
    }

    private void updateProgress(StaticGenTaskDO task, int generated) {
        task.setGeneratedPages(generated);
        taskMapper.updateById(task);
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.warn("删除文件失败: {}", path, e);
                    }
                });
    }

    public String getOutputDir() {
        return outputDir;
    }
}
