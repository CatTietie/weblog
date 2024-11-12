package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.service.AdminDashboardService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Group 5
 * @date: 2023-09-15 14:01
 * @description: 仪表盘
 **/
@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "Admin 仪表盘")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;


    @PostMapping("/ArticlesReadNumber")
    @ApiOperation(value = "获取全部文章阅读量统计")
    @ApiOperationLog(description = "获取全部文章阅读量统计")
    public Response findArticlePvStatistics() {
        return dashboardService.findArticlePvCount();
    }

    @PostMapping("/ArticlesReadNumber/top6")
    @ApiOperation(value = "获取全部文章阅读量前6")
    @ApiOperationLog(description = "获取全部文章阅读量前6")
    public Response orderArticlePvStatistics() {
        return dashboardService.orderArticlePvCount();
    }

    @PostMapping("/statistics")
    @ApiOperation(value = "获取后台仪表盘基础统计信息")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    public Response findDashboardStatistics() {
        return dashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @ApiOperation(value = "获取后台仪表盘文章发布热点统计信息")
    @ApiOperationLog(description = "获取后台仪表盘文章发布热点统计信息")
    public Response findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }

    @PostMapping("/pv/statistics")
    @ApiOperation(value = "获取后台仪表盘最近一个月 PV 访问量信息")
    @ApiOperationLog(description = "获取后台仪表盘最近一个月 PV 访问量信息")
    public Response findDashboardPVStatistics() {
        return dashboardService.findDashboardPVStatistics();
    }

    @PostMapping("/updateTimes/statistics")
    @ApiOperation(value = "获取后台仪表盘最近一个月 更新频率 访问量信息")
    @ApiOperationLog(description = "获取后台仪表盘最近一个月 更新频率 访问量信息")
    public Response findDashboardUpdateStatistics() {
        return dashboardService.findDashboardUpdateStatistics();
    }


    @PostMapping("/updateArticle/statistics")
    @ApiOperation(value = "统计文章更新频率")
    @ApiOperationLog(description = "统计文章更新频率")
    public Response countArticleUpdateTimes() {
        return dashboardService.countArticleUpdateTimes();
    }

    @PostMapping("/category")
    @ApiOperation(value = "统计分类下的文章数量")
    @ApiOperationLog(description = "统计分类下的文章数量")
    public Response countCategory() {
        return dashboardService.countCategory();
    }

    @PostMapping("/tag")
    @ApiOperation(value = "统计标签下的文章数量")
    @ApiOperationLog(description = "统计标签下的文章数量")
    public Response countTags() {
        return dashboardService.countTags();
    }
}
