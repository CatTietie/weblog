package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.alibaba.fastjson2.JSON;
import com.quanxiaoha.weblog.admin.model.vo.workflow.*;
import com.quanxiaoha.weblog.admin.service.AdminWorkflowService;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowContext;
import com.quanxiaoha.weblog.admin.workflow.engine.WorkflowEngine;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionDO;
import com.quanxiaoha.weblog.common.domain.dos.WorkflowExecutionLogDO;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowExecutionLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowExecutionMapper;
import com.quanxiaoha.weblog.common.domain.mapper.WorkflowMapper;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminWorkflowServiceImpl implements AdminWorkflowService {

    @Autowired
    private WorkflowMapper workflowMapper;

    @Autowired
    private WorkflowExecutionMapper executionMapper;

    @Autowired
    private WorkflowExecutionLogMapper executionLogMapper;

    @Autowired
    private WorkflowEngine workflowEngine;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createWorkflow(CreateWorkflowReqVO vo) {
        WorkflowDO workflow = WorkflowDO.builder()
                .name(vo.getName())
                .description(vo.getDescription())
                .triggerType(vo.getTriggerType())
                .definitionJson(vo.getDefinitionJson())
                .isEnabled(true)
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        workflowMapper.insert(workflow);
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateWorkflow(UpdateWorkflowReqVO vo) {
        WorkflowDO workflow = workflowMapper.selectById(vo.getId());
        if (workflow == null || workflow.getIsDeleted()) {
            throw new BizException("工作流不存在");
        }
        workflow.setName(vo.getName());
        workflow.setDescription(vo.getDescription());
        workflow.setTriggerType(vo.getTriggerType());
        workflow.setDefinitionJson(vo.getDefinitionJson());
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteWorkflow(Long id) {
        WorkflowDO workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            throw new BizException("工作流不存在");
        }
        workflow.setIsDeleted(true);
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        return Response.success();
    }

    @Override
    public Response getWorkflowDetail(Long id) {
        WorkflowDO workflow = workflowMapper.selectById(id);
        if (workflow == null || workflow.getIsDeleted()) {
            throw new BizException("工作流不存在");
        }
        WorkflowRspVO rsp = WorkflowRspVO.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .triggerType(workflow.getTriggerType())
                .definitionJson(workflow.getDefinitionJson())
                .isEnabled(workflow.getIsEnabled())
                .createTime(workflow.getCreateTime())
                .updateTime(workflow.getUpdateTime())
                .build();
        return Response.success(rsp);
    }

    @Override
    public PageResponse findWorkflowPageList(FindWorkflowPageListReqVO vo) {
        Page<WorkflowDO> page = new Page<>(vo.getCurrent(), vo.getSize());
        Page<WorkflowDO> result = workflowMapper.selectPage(page, Wrappers.<WorkflowDO>lambdaQuery()
                .eq(WorkflowDO::getIsDeleted, false)
                .like(StringUtils.isNotBlank(vo.getName()), WorkflowDO::getName, vo.getName())
                .eq(StringUtils.isNotBlank(vo.getTriggerType()), WorkflowDO::getTriggerType, vo.getTriggerType())
                .orderByDesc(WorkflowDO::getCreateTime));

        List<WorkflowRspVO> vos = result.getRecords().stream().map(w -> WorkflowRspVO.builder()
                .id(w.getId())
                .name(w.getName())
                .description(w.getDescription())
                .triggerType(w.getTriggerType())
                .isEnabled(w.getIsEnabled())
                .createTime(w.getCreateTime())
                .updateTime(w.getUpdateTime())
                .build()).collect(Collectors.toList());

        return PageResponse.success(result, vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response toggleWorkflow(Long id) {
        WorkflowDO workflow = workflowMapper.selectById(id);
        if (workflow == null || workflow.getIsDeleted()) {
            throw new BizException("工作流不存在");
        }
        workflow.setIsEnabled(!workflow.getIsEnabled());
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        return Response.success();
    }

    @Override
    public Response debugWorkflow(DebugWorkflowReqVO vo) {
        WorkflowDO workflow = workflowMapper.selectById(vo.getId());
        if (workflow == null || workflow.getIsDeleted()) {
            throw new BizException("工作流不存在");
        }

        Map<String, Object> variables = JSON.parseObject(vo.getTriggerData(), Map.class);

        WorkflowExecutionDO execution = WorkflowExecutionDO.builder()
                .workflowId(workflow.getId())
                .status("RUNNING")
                .triggerData(vo.getTriggerData())
                .isDebug(true)
                .startTime(LocalDateTime.now())
                .build();
        executionMapper.insert(execution);

        WorkflowContext context = WorkflowContext.builder()
                .variables(new HashMap<>(variables))
                .debugMode(true)
                .build();
        workflowEngine.execute(execution, workflow, context);

        List<WorkflowExecutionLogDO> logs = executionLogMapper.selectByExecutionId(execution.getId());
        List<WorkflowExecutionLogRspVO> logVos = logs.stream().map(l -> WorkflowExecutionLogRspVO.builder()
                .id(l.getId())
                .executionId(l.getExecutionId())
                .nodeId(l.getNodeId())
                .nodeType(l.getNodeType())
                .nodeLabel(l.getNodeLabel())
                .status(l.getStatus())
                .inputData(l.getInputData())
                .outputData(l.getOutputData())
                .errorMessage(l.getErrorMessage())
                .executeTime(l.getExecuteTime())
                .durationMs(l.getDurationMs())
                .build()).collect(Collectors.toList());

        return Response.success(logVos);
    }

    @Override
    public PageResponse findExecutionPageList(Long workflowId, Long current, Long size) {
        Page<WorkflowExecutionDO> result = executionMapper.selectPageByWorkflowId(current, size, workflowId);

        List<WorkflowExecutionRspVO> vos = result.getRecords().stream().map(e -> WorkflowExecutionRspVO.builder()
                .id(e.getId())
                .workflowId(e.getWorkflowId())
                .status(e.getStatus())
                .triggerData(e.getTriggerData())
                .currentNodeId(e.getCurrentNodeId())
                .errorMessage(e.getErrorMessage())
                .isDebug(e.getIsDebug())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .build()).collect(Collectors.toList());

        return PageResponse.success(result, vos);
    }

    @Override
    public Response findExecutionLogs(Long executionId) {
        List<WorkflowExecutionLogDO> logs = executionLogMapper.selectByExecutionId(executionId);

        List<WorkflowExecutionLogRspVO> vos = logs.stream().map(l -> WorkflowExecutionLogRspVO.builder()
                .id(l.getId())
                .executionId(l.getExecutionId())
                .nodeId(l.getNodeId())
                .nodeType(l.getNodeType())
                .nodeLabel(l.getNodeLabel())
                .status(l.getStatus())
                .inputData(l.getInputData())
                .outputData(l.getOutputData())
                .errorMessage(l.getErrorMessage())
                .executeTime(l.getExecuteTime())
                .durationMs(l.getDurationMs())
                .build()).collect(Collectors.toList());

        return Response.success(vos);
    }
}
