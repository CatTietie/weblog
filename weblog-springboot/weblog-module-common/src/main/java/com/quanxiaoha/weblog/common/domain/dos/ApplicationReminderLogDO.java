package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_application_reminder_log")
public class ApplicationReminderLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDateTime executeTime;

    private Integer scannedCount;

    private Integer userCount;

    private Integer sentCount;

    private Integer failedCount;

    private LocalDateTime createTime;
}
