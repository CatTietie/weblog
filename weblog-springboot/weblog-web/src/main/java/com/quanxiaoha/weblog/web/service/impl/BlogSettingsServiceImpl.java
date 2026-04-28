package com.quanxiaoha.weblog.web.service.impl;

import com.quanxiaoha.weblog.common.domain.dos.BlogSettingsDO;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingsMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.convert.BlogSettingsConvert;
import com.quanxiaoha.weblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import com.quanxiaoha.weblog.web.service.BlogSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    private static final Long GLOBAL_SETTINGS_ID = 1L;

    @Override
    public Response findDetail() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BlogSettingsDO globalSettings = blogSettingsMapper.selectById(GLOBAL_SETTINGS_ID);
        if (Objects.isNull(globalSettings)) {
            globalSettings = BlogSettingsDO.builder()
                    .logo("")
                    .name("")
                    .introduction("")
                    .build();
        }

        BlogSettingsDO userSettings;
        if (username.equals("anonymousUser")) {
            userSettings = globalSettings;
        } else {
            userSettings = blogSettingsMapper.selectByUsername(username);
            if (Objects.isNull(userSettings)) {
                userSettings = globalSettings;
            }
        }

        FindBlogSettingsDetailRspVO vo = FindBlogSettingsDetailRspVO.builder()
                .logo(globalSettings.getLogo())
                .name(globalSettings.getName())
                .introduction(globalSettings.getIntroduction())
                .author(userSettings.getAuthor())
                .avatar(userSettings.getAvatar())
                .githubHomepage(userSettings.getGithubHomepage())
                .csdnHomepage(userSettings.getCsdnHomepage())
                .giteeHomepage(userSettings.getGiteeHomepage())
                .zhihuHomepage(userSettings.getZhihuHomepage())
                .build();

        return Response.success(vo);
    }
}
