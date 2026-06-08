package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.reminderlog.FindReminderLogPageListReqVO;
import com.quanxiaoha.weblog.admin.service.AdminReminderLogService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reminder-chat.log")
@Api(tags = "Admin 投递提醒日志模块")
public class AdminReminderLogController {

    @Autowired
    private AdminReminderLogService adminReminderLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获取投递提醒日志分页列表")
    @ApiOperationLog(description = "获取投递提醒日志分页列表")
    public Response findReminderLogPageList(@RequestBody FindReminderLogPageListReqVO findReminderLogPageListReqVO) {
        return adminReminderLogService.findReminderLogPageList(findReminderLogPageListReqVO);
    }
}
