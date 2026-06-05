package com.quanxiaoha.weblog.admin.workflow.engine;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.workflow.engine.executors.DelayNodeExecutor;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionLogDO;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowExecutionLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowExecutionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WorkflowEngine {

    @Autowired
    private List<NodeExecutor> nodeExecutorList;

    @Autowired
    private WorkflowExecutionMapper executionMapper;

    @Autowired
    private WorkflowExecutionLogMapper logMapper;

    private Map<String, NodeExecutor> executorMap;

    @PostConstruct
    public void init() {
        executorMap = nodeExecutorList.stream()
                .collect(Collectors.toMap(NodeExecutor::getNodeType, e -> e));
    }

    public void execute(WorkflowExecutionDO execution, WorkflowDO workflow, WorkflowContext context) {
        try {
            JSONObject definition = JSON.parseObject(workflow.getDefinitionJson());
            JSONArray nodesArray = definition.getJSONArray("nodes");
            JSONArray edgesArray = definition.getJSONArray("edges");

            Map<String, JSONObject> nodeMap = new HashMap<>();
            for (int i = 0; i < nodesArray.size(); i++) {
                JSONObject node = nodesArray.getJSONObject(i);
                nodeMap.put(node.getString("id"), node);
            }

            Map<String, List<JSONObject>> outgoingEdges = new HashMap<>();
            for (int i = 0; i < edgesArray.size(); i++) {
                JSONObject edge = edgesArray.getJSONObject(i);
                outgoingEdges.computeIfAbsent(edge.getString("source"), k -> new ArrayList<>()).add(edge);
            }

            String startNodeId = execution.getCurrentNodeId();
            if (startNodeId == null) {
                startNodeId = findTriggerNodeId(nodesArray);
            }

            executeFromNode(startNodeId, nodeMap, outgoingEdges, execution, context);
        } catch (Exception e) {
            log.error("工作流执行异常, workflowId: {}, executionId: {}", workflow.getId(), execution.getId(), e);
            execution.setStatus("FAILED");
            execution.setErrorMessage(e.getMessage());
            execution.setEndTime(LocalDateTime.now());
            executionMapper.updateById(execution);
        }
    }

    private void executeFromNode(String nodeId, Map<String, JSONObject> nodeMap,
                                 Map<String, List<JSONObject>> outgoingEdges,
                                 WorkflowExecutionDO execution, WorkflowContext context) {
        String currentNodeId = nodeId;

        while (currentNodeId != null) {
            JSONObject node = nodeMap.get(currentNodeId);
            if (node == null) {
                break;
            }

            String nodeType = node.getString("type");
            JSONObject nodeData = node.getJSONObject("data");
            if (nodeData == null) {
                nodeData = new JSONObject();
            }
            String nodeLabel = nodeData.getString("label");

            NodeExecutor executor = executorMap.get(nodeType);
            if (executor == null) {
                log.warn("未知的节点类型: {}", nodeType);
                break;
            }

            long startMs = System.currentTimeMillis();
            NodeExecutionResult result = executor.execute(nodeData, context);
            long durationMs = System.currentTimeMillis() - startMs;

            WorkflowExecutionLogDO logEntry = WorkflowExecutionLogDO.builder()
                    .executionId(execution.getId())
                    .nodeId(currentNodeId)
                    .nodeType(nodeType)
                    .nodeLabel(nodeLabel)
                    .status(result.getStatus())
                    .inputData(JSON.toJSONString(context.getVariables()))
                    .outputData(result.getOutputData())
                    .errorMessage(result.getErrorMessage())
                    .executeTime(LocalDateTime.now())
                    .durationMs(durationMs)
                    .build();
            logMapper.insert(logEntry);

            if ("FAILED".equals(result.getStatus())) {
                execution.setStatus("FAILED");
                execution.setErrorMessage(result.getErrorMessage());
                execution.setCurrentNodeId(currentNodeId);
                execution.setEndTime(LocalDateTime.now());
                executionMapper.updateById(execution);
                return;
            }

            if (result.isShouldPause()) {
                int delayMinutes = ((DelayNodeExecutor) executor).getDelayMinutes(nodeData);
                String nextNodeId = findNextNode(currentNodeId, null, outgoingEdges);
                execution.setStatus("WAITING_DELAY");
                execution.setCurrentNodeId(nextNodeId);
                execution.setDelayUntil(LocalDateTime.now().plusMinutes(delayMinutes));
                execution.setContextJson(JSON.toJSONString(context.getVariables()));
                executionMapper.updateById(execution);
                return;
            }

            String branchResult = result.getBranchResult();
            currentNodeId = findNextNode(currentNodeId, branchResult, outgoingEdges);

            execution.setCurrentNodeId(currentNodeId);
            executionMapper.updateById(execution);
        }

        execution.setStatus("COMPLETED");
        execution.setEndTime(LocalDateTime.now());
        execution.setCurrentNodeId(null);
        executionMapper.updateById(execution);
    }

    private String findNextNode(String sourceId, String branchResult, Map<String, List<JSONObject>> outgoingEdges) {
        List<JSONObject> edges = outgoingEdges.get(sourceId);
        if (edges == null || edges.isEmpty()) {
            return null;
        }
        if (branchResult != null) {
            for (JSONObject edge : edges) {
                String sourceHandle = edge.getString("sourceHandle");
                if (branchResult.equals(sourceHandle)) {
                    return edge.getString("target");
                }
            }
            return null;
        }
        return edges.get(0).getString("target");
    }

    private String findTriggerNodeId(JSONArray nodesArray) {
        for (int i = 0; i < nodesArray.size(); i++) {
            JSONObject node = nodesArray.getJSONObject(i);
            if ("trigger".equals(node.getString("type"))) {
                return node.getString("id");
            }
        }
        return nodesArray.getJSONObject(0).getString("id");
    }
}
