package com.quanxiaoha.weblog.web.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.quanxiaoha.weblog.common.context.TenantContext;
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
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    private static final Long GLOBAL_SETTINGS_ID = 1L;

    private final Cache<String, FindBlogSettingsDetailRspVO> blogSettingsCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    public Response findDetail() {
        FindBlogSettingsDetailRspVO vo = getLatestBlogSettings();
        return Response.success(vo);
    }

    @Override
    public FindBlogSettingsDetailRspVO getLatestBlogSettings() {
        Long tenantId = TenantContext.getTenantId();
        String cacheKey = "blog_settings:" + tenantId;

        FindBlogSettingsDetailRspVO cached = blogSettingsCache.get(cacheKey, key -> {
            BlogSettingsDO globalSettings = blogSettingsMapper.selectById(GLOBAL_SETTINGS_ID);
            if (Objects.isNull(globalSettings)) {
                globalSettings = BlogSettingsDO.builder()
                        .logo("")
                        .name("")
                        .introduction("")
                        .build();
            }

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            BlogSettingsDO userSettings;
            if ("anonymousUser".equals(username)) {
                userSettings = globalSettings;
            } else {
                userSettings = blogSettingsMapper.selectByUsername(username);
                if (Objects.isNull(userSettings)) {
                    userSettings = globalSettings;
                }
            }

            return FindBlogSettingsDetailRspVO.builder()
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
        });

        return cached;
    }
}
