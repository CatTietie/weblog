package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.common.utils.Response;

/**
 * @author: Group 5

 * @date: 2023-09-15 14:03
 * @description: 仪表盘
 **/
public interface AdminDashboardService {

    /**
     * 获取仪表盘基础统计信息
     * @return
     */
    Response findDashboardStatistics();

    /**
     * 获取文章发布热点统计信息
     * @return
     */
    Response findDashboardPublishArticleStatistics();

    /**
     * 获取文章最近一周 PV 访问量统计信息
     * @return
     */
    Response findDashboardPVStatistics();

    /**
     *获取文章阅读量统计
     * @return
     */
    Response findArticlePvCount();

    /**
     * 获取文章更新频率
     * @return
     */
    Response countArticleUpdateTimes();

    /**
     * 获取文章阅读量前6
     * @return
     */
    Response orderArticlePvCount();

    /**
     * 获取每个分类下的文章数量
     * @return
     */
    Response countCategory();
}
