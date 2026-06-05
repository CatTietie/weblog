package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.resumetemplate.*;
import com.quanxiaoha.weblog.admin.service.AdminResumeTemplateService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/resume/template")
@Api(tags = "Admin 简历模板模块")
public class AdminResumeTemplateController {

    @Autowired
    private AdminResumeTemplateService adminResumeTemplateService;

    @PostMapping("/add")
    @ApiOperation(value = "添加简历模板")
    @ApiOperationLog(description = "添加简历模板")
    @PreAuthorize("hasAuthority('template:create')")
    public Response addResumeTemplate(@RequestBody @Validated AddResumeTemplateReqVO addResumeTemplateReqVO) {
        return adminResumeTemplateService.addResumeTemplate(addResumeTemplateReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "简历模板分页数据获取")
    @ApiOperationLog(description = "简历模板分页数据获取")
    public PageResponse findResumeTemplatePageList(@RequestBody @Validated FindResumeTemplatePageListReqVO findResumeTemplatePageListReqVO) {
        return adminResumeTemplateService.findResumeTemplatePageList(findResumeTemplatePageListReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新简历模板")
    @ApiOperationLog(description = "更新简历模板")
    @PreAuthorize("hasAuthority('template:update')")
    public Response updateResumeTemplate(@RequestBody @Validated UpdateResumeTemplateReqVO updateResumeTemplateReqVO) {
        return adminResumeTemplateService.updateResumeTemplate(updateResumeTemplateReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除简历模板")
    @ApiOperationLog(description = "删除简历模板")
    @PreAuthorize("hasAuthority('template:delete')")
    public Response deleteResumeTemplate(@RequestBody @Validated DeleteResumeTemplateReqVO deleteResumeTemplateReqVO) {
        return adminResumeTemplateService.deleteResumeTemplate(deleteResumeTemplateReqVO);
    }

    @PostMapping("/status/update")
    @ApiOperation(value = "更新简历模板状态")
    @ApiOperationLog(description = "更新简历模板状态")
    @PreAuthorize("hasAuthority('template:update')")
    public Response updateResumeTemplateStatus(@RequestBody @Validated UpdateResumeTemplateStatusReqVO updateResumeTemplateStatusReqVO) {
        return adminResumeTemplateService.updateResumeTemplateStatus(updateResumeTemplateStatusReqVO);
    }
}
