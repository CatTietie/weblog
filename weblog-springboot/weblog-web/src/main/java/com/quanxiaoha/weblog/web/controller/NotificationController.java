package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.notification.FindNotificationPageListReqVO;
import com.quanxiaoha.weblog.web.model.vo.notification.ReadNotificationReqVO;
import com.quanxiaoha.weblog.web.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@Api(tags = "通知")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/unreadCount")
    @ApiOperation(value = "查询未读通知数量")
    @ApiOperationLog(description = "查询未读通知数量")
    public Response getUnreadCount() {
        return notificationService.getUnreadCount();
    }

    @PostMapping("/list")
    @ApiOperation(value = "分页查询通知列表")
    @ApiOperationLog(description = "分页查询通知列表")
    public Response findNotificationList(@RequestBody @Validated FindNotificationPageListReqVO reqVO) {
        return notificationService.findNotificationList(reqVO);
    }

    @PostMapping("/read")
    @ApiOperation(value = "标记单条通知为已读")
    @ApiOperationLog(description = "标记单条通知为已读")
    public Response readNotification(@RequestBody @Validated ReadNotificationReqVO reqVO) {
        return notificationService.readNotification(reqVO);
    }

    @PostMapping("/readAll")
    @ApiOperation(value = "标记全部通知为已读")
    @ApiOperationLog(description = "标记全部通知为已读")
    public Response readAllNotifications() {
        return notificationService.readAllNotifications();
    }
}
