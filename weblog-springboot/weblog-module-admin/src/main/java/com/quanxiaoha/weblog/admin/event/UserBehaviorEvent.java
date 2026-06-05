package com.quanxiaoha.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class UserBehaviorEvent extends ApplicationEvent {

    private Long userId;
    private List<BehaviorItem> events;

    public UserBehaviorEvent(Object source, Long userId, List<BehaviorItem> events) {
        super(source);
        this.userId = userId;
        this.events = events;
    }

    @Getter
    public static class BehaviorItem {
        private Integer eventType;
        private Long articleId;
        private Long tagId;
        private String keyword;
        private Integer durationSeconds;

        public BehaviorItem(Integer eventType, Long articleId, Long tagId, String keyword, Integer durationSeconds) {
            this.eventType = eventType;
            this.articleId = articleId;
            this.tagId = tagId;
            this.keyword = keyword;
            this.durationSeconds = durationSeconds;
        }
    }
}
