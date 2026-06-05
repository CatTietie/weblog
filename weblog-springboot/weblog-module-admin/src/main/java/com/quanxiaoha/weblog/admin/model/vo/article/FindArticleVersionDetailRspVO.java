package com.quanxiaoha.weblog.admin.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleVersionDetailRspVO {

    private Long id;

    private Long articleId;

    private String title;

    private String content;

    private LocalDateTime createTime;
}
