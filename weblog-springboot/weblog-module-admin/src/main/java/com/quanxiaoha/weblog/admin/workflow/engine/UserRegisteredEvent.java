package com.quanxiaoha.weblog.admin.workflow.engine;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private Long userId;
    private String username;
    private String email;

    public UserRegisteredEvent(Object source, Long userId, String username, String email) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
