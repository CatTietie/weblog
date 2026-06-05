package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.workflow.*;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminWorkflowService {

    Response createWorkflow(CreateWorkflowReqVO vo);

    Response updateWorkflow(UpdateWorkflowReqVO vo);

    Response deleteWorkflow(Long id);

    Response getWorkflowDetail(Long id);

    PageResponse findWorkflowPageList(FindWorkflowPageListReqVO vo);

    Response toggleWorkflow(Long id);

    Response debugWorkflow(DebugWorkflowReqVO vo);

    PageResponse findExecutionPageList(Long workflowId, Long current, Long size);

    Response findExecutionLogs(Long executionId);
}
