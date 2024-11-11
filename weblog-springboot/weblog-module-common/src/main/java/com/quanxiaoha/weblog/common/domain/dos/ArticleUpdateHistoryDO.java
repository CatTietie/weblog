package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章更新频率对应DO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_article_update_history")
public class ArticleUpdateHistoryDO {

    @TableId(type = IdType.NONE)
    private Long articleId;

    private LocalDateTime updateTime;
}