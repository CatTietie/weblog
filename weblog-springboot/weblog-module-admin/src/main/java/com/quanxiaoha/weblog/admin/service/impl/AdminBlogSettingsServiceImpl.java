package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.admin.convert.BlogSettingsConvert;
import com.quanxiaoha.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.quanxiaoha.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.quanxiaoha.weblog.admin.service.AdminBlogSettingsService;
import com.quanxiaoha.weblog.common.domain.dos.BlogSettingsDO;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingsMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    private static final Long GLOBAL_SETTINGS_ID = 1L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BlogSettingsDO globalSettings = blogSettingsMapper.selectById(GLOBAL_SETTINGS_ID);
        if (Objects.isNull(globalSettings)) {
            globalSettings = BlogSettingsDO.builder()
                    .id(GLOBAL_SETTINGS_ID)
                    .username("global")
                    .build();
        }
        globalSettings.setLogo(updateBlogSettingsReqVO.getLogo());
        globalSettings.setName(updateBlogSettingsReqVO.getName());
        globalSettings.setIntroduction(updateBlogSettingsReqVO.getIntroduction());
        saveOrUpdate(globalSettings);

        BlogSettingsDO userSettings = blogSettingsMapper.selectByUsername(username);
        if (Objects.isNull(userSettings)) {
            userSettings = BlogSettingsDO.builder()
                    .username(username)
                    .build();
        }
        userSettings.setAuthor(updateBlogSettingsReqVO.getAuthor());
        userSettings.setAvatar(updateBlogSettingsReqVO.getAvatar());
        userSettings.setGithubHomepage(updateBlogSettingsReqVO.getGithubHomepage());
        userSettings.setCsdnHomepage(updateBlogSettingsReqVO.getCsdnHomepage());
        userSettings.setGiteeHomepage(updateBlogSettingsReqVO.getGiteeHomepage());
        userSettings.setZhihuHomepage(updateBlogSettingsReqVO.getZhihuHomepage());

        if (Objects.isNull(userSettings.getId())) {
            blogSettingsMapper.insert(userSettings);
        } else {
            UpdateWrapper<BlogSettingsDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("userName", username);
            blogSettingsMapper.update(userSettings, wrapper);
        }

        return Response.success();
    }

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

        BlogSettingsDO userSettings = blogSettingsMapper.selectByUsername(username);
        if (Objects.isNull(userSettings)) {
            userSettings = BlogSettingsDO.builder()
                    .username(username)
                    .author(username)
                    .avatar("")
                    .githubHomepage("")
                    .csdnHomepage("")
                    .giteeHomepage("")
                    .zhihuHomepage("")
                    .build();
        }

        FindBlogSettingsRspVO vo = FindBlogSettingsRspVO.builder()
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
