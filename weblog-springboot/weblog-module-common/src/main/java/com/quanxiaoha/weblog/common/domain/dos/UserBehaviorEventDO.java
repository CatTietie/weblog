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
@TableName("t_user_behavior_event")
public class UserBehaviorEventDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer eventType;

    private Long articleId;

    private Long tagId;

    private String keyword;

    private Integer durationSeconds;

    private LocalDateTime createTime;

    private Long tenantId;
}
