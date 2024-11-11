package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.BlogSettingsDO;
import org.apache.ibatis.annotations.Select;

/**
 * @author: Group 5

 * @date: 2023-08-22 17:06
 * @description: TODO
 **/
public interface BlogSettingsMapper extends BaseMapper<BlogSettingsDO> {

    @Select("select * from t_blog_settings where userName = #{username}")
    BlogSettingsDO selectByUsername(String username);
}
