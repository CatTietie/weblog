package com.quanxiaoha.weblog.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.service.AdminUserStatsService;
import com.quanxiaoha.weblog.common.domain.mapper.UserVisitStatsMapper;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import com.quanxiaoha.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lixing
 * @version V1.0
 * Copyright (c) 2024, lixing@hwadee.com All Rights Reserved.
 * @ProjectName:weblog-springboot
 * @Title: AdminUserStatsServiceImpl
 * @Package com.quanxiaoha.weblog.admin.service.impl
 * @Description: 后台用户统计服务层
 * @date 2024/11/14 10:24
 */
@Service
public class AdminUserStatsServiceImpl implements AdminUserStatsService {

    @Autowired
    private UserVisitStatsMapper userVisitStatsMapper;

    @Override
    public Response countByOs(String device) {
        List<JSONObject> queryRes = userVisitStatsMapper.countOsByDevice(device);
        Long max = 0L;
        for (JSONObject queryRe : queryRes) {
            if (queryRe.getLong("count") > max) {
                max = queryRe.getLong("count");
            }
        }
        JSONObject res = convertJsonQuery(queryRes, "OsName", "count");
        res.put("max", max + 30);
        return Response.success(res);
    }

    @Override
    public Response countByDevice() {
        List<PieDataVO> res = userVisitStatsMapper.countByDevice();
        return Response.success(res);
    }

    @Override
    public Response countPageUrl() {
        Long adminCount = userVisitStatsMapper.countByAdmin();
        Long frontCount = userVisitStatsMapper.countByFront();
        ArrayList<PieDataVO> res = new ArrayList<>();
        res.add(PieDataVO.builder().name("后台访问").value(adminCount).build());
        res.add(PieDataVO.builder().name("前台访问").value(frontCount).build());
        return Response.success(res);
    }

    @Override
    public Response countByBrowser() {
        List<JSONObject> queryRes = userVisitStatsMapper.countByBrowser();
        return Response.success(convertJsonQuery(queryRes, "xData", "seriesData"));
    }

    @Override
    public Response countByPeriod() {
        //获取到十天的日期
        List<LocalDateTime> lastTenDays = generateLastTenDays();
        LocalDateTime lastTenDay = lastTenDays.get(0);


        JSONObject res = new JSONObject();

        ArrayList<Long> zhuomianCounts = new ArrayList<>();

        zhuomianCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "00:00:00", "06:00:00", "桌面端"));
        zhuomianCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "06:00:00", "10:00:00", "桌面端"));
        zhuomianCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "10:00:00", "16:00:00", "桌面端"));
        zhuomianCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "16:00:00", "20:00:00", "桌面端"));
        zhuomianCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "20:00:00", "25:00:00", "桌面端"));
        res.put("desk", zhuomianCounts);
        ArrayList<Long> mobileCounts = new ArrayList<>();

        mobileCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "00:00:00", "06:00:00", "移动端"));
        mobileCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "06:00:00", "10:00:00", "移动端"));
        mobileCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "10:00:00", "16:00:00", "移动端"));
        mobileCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "16:00:00", "20:00:00", "移动端"));
        mobileCounts.add(userVisitStatsMapper.countByPeriod(lastTenDay, "20:00:00", "25:00:00", "移动端"));
        res.put("mobile", mobileCounts);
        ArrayList<Long> adminPage = new ArrayList<>();

        adminPage.add(userVisitStatsMapper.countByPage(lastTenDay, "00:00:00", "06:00:00", "Admin"));
        adminPage.add(userVisitStatsMapper.countByPage(lastTenDay, "06:00:00", "10:00:00", "Admin"));
        adminPage.add(userVisitStatsMapper.countByPage(lastTenDay, "10:00:00", "16:00:00", "Admin"));
        adminPage.add(userVisitStatsMapper.countByPage(lastTenDay, "16:00:00", "20:00:00", "Admin"));
        adminPage.add(userVisitStatsMapper.countByPage(lastTenDay, "20:00:00", "25:00:00", "Admin"));
        res.put("back", adminPage);
        ArrayList<Long> frontPage = new ArrayList<>();

        frontPage.add(userVisitStatsMapper.countByPageUrl(lastTenDay, "00:00:00", "06:00:00", "Admin"));
        frontPage.add(userVisitStatsMapper.countByPageUrl(lastTenDay, "06:00:00", "10:00:00", "Admin"));
        frontPage.add(userVisitStatsMapper.countByPageUrl(lastTenDay, "10:00:00", "16:00:00", "Admin"));
        frontPage.add(userVisitStatsMapper.countByPageUrl(lastTenDay, "16:00:00", "20:00:00", "Admin"));
        frontPage.add(userVisitStatsMapper.countByPageUrl(lastTenDay, "20:00:00", "25:00:00", "Admin"));
        res.put("front", frontPage);


        //需要返回十组数据，且每组数据包含四组，每子组对应的name，对应的value
        return Response.success(res);
    }


    public static List<LocalDateTime> generateLastTenDays() {
        List<LocalDateTime> dateTimes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 10; i++) {
            dateTimes.add(now.minusDays(i));
        }

        return dateTimes;
    }

    private JSONObject convertJsonQuery(List<JSONObject> queryRes, String xKey, String seriesKey) {
        ArrayList<Long> seriesData = new ArrayList<>();
        ArrayList<String> XData = new ArrayList<>();
        for (JSONObject queryRe : queryRes) {
            XData.add((String) queryRe.get(xKey));
            seriesData.add((Long) queryRe.get(seriesKey));
        }
        JSONObject res = new JSONObject();
        res.put("xData", XData);
        res.put("seriesData", seriesData);
        return res;
    }

}
