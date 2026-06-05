package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.staticsite.FindGenTaskPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.staticsite.TriggerGenerationReqVO;
import com.quanxiaoha.weblog.admin.model.vo.staticsite.UpdateStaticSiteConfigReqVO;
import com.quanxiaoha.weblog.admin.service.AdminStaticSiteService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin/static-site")
@Api(tags = "Admin 静态站点管理模块")
public class AdminStaticSiteController {

    @Autowired
    private AdminStaticSiteService staticSiteService;

    @PostMapping("/config/update")
    @ApiOperation(value = "更新静态站点配置")
    @ApiOperationLog(description = "更新静态站点配置")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public Response updateConfig(@RequestBody @Validated UpdateStaticSiteConfigReqVO vo) {
        return staticSiteService.updateConfig(vo);
    }

    @PostMapping("/config/detail")
    @ApiOperation(value = "获取静态站点配置详情")
    @ApiOperationLog(description = "获取静态站点配置详情")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public Response findConfigDetail() {
        return staticSiteService.findConfigDetail();
    }

    @PostMapping("/generate")
    @ApiOperation(value = "触发静态站点生成")
    @ApiOperationLog(description = "触发静态站点生成")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public Response triggerGeneration(@RequestBody @Validated TriggerGenerationReqVO vo) {
        return staticSiteService.triggerGeneration(vo);
    }

    @PostMapping("/deploy")
    @ApiOperation(value = "触发部署")
    @ApiOperationLog(description = "触发部署")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public Response triggerDeploy() {
        return staticSiteService.triggerDeploy();
    }

    @GetMapping("/download")
    @ApiOperation(value = "下载静态站点 ZIP")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public void downloadZip(HttpServletResponse response) {
        staticSiteService.downloadZip(response);
    }

    @PostMapping("/task/list")
    @ApiOperation(value = "生成任务列表")
    @ApiOperationLog(description = "生成任务列表")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public Response findTaskPageList(@RequestBody FindGenTaskPageListReqVO vo) {
        return staticSiteService.findTaskPageList(vo);
    }

    @PostMapping("/task/progress")
    @ApiOperation(value = "查询当前任务进度")
    @PreAuthorize("hasAuthority('static-site:manage')")
    public Response findTaskProgress() {
        return staticSiteService.findTaskProgress();
    }
}
