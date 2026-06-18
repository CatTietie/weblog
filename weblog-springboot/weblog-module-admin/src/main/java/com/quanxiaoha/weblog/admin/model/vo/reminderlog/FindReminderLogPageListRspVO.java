package com.quanxiaoha.weblog.admin.model.vo.reminderlog;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询投递提醒日志分页出参 VO")
public class FindReminderLogPageListRspVO {

    private Long id;

    private LocalDateTime executeTime;

    private Integer scannedCount;

    private Integer userCount;

    private Integer sentCount;

    private Integer failedCount;
}
