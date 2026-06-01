package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.CommentDO;

import java.util.List;

public interface CommentMapper extends BaseMapper<CommentDO> {

    default Page<CommentDO> selectRootCommentsByArticleId(Long current, Long size, Long articleId) {
        Page<CommentDO> page = new Page<>(current, size);
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getArticleId, articleId)
                .isNull(CommentDO::getParentId)
                .eq(CommentDO::getIsDeleted, false)
                .orderByDesc(CommentDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    default List<CommentDO> selectRepliesByParentIds(List<Long> parentIds) {
        return selectList(Wrappers.<CommentDO>lambdaQuery()
                .in(CommentDO::getParentId, parentIds)
                .eq(CommentDO::getIsDeleted, false)
                .orderByAsc(CommentDO::getCreateTime));
    }

    default Long selectCommentCountByArticleId(Long articleId) {
        return selectCount(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getIsDeleted, false));
    }

    default int increaseLikeCount(Long commentId) {
        return update(null, Wrappers.<CommentDO>lambdaUpdate()
                .setSql("like_count = like_count + 1")
                .eq(CommentDO::getId, commentId));
    }

    default int decreaseLikeCount(Long commentId) {
        return update(null, Wrappers.<CommentDO>lambdaUpdate()
                .setSql("like_count = GREATEST(like_count - 1, 0)")
                .eq(CommentDO::getId, commentId));
    }
}
