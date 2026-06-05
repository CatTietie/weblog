package com.quanxiaoha.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticlesBySearchRspVO {
    private Long id;
    private String cover;
    private String title;
    private String summary;
    private LocalDate createDate;
}
