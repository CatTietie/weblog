package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.comment.BatchUpdateCommentStatusReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.FindCommentPageListRspVO;
import com.quanxiaoha.weblog.admin.service.AdminCommentService;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.CommentDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.CommentMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.CommentStatusEnum;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO) {
        Long current = findCommentPageListReqVO.getCurrent();
        Long size = findCommentPageListReqVO.getSize();
        Integer status = findCommentPageListReqVO.getStatus();

        Page<CommentDO> page = commentMapper.selectPageListForAdmin(current, size, status);
        List<CommentDO> records = page.getRecords();

        if (records.isEmpty()) {
            return PageResponse.success(page, List.of());
        }

        Set<Long> userIds = records.stream().map(CommentDO::getUserId).collect(Collectors.toSet());
        Set<Long> articleIds = records.stream().map(CommentDO::getArticleId).collect(Collectors.toSet());

        Map<Long, String> userNameMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));
        Map<Long, String> articleTitleMap = articleMapper.selectBatchIds(articleIds).stream()
                .collect(Collectors.toMap(ArticleDO::getId, ArticleDO::getTitle));

        List<FindCommentPageListRspVO> voList = records.stream().map(comment -> {
            CommentStatusEnum statusEnum = CommentStatusEnum.valueOf(comment.getStatus());
            return FindCommentPageListRspVO.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .username(userNameMap.getOrDefault(comment.getUserId(), "未知用户"))
                    .articleTitle(articleTitleMap.getOrDefault(comment.getArticleId(), "未知文章"))
                    .createTime(comment.getCreateTime())
                    .status(comment.getStatus())
                    .statusName(Objects.nonNull(statusEnum) ? statusEnum.getDescription() : "未知")
                    .build();
        }).collect(Collectors.toList());

        return PageResponse.success(page, voList);
    }

    @Override
    public Response deleteComment(DeleteCommentReqVO deleteCommentReqVO) {
        Long id = deleteCommentReqVO.getId();
        CommentDO comment = commentMapper.selectById(id);
        if (Objects.isNull(comment)) {
            return Response.fail("该评论不存在");
        }
        comment.setIsDeleted(true);
        commentMapper.updateById(comment);
        return Response.success();
    }

    @Override
    public Response batchUpdateStatus(BatchUpdateCommentStatusReqVO batchUpdateCommentStatusReqVO) {
        List<Long> ids = batchUpdateCommentStatusReqVO.getIds();
        Integer status = batchUpdateCommentStatusReqVO.getStatus();

        List<CommentDO> comments = commentMapper.selectBatchIds(ids);
        for (CommentDO comment : comments) {
            comment.setStatus(status);
            commentMapper.updateById(comment);
        }
        return Response.success();
    }
}
