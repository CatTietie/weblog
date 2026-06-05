package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.recommendation.FindRelatedArticlesReqVO;
import com.quanxiaoha.weblog.web.service.RecommendationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendation")
@Api(tags = "内容推荐")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/home")
    @ApiOperation(value = "获取首页推荐文章")
    @ApiOperationLog(description = "获取首页推荐文章")
    public Response findHomeRecommendations() {
        return recommendationService.findHomeRecommendations();
    }

    @PostMapping("/related")
    @ApiOperation(value = "获取相关推荐文章")
    @ApiOperationLog(description = "获取相关推荐文章")
    public Response findRelatedArticles(@RequestBody @Validated FindRelatedArticlesReqVO findRelatedArticlesReqVO) {
        return recommendationService.findRelatedArticles(findRelatedArticlesReqVO);
    }
}
