package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;

public interface BlogSettingsService {

    Response findDetail();

    FindBlogSettingsDetailRspVO getLatestBlogSettings();
}
