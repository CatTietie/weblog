package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.notification.FindNotificationPageListReqVO;
import com.quanxiaoha.weblog.web.model.vo.notification.ReadNotificationReqVO;

public interface NotificationService {

    Response getUnreadCount();

    Response findNotificationList(FindNotificationPageListReqVO reqVO);

    Response readNotification(ReadNotificationReqVO reqVO);

    Response readAllNotifications();
}
