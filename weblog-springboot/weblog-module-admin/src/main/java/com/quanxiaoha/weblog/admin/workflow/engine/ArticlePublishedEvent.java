package com.quanxiaoha.weblog.admin.workflow.engine;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ArticlePublishedEvent extends ApplicationEvent {

    private Long articleId;
    private String title;
    private Long categoryId;
    private String categoryName;
    private Long authorId;

    public ArticlePublishedEvent(Object source, Long articleId, String title,
                                  Long categoryId, String categoryName, Long authorId) {
        super(source);
        this.articleId = articleId;
        this.title = title;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.authorId = authorId;
    }
}
