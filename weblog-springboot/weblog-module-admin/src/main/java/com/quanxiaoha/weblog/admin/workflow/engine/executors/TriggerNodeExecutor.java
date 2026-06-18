package com.quanxiaoha.weblog.admin.workflow.engine.executors;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutionResult;
import com.quanxiaoha.weblog.admin.workflow.engine.NodeExecutor;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import org.springframework.stereotype.Component;

@Component
public class TriggerNodeExecutor implements NodeExecutor {

    @Override
    public NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context) {
        return NodeExecutionResult.success("触发器执行完成");
    }

    @Override
    public String getNodeType() {
        return "trigger";
    }
}
