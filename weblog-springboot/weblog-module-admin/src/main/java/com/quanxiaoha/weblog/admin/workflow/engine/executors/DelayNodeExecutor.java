package com.quanxiaoha.weblog.admin.workflow.engine.executors;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutionResult;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutor;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import org.springframework.stereotype.Component;

@Component
public class DelayNodeExecutor implements NodeExecutor {

    @Override
    public NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context) {
        if (context.isDebugMode()) {
            return NodeExecutionResult.success("调试模式：跳过延时等待");
        }
        return NodeExecutionResult.pause();
    }

    public int getDelayMinutes(JSONObject nodeData) {
        return nodeData.getIntValue("delayMinutes", 0);
    }

    @Override
    public String getNodeType() {
        return "delay";
    }
}
