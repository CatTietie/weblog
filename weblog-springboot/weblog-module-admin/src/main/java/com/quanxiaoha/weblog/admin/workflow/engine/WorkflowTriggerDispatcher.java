package com.quanxiaoha.weblog.admin.workflow.engine;

import com.alibaba.fastjson2.JSON;
import com.quanxiaoha.weblog.admin.event.PublishCommentEvent;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowExecutionMapper;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WorkflowTriggerDispatcher {

    @Autowired
    private WorkflowMapper workflowMapper;

    @Autowired
    private WorkflowExecutionMapper executionMapper;

    @Autowired
    private WorkflowEngine engine;

    @Autowired
    private WorkflowConfig workflowConfig;

    @Autowired
    private ArticleMapper articleMapper;

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onArticlePublished(ArticlePublishedEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("articleId", event.getArticleId());
        variables.put("title", event.getTitle());
        variables.put("categoryId", event.getCategoryId());
        variables.put("category", event.getCategoryName());
        variables.put("authorId", event.getAuthorId());

        // 拼接文章访问URL（前端 hash 路由格式）
        String articleUrl = workflowConfig.getSiteUrl() + "/#/article/" + event.getArticleId();
        variables.put("articleUrl", articleUrl);

        // 注入当前阅读量
        ArticleDO article = articleMapper.selectById(event.getArticleId());
        if (article != null && article.getReadNum() != null) {
            variables.put("readNum", article.getReadNum());
        } else {
            variables.put("readNum", 0L);
        }

        dispatch("ARTICLE_PUBLISHED", variables);
    }

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onCommentReceived(PublishCommentEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("commentId", event.getCommentId());
        variables.put("articleId", event.getArticleId());
        variables.put("userId", event.getCommentUserId());
        variables.put("content", event.getContent());

        // 拼接文章访问URL
        String articleUrl = workflowConfig.getSiteUrl() + "/#/article/" + event.getArticleId();
        variables.put("articleUrl", articleUrl);

        // 注入文章阅读量
        ArticleDO article = articleMapper.selectById(event.getArticleId());
        if (article != null) {
            variables.put("readNum", article.getReadNum() != null ? article.getReadNum() : 0L);
            variables.put("title", article.getTitle());
        }

        dispatch("COMMENT_RECEIVED", variables);
    }

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userId", event.getUserId());
        variables.put("username", event.getUsername());
        variables.put("email", event.getEmail());
        dispatch("USER_REGISTERED", variables);
    }

    private void dispatch(String triggerType, Map<String, Object> variables) {
        List<WorkflowDO> workflows = workflowMapper.selectEnabledByTriggerType(triggerType);
        if (workflows.isEmpty()) {
            return;
        }

        for (WorkflowDO workflow : workflows) {
            try {
                WorkflowExecutionDO execution = WorkflowExecutionDO.builder()
                        .workflowId(workflow.getId())
                        .status("RUNNING")
                        .triggerData(JSON.toJSONString(variables))
                        .isDebug(false)
                        .startTime(LocalDateTime.now())
                        .build();
                executionMapper.insert(execution);

                WorkflowContext context = WorkflowContext.builder()
                        .variables(new HashMap<>(variables))
                        .debugMode(false)
                        .build();
                engine.execute(execution, workflow, context);
            } catch (Exception e) {
                log.error("工作流触发执行失败, workflowId: {}, triggerType: {}", workflow.getId(), triggerType, e);
            }
        }
    }
}
