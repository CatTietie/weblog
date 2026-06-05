package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.notification.SendGlobalNotificationReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminNotificationService {

    Response sendGlobalNotification(SendGlobalNotificationReqVO reqVO);
}
