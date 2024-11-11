package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.Objects;

/**
 * @author: Group 5
 * @date: 2023-09-15 14:03
 * @description: TODO
 **/
@Service
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    @Override
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {

        //获取到当前用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //若为空则进行插入操作

        // VO 转 DO
        BlogSettingsDO blogSettingsDO = BlogSettingsConvert.INSTANCE.convertVO2DO(updateBlogSettingsReqVO);
        blogSettingsDO.setUsername(username);

        // 保存或更新（当数据库中存在 username 为 当前用户的记录时，则执行更新操作，否则执行插入操作）
        if(Objects.isNull(blogSettingsMapper.selectByUsername(username))){
            blogSettingsMapper.insert(blogSettingsDO);
        }
        QueryWrapper<BlogSettingsDO> wrapper = new QueryWrapper<>();
        wrapper.eq("userName", username);
//        update(blogSettingsDO, wrapper);
        saveOrUpdate(blogSettingsDO,wrapper);
        return Response.success();
    }

    /**
     * 获取博客设置详情 TODO需要改为获取当前账户的设置详情
     *
     * @return
     */
    @Override
    public Response findDetail() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 查询 ID 为 1 的记录
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectByUsername(username);

        // DO 转 VO
        FindBlogSettingsRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);

        return Response.success(vo);
    }
}
