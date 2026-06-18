package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.article.*;
import com.quanxiaoha.weblog.common.utils.Response;

/**
 * @author: Group 5

 * @date: 2023-09-15 14:03
 * @description: 文章
 **/
public interface AdminArticleService {
    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    Response publishArticle(PublishArticleReqVO publishArticleReqVO);

    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO);

    /**
     * 查询文章分页数据
     * @param findArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO);

    /**
     * 查询文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 更新文章
     * @param updateArticleReqVO
     * @return
     */
    Response updateArticle(UpdateArticleReqVO updateArticleReqVO);

    /**
     * 修改文章状态
     * @param changeArticleStatusReqVO
     * @return
     */
    Response changeArticleStatus(ChangeArticleStatusReqVO changeArticleStatusReqVO);

    /**
     * 查询文章版本列表
     * @param findArticleVersionListReqVO
     * @return
     */
    Response findArticleVersionList(FindArticleVersionListReqVO findArticleVersionListReqVO);

    /**
     * 查询文章版本详情
     * @param findArticleVersionDetailReqVO
     * @return
     */
    Response findArticleVersionDetail(FindArticleVersionDetailReqVO findArticleVersionDetailReqVO);
}
