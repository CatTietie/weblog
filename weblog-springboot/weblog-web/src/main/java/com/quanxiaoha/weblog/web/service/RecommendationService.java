package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.recommendation.FindRelatedArticlesReqVO;

public interface RecommendationService {

    Response findHomeRecommendations();

    Response findRelatedArticles(FindRelatedArticlesReqVO findRelatedArticlesReqVO);
}
