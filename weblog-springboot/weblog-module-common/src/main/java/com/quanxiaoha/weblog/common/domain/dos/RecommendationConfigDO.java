package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_recommendation_config")
public class RecommendationConfigDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer configType;

    private Long articleId;

    private Long tagId;

    private BigDecimal weightAdjustment;

    private Boolean isActive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long tenantId;
}
