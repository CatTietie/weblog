package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.service.ResumeTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume/template")
@Api(tags = "简历模板公开接口")
public class ResumeTemplateController {

    @Autowired
    private ResumeTemplateService resumeTemplateService;

    @PostMapping("/enabled/list")
    @ApiOperation(value = "获取已启用的简历模板列表")
    @ApiOperationLog(description = "获取已启用的简历模板列表")
    public Response findEnabledTemplateList() {
        return resumeTemplateService.findEnabledTemplateList();
    }
}
