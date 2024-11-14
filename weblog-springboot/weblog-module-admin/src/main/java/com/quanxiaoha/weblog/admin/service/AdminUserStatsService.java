package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.common.utils.Response;

/**
 * @author lixing
 */
public interface AdminUserStatsService {

    /**
     * 根据用户设备统计用户操作系统
     * @param device
     * @return
     */
    Response countByOs(String device);

    /**
     * 统计用户设备
     * @return
     */
    Response countByDevice();
}
