package com.quanxiaoha.weblog.admin.workflow.engine;

import com.alibaba.fastjson2.JSON;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionDO;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowExecutionMapper;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WorkflowDelayScheduledTask {

    @Autowired
    private WorkflowExecutionMapper executionMapper;

    @Autowired
    private WorkflowMapper workflowMapper;

    @Autowired
    private WorkflowEngine engine;

    @Scheduled(cron = "0 * * * * ?")
    public void pollDelayedExecutions() {
        LocalDateTime now = LocalDateTime.now();
        List<WorkflowExecutionDO> readyExecutions = executionMapper.selectReadyDelayExecutions(now);
        if (readyExecutions.isEmpty()) {
            return;
        }

        log.info("==> 工作流延时轮询: 发现 {} 个待恢复执行", readyExecutions.size());

        for (WorkflowExecutionDO exec : readyExecutions) {
            try {
                WorkflowDO workflow = workflowMapper.selectById(exec.getWorkflowId());
                if (workflow == null || workflow.getIsDeleted() || !workflow.getIsEnabled()) {
                    exec.setStatus("FAILED");
                    exec.setErrorMessage("工作流已删除或禁用");
                    exec.setEndTime(LocalDateTime.now());
                    executionMapper.updateById(exec);
                    continue;
                }

                exec.setStatus("RUNNING");
                executionMapper.updateById(exec);

                Map<String, Object> variables = new HashMap<>();
                if (exec.getContextJson() != null) {
                    variables = JSON.parseObject(exec.getContextJson(), Map.class);
                }

                WorkflowContext context = WorkflowContext.builder()
                        .variables(variables)
                        .debugMode(false)
                        .build();

                engine.execute(exec, workflow, context);
            } catch (Exception e) {
                log.error("工作流延时恢复失败, executionId: {}", exec.getId(), e);
                exec.setStatus("FAILED");
                exec.setErrorMessage("延时恢复失败: " + e.getMessage());
                exec.setEndTime(LocalDateTime.now());
                executionMapper.updateById(exec);
            }
        }
    }
}
