package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.sensitiveword.*;
import com.quanxiaoha.weblog.admin.service.AdminSensitiveWordService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/sensitive-word")
@Api(tags = "Admin 敏感词管理")
public class AdminSensitiveWordController {

    @Autowired
    private AdminSensitiveWordService adminSensitiveWordService;

    @PostMapping("/add")
    @ApiOperation(value = "添加敏感词")
    @ApiOperationLog(description = "添加敏感词")
    @PreAuthorize("hasAuthority('sensitive_word:add')")
    public Response add(@RequestBody @Validated AddSensitiveWordReqVO addSensitiveWordReqVO) {
        return adminSensitiveWordService.addSensitiveWord(addSensitiveWordReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除敏感词")
    @ApiOperationLog(description = "删除敏感词")
    @PreAuthorize("hasAuthority('sensitive_word:delete')")
    public Response delete(@RequestBody Map<String, Long> params) {
        return adminSensitiveWordService.deleteSensitiveWord(params.get("id"));
    }

    @PostMapping("/list")
    @ApiOperation(value = "敏感词分页列表")
    @ApiOperationLog(description = "敏感词分页列表")
    @PreAuthorize("hasAuthority('sensitive_word:list')")
    public Response list(@RequestBody FindSensitiveWordPageListReqVO findSensitiveWordPageListReqVO) {
        return adminSensitiveWordService.findSensitiveWordPageList(findSensitiveWordPageListReqVO);
    }

    @PostMapping("/batch-import")
    @ApiOperation(value = "批量导入敏感词")
    @ApiOperationLog(description = "批量导入敏感词")
    @PreAuthorize("hasAuthority('sensitive_word:import')")
    public Response batchImport(@RequestBody @Validated BatchImportSensitiveWordReqVO batchImportSensitiveWordReqVO) {
        return adminSensitiveWordService.batchImport(batchImportSensitiveWordReqVO);
    }

    @PostMapping("/scan/start")
    @ApiOperation(value = "启动全库扫描")
    @ApiOperationLog(description = "启动全库扫描")
    @PreAuthorize("hasAuthority('sensitive_word:scan')")
    public Response startScan() {
        return adminSensitiveWordService.startScan();
    }

    @GetMapping("/scan/tasks")
    @ApiOperation(value = "获取扫描任务列表")
    @PreAuthorize("hasAuthority('sensitive_word:scan')")
    public Response getScanTasks() {
        return adminSensitiveWordService.getScanTaskList();
    }

    @GetMapping("/scan/progress/{taskId}")
    @ApiOperation(value = "获取扫描任务进度")
    @PreAuthorize("hasAuthority('sensitive_word:scan')")
    public Response getScanProgress(@PathVariable Long taskId) {
        return adminSensitiveWordService.getScanTaskProgress(taskId);
    }

    @PostMapping("/scan/results")
    @ApiOperation(value = "扫描结果分页列表")
    @PreAuthorize("hasAuthority('sensitive_word:scan')")
    public Response getScanResults(@RequestBody FindScanResultPageListReqVO findScanResultPageListReqVO) {
        return adminSensitiveWordService.findScanResultPageList(findScanResultPageListReqVO);
    }

    @PostMapping("/scan/handle")
    @ApiOperation(value = "处理扫描结果")
    @ApiOperationLog(description = "处理扫描结果")
    @PreAuthorize("hasAuthority('sensitive_word:scan')")
    public Response handleResult(@RequestBody @Validated HandleScanResultReqVO handleScanResultReqVO) {
        return adminSensitiveWordService.handleScanResult(handleScanResultReqVO);
    }
}
