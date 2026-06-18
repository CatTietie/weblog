package com.quanxiaoha.weblog.admin.workflow.engine.executors;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutionResult;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutor;
import com.quanxiaoha.weblog.admin.workflow.engine.TemplateResolver;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class WebhookActionExecutor implements NodeExecutor {

    @Autowired
    private TemplateResolver templateResolver;

    @Autowired
    private RestTemplate workflowRestTemplate;

    @Override
    public NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context) {
        String url = templateResolver.resolve(nodeData.getString("url"), context);
        String bodyTemplate = nodeData.getString("body");
        String body = bodyTemplate != null ? templateResolver.resolve(bodyTemplate, context)
                : JSONObject.toJSONString(context.getVariables());

        if (context.isDebugMode()) {
            String output = String.format("调试模式：将POST到 %s, Body: %s", url, body);
            return NodeExecutionResult.success(output);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = workflowRestTemplate.postForEntity(url, entity, String.class);
            return NodeExecutionResult.success("Webhook响应: " + response.getStatusCodeValue());
        } catch (Exception e) {
            log.error("工作流Webhook调用失败, url: {}", url, e);
            return NodeExecutionResult.fail("Webhook调用失败: " + e.getMessage());
        }
    }

    @Override
    public String getNodeType() {
        return "action_webhook";
    }
}
