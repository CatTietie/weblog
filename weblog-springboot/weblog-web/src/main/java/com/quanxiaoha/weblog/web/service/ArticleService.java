package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.quanxiaoha.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

/**
 * @author: Group 5

 * @date: 2023-09-15 14:03
 * @description: 文章
 **/
public interface ArticleService {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

}
