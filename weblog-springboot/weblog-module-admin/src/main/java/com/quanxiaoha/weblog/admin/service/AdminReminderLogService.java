package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.reminderlog.FindReminderLogPageListReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminReminderLogService {

    Response findReminderLogPageList(FindReminderLogPageListReqVO findReminderLogPageListReqVO);
}
