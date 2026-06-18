package com.quanxiaoha.weblog.admin.workflow.engine;

import com.alibaba.fastjson2.JSONObject;

public interface NodeExecutor {

    NodeExecutionResult execute(JSONObject nodeData, WorkflowContext context);

    String getNodeType();
}
