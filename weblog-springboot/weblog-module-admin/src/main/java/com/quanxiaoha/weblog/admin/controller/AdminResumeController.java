package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.resume.*;
import com.quanxiaoha.weblog.admin.service.AdminResumeService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/resume")
@Api(tags = "Admin 简历模块")
public class AdminResumeController {

    @Autowired
    private AdminResumeService adminResumeService;

    @PostMapping("/list")
    @ApiOperation(value = "获取简历列表")
    @ApiOperationLog(description = "获取简历列表")
    public Response findResumeList() {
        return adminResumeService.findResumeList();
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取简历详情")
    @ApiOperationLog(description = "获取简历详情")
    public Response findResumeDetail(@RequestBody @Validated FindResumeDetailReqVO findResumeDetailReqVO) {
        return adminResumeService.findResumeDetail(findResumeDetailReqVO);
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建简历")
    @ApiOperationLog(description = "创建简历")
    public Response createResume(@RequestBody @Validated CreateResumeReqVO createResumeReqVO) {
        return adminResumeService.createResume(createResumeReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新简历")
    @ApiOperationLog(description = "更新简历")
    public Response updateResume(@RequestBody @Validated UpdateResumeReqVO updateResumeReqVO) {
        return adminResumeService.updateResume(updateResumeReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除简历")
    @ApiOperationLog(description = "删除简历")
    public Response deleteResume(@RequestBody @Validated DeleteResumeReqVO deleteResumeReqVO) {
        return adminResumeService.deleteResume(deleteResumeReqVO);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "上传简历文件")
    @ApiOperationLog(description = "上传简历文件")
    public Response uploadResume(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "resumeId", required = false) Long resumeId,
                                 @RequestParam(value = "name", required = false) String name) {
        return adminResumeService.uploadResume(file, resumeId, name);
    }
}
