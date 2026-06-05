package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.quanxiaoha.weblog.common.domain.dos.ArticleVersionDO;

import java.util.List;

public interface ArticleVersionMapper extends BaseMapper<ArticleVersionDO> {

    default List<ArticleVersionDO> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<ArticleVersionDO>lambdaQuery()
                .eq(ArticleVersionDO::getArticleId, articleId)
                .orderByDesc(ArticleVersionDO::getCreateTime));
    }
}
