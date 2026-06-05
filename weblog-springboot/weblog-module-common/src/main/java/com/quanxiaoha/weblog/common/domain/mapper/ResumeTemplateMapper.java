package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.ResumeTemplateDO;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.List;

public interface ResumeTemplateMapper extends BaseMapper<ResumeTemplateDO> {

    default Page<ResumeTemplateDO> selectPageList(Long current, Long size, String name) {
        Page<ResumeTemplateDO> page = new Page<>(current, size);
        LambdaQueryWrapper<ResumeTemplateDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), ResumeTemplateDO::getName, name)
                .orderByAsc(ResumeTemplateDO::getSortOrder)
                .orderByDesc(ResumeTemplateDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    default List<ResumeTemplateDO> selectEnabledList() {
        LambdaQueryWrapper<ResumeTemplateDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeTemplateDO::getStatus, 1)
                .orderByAsc(ResumeTemplateDO::getSortOrder);
        return selectList(wrapper);
    }

    default ResumeTemplateDO selectByName(String name) {
        LambdaQueryWrapper<ResumeTemplateDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeTemplateDO::getName, name);
        return selectOne(wrapper);
    }

    default ResumeTemplateDO selectByComponentName(String componentName) {
        LambdaQueryWrapper<ResumeTemplateDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeTemplateDO::getComponentName, componentName);
        return selectOne(wrapper);
    }
}
