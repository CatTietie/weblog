package com.quanxiaoha.weblog.admin.workflow.engine.executors;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutionResult;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutor;
import com.quanxiaoha.weblog.admin.workflow.engine.TemplateResolver;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import com.quanxiaoha.weblog.common.domain.dos.NotificationDO;
import com.quanxiaoha.weblog.common.domain.mapper.NotificationMapper;
import com.quanxiaoha.weblog.common.event.NotificationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class NotificationActionExecutor implements NodeExecutor {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private TemplateResolver templateResolver;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context) {
        String title = templateResolver.resolve(nodeData.getString("title"), context);
        String content = templateResolver.resolve(nodeData.getString("content"), context);
        Long receiverId = nodeData.getLong("receiverId");

        if (receiverId == null) {
            Object userIdVar = context.getVariable("userId");
            if (userIdVar != null) {
                receiverId = Long.parseLong(userIdVar.toString());
            }
        }

        if (context.isDebugMode()) {
            String output = String.format("调试模式：将发送通知给用户%d, 标题: %s", receiverId, title);
            return NodeExecutionResult.success(output);
        }

        if (receiverId == null) {
            return NodeExecutionResult.fail("通知接收人ID为空");
        }

        try {
            NotificationDO notification = NotificationDO.builder()
                    .receiverId(receiverId)
                    .type(3)
                    .title(title)
                    .content(content)
                    .isRead(false)
                    .createTime(LocalDateTime.now())
                    .build();
            notificationMapper.insert(notification);

            eventPublisher.publishEvent(new NotificationCreatedEvent(
                    this, receiverId, notification.getId(), 3, title, content, null, null, LocalDateTime.now()));

            return NodeExecutionResult.success("站内通知发送成功: " + title);
        } catch (Exception e) {
            log.error("工作流站内通知发送失败", e);
            return NodeExecutionResult.fail("通知发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getNodeType() {
        return "action_notification";
    }
}
