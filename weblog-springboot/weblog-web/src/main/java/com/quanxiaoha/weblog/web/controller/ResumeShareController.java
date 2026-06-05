package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.service.ResumeShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume/share")
@Api(tags = "简历公开分享")
public class ResumeShareController {

    @Autowired
    private ResumeShareService resumeShareService;

    @GetMapping("/{shareCode}")
    @ApiOperation(value = "获取分享简历内容")
    @ApiOperationLog(description = "获取分享简历内容")
    public Response findSharedResume(@PathVariable("shareCode") String shareCode) {
        return resumeShareService.findSharedResume(shareCode);
    }
}
