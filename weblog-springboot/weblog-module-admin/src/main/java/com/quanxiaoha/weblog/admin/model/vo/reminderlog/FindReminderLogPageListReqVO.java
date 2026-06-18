package com.quanxiaoha.weblog.admin.model.vo.reminderlog;

import com.quanxiaoha.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "查询投递提醒日志分页入参 VO")
public class FindReminderLogPageListReqVO extends BasePageQuery {
}
