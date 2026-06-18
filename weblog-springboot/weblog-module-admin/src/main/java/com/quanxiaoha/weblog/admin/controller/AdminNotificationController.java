package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.notification.SendGlobalNotificationReqVO;
import com.quanxiaoha.weblog.admin.service.AdminNotificationService;
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
@RequestMapping("/admin/notification")
@Api(tags = "Admin 通知模块")
public class AdminNotificationController {

    @Autowired
    private AdminNotificationService adminNotificationService;

    @PostMapping("/sendGlobal")
    @ApiOperation(value = "发送全局通知")
    @PreAuthorize("hasAuthority('notification:send')")
    public Response sendGlobalNotification(@RequestBody @Validated SendGlobalNotificationReqVO reqVO) {
        return adminNotificationService.sendGlobalNotification(reqVO);
    }
}
