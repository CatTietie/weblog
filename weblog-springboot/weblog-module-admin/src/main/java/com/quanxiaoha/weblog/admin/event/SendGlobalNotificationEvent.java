package com.quanxiaoha.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendGlobalNotificationEvent extends ApplicationEvent {

    private Long senderId;
    private String title;
    private String content;

    public SendGlobalNotificationEvent(Object source, Long senderId, String title, String content) {
        super(source);
        this.senderId = senderId;
        this.title = title;
        this.content = content;
    }
}
