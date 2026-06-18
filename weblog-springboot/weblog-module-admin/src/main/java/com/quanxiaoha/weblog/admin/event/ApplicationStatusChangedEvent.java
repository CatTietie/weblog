package com.quanxiaoha.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApplicationStatusChangedEvent extends ApplicationEvent {

    private final String userEmail;
    private final String company;
    private final String position;
    private final Integer oldStatus;
    private final Integer newStatus;

    public ApplicationStatusChangedEvent(Object source, String userEmail, String company, String position,
                                         Integer oldStatus, Integer newStatus) {
        super(source);
        this.userEmail = userEmail;
        this.company = company;
        this.position = position;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
