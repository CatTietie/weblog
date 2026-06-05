package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.SensitiveWordDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SensitiveWordMapper extends BaseMapper<SensitiveWordDO> {

    @Select("SELECT word FROM t_sensitive_word WHERE tenant_id = #{tenantId}")
    List<String> selectAllWords(@Param("tenantId") Long tenantId);

    default Page<SensitiveWordDO> selectPageList(Long current, Long size, String keyword) {
        Page<SensitiveWordDO> page = new Page<>(current, size);
        return selectPage(page, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SensitiveWordDO>()
                .like(keyword != null && !keyword.isEmpty(), SensitiveWordDO::getWord, keyword)
                .orderByDesc(SensitiveWordDO::getCreateTime));
    }
}
