package com.quanxiaoha.weblog.web.model.vo.recommendation;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "推荐文章响应")
public class FindRecommendedArticlesRspVO {

    private Long id;
    private String title;
    private String cover;
    private String summary;
    private String createDate;
    private Long readNum;
    private List<TagInfo> tags;
    private Double score;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TagInfo {
        private Long id;
        private String name;
    }
}
