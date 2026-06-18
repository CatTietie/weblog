package com.quanxiaoha.weblog.admin.workflow.engine.executors;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutionResult;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutor;
import com.quanxiaoha.weblog.admin.workflow.engine.TemplateResolver;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import com.quanxiaoha.weblog.common.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailActionExecutor implements NodeExecutor {

    @Autowired
    private MailService mailService;

    @Autowired
    private TemplateResolver templateResolver;

    @Override
    public NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context) {
        String toEmail = templateResolver.resolve(nodeData.getString("toEmail"), context);
        String subject = templateResolver.resolve(nodeData.getString("subject"), context);
        String body = templateResolver.resolve(nodeData.getString("body"), context);

        if (context.isDebugMode()) {
            String output = String.format("调试模式：将发送邮件到 %s, 主题: %s", toEmail, subject);
            return NodeExecutionResult.success(output);
        }

        try {
            mailService.sendHtmlMail(toEmail, subject, body);
            return NodeExecutionResult.success("邮件发送成功: " + toEmail);
        } catch (Exception e) {
            log.error("工作流邮件发送失败", e);
            return NodeExecutionResult.fail("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getNodeType() {
        return "action_email";
    }
}
