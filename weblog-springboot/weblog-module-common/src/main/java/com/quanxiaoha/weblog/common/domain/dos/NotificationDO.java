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
@TableName("t_notification")
public class NotificationDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long receiverId;

    private Integer type;

    private String title;

    private String content;

    private Long articleId;

    private Long commentId;

    private Long senderId;

    private Boolean isRead;

    private LocalDateTime createTime;

    private Long tenantId;
}
