package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.workflow.*;
import com.quanxiaoha.weblog.admin.service.AdminWorkflowService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/workflow")
@Api(tags = "Admin 工作流模块")
public class AdminWorkflowController {

    @Autowired
    private AdminWorkflowService workflowService;

    @PostMapping("/create")
    @ApiOperation(value = "创建工作流")
    @ApiOperationLog(description = "创建工作流")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response createWorkflow(@RequestBody @Validated CreateWorkflowReqVO vo) {
        return workflowService.createWorkflow(vo);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新工作流")
    @ApiOperationLog(description = "更新工作流")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response updateWorkflow(@RequestBody @Validated UpdateWorkflowReqVO vo) {
        return workflowService.updateWorkflow(vo);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除工作流")
    @ApiOperationLog(description = "删除工作流")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response deleteWorkflow(@RequestBody Long id) {
        return workflowService.deleteWorkflow(id);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取工作流详情")
    @ApiOperationLog(description = "获取工作流详情")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response getWorkflowDetail(@RequestBody Long id) {
        return workflowService.getWorkflowDetail(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "工作流分页列表")
    @ApiOperationLog(description = "工作流分页列表")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public PageResponse findWorkflowPageList(@RequestBody @Validated FindWorkflowPageListReqVO vo) {
        return workflowService.findWorkflowPageList(vo);
    }

    @PostMapping("/toggle")
    @ApiOperation(value = "启用/禁用工作流")
    @ApiOperationLog(description = "启用/禁用工作流")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response toggleWorkflow(@RequestBody Long id) {
        return workflowService.toggleWorkflow(id);
    }

    @PostMapping("/debug")
    @ApiOperation(value = "调试工作流")
    @ApiOperationLog(description = "调试工作流")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response debugWorkflow(@RequestBody @Validated DebugWorkflowReqVO vo) {
        return workflowService.debugWorkflow(vo);
    }

    @PostMapping("/execution/list")
    @ApiOperation(value = "工作流执行记录列表")
    @ApiOperationLog(description = "工作流执行记录列表")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public PageResponse findExecutionPageList(@RequestParam Long workflowId,
                                              @RequestParam(defaultValue = "1") Long current,
                                              @RequestParam(defaultValue = "10") Long size) {
        return workflowService.findExecutionPageList(workflowId, current, size);
    }

    @PostMapping("/execution/logs")
    @ApiOperation(value = "工作流执行日志")
    @ApiOperationLog(description = "工作流执行日志")
    @PreAuthorize("hasAuthority('workflow:manage')")
    public Response findExecutionLogs(@RequestBody Long executionId) {
        return workflowService.findExecutionLogs(executionId);
    }
}
