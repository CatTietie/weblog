package com.quanxiaoha.weblog.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.quanxiaoha.weblog.admin.service.AdminUserStatsService;
import com.quanxiaoha.weblog.common.domain.mapper.UserVisitStatsMapper;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import com.quanxiaoha.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
