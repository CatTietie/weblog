package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.AddResumeApplicationReqVO;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.DeleteResumeApplicationReqVO;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.FindResumeApplicationPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.UpdateResumeApplicationReqVO;
import com.quanxiaoha.weblog.admin.service.AdminResumeApplicationService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/resume/application")
@Api(tags = "Admin 简历投递记录模块")
public class AdminResumeApplicationController {

    @Autowired
    private AdminResumeApplicationService adminResumeApplicationService;

    @PostMapping("/add")
    @ApiOperation(value = "新增投递记录")
    @ApiOperationLog(description = "新增投递记录")
    public Response addApplication(@RequestBody @Validated AddResumeApplicationReqVO vo) {
        return adminResumeApplicationService.addApplication(vo);
    }

    @PostMapping("/list")
    @ApiOperation(value = "投递记录分页列表")
    @ApiOperationLog(description = "投递记录分页列表")
    public PageResponse findApplicationPageList(@RequestBody @Validated FindResumeApplicationPageListReqVO vo) {
        return adminResumeApplicationService.findApplicationPageList(vo);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新投递记录")
    @ApiOperationLog(description = "更新投递记录")
    public Response updateApplication(@RequestBody @Validated UpdateResumeApplicationReqVO vo) {
        return adminResumeApplicationService.updateApplication(vo);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除投递记录")
    @ApiOperationLog(description = "删除投递记录")
    public Response deleteApplication(@RequestBody @Validated DeleteResumeApplicationReqVO vo) {
        return adminResumeApplicationService.deleteApplication(vo);
    }

    @PostMapping("/statistics")
    @ApiOperation(value = "投递记录统计")
    @ApiOperationLog(description = "投递记录统计")
    public Response getApplicationStatistics() {
        return adminResumeApplicationService.getApplicationStatistics();
    }
}
