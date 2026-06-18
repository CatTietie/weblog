package com.quanxiaoha.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StaticSiteGenerateEvent extends ApplicationEvent {

    private final String taskType;

    private final Boolean autoDeploy;

    public StaticSiteGenerateEvent(Object source, String taskType, Boolean autoDeploy) {
        super(source);
        this.taskType = taskType;
        this.autoDeploy = autoDeploy;
    }
}
