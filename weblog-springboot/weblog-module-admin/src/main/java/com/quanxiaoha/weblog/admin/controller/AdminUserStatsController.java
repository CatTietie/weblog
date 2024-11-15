package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.service.AdminUserStatsService;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lixing
 * @version V1.0
 * Copyright (c) 2024, lixing@hwadee.com All Rights Reserved.
 * @ProjectName:weblog-springboot
 * @Title: AdminUserStatsController
 * @Package com.quanxiaoha.weblog.admin
 * @Description: 用户统计controller
 * @date 2024/11/14 10:22
 */
@RestController
@RequestMapping("/admin/userStats")
@Api(tags = " Admin 用户统计")
public class AdminUserStatsController {

    @Autowired
    private AdminUserStatsService adminUserStatsService;


    /**
     * 根据设备统计用户操作系统
     *
     * @param device
     * @return
     */
    @GetMapping("/os")
    public Response countByOs(@RequestParam("device") String device) {
        return adminUserStatsService.countByOs(device);
    }

    /**
     * 统计用户设备
     *
     * @return
     */
    @GetMapping("/device")
    public Response countByDevice() {
        return adminUserStatsService.countByDevice();
    }

    /**
     * 统计用户访问前后台
     *
     * @return
     */
    @GetMapping("/pageUrl")
    public Response countByPageUrl() {
        return adminUserStatsService.countPageUrl();
    }

    /**
     * 统计用户访问浏览器
     *
     * @return
     */
    @GetMapping("/browser")
    public Response countByBrowser() {
        return adminUserStatsService.countByBrowser();
    }

    /**
     * 统计指定时间段访问
     *
     * @return
     */
    @GetMapping("/period")
    public Response countByPeriod() {
        return adminUserStatsService.countByPeriod();
    }


}
