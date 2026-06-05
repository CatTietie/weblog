package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.behavior.ReportBehaviorReqVO;
import com.quanxiaoha.weblog.web.model.vo.behavior.ReportRecommendClickReqVO;
import com.quanxiaoha.weblog.web.service.BehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/behavior")
@Api(tags = "行为采集")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/report")
    @ApiOperation(value = "批量上报行为事件")
    @ApiOperationLog(description = "批量上报行为事件")
    public Response reportBehavior(@RequestBody @Validated ReportBehaviorReqVO reportBehaviorReqVO) {
        return behaviorService.reportBehavior(reportBehaviorReqVO);
    }

    @PostMapping("/click")
    @ApiOperation(value = "上报推荐点击")
    @ApiOperationLog(description = "上报推荐点击")
    public Response reportRecommendClick(@RequestBody @Validated ReportRecommendClickReqVO reportRecommendClickReqVO) {
        return behaviorService.reportRecommendClick(reportRecommendClickReqVO);
    }
}
