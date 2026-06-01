package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.CommentDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.CommentMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.comment.*;
import com.quanxiaoha.weblog.web.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response findCommentList(FindCommentListReqVO findCommentListReqVO) {
        Long articleId = findCommentListReqVO.getArticleId();
        Long current = findCommentListReqVO.getCurrent();
        Long size = findCommentListReqVO.getSize();

        Page<CommentDO> rootPage = commentMapper.selectRootCommentsByArticleId(current, size, articleId);
        List<CommentDO> rootComments = rootPage.getRecords();

        if (CollectionUtils.isEmpty(rootComments)) {
            Long totalCount = commentMapper.selectCommentCountByArticleId(articleId);
            PageResponse<FindCommentListRspVO> response = PageResponse.success(rootPage, Collections.emptyList());
            response.setTotal(totalCount);
            return response;
        }

        List<Long> rootIds = rootComments.stream().map(CommentDO::getId).collect(Collectors.toList());
        List<CommentDO> replies = commentMapper.selectRepliesByParentIds(rootIds);

        Set<Long> userIds = new HashSet<>();
        rootComments.forEach(c -> userIds.add(c.getUserId()));
        replies.forEach(c -> {
            userIds.add(c.getUserId());
            if (c.getReplyToUserId() != null) {
                userIds.add(c.getReplyToUserId());
            }
        });

        List<UserDO> users = userMapper.selectBatchIds(userIds);
        Map<Long, String> userIdNameMap = users.stream()
                .collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));

        Map<Long, List<CommentDO>> repliesGrouped = replies.stream()
                .collect(Collectors.groupingBy(CommentDO::getParentId));

        List<FindCommentListRspVO> voList = rootComments.stream().map(root -> {
            List<CommentDO> childList = repliesGrouped.getOrDefault(root.getId(), Collections.emptyList());
            List<FindCommentReplyRspVO> replyVOs = childList.stream().map(reply ->
                    FindCommentReplyRspVO.builder()
                            .id(reply.getId())
                            .content(reply.getContent())
                            .createTime(reply.getCreateTime())
                            .username(userIdNameMap.getOrDefault(reply.getUserId(), "未知用户"))
                            .replyToUsername(reply.getReplyToUserId() != null
                                    ? userIdNameMap.getOrDefault(reply.getReplyToUserId(), "未知用户")
                                    : null)
                            .likeCount(reply.getLikeCount())
                            .build()
            ).collect(Collectors.toList());

            return FindCommentListRspVO.builder()
                    .id(root.getId())
                    .content(root.getContent())
                    .createTime(root.getCreateTime())
                    .username(userIdNameMap.getOrDefault(root.getUserId(), "未知用户"))
                    .likeCount(root.getLikeCount())
                    .replies(replyVOs)
                    .build();
        }).collect(Collectors.toList());

        Long totalCount = commentMapper.selectCommentCountByArticleId(articleId);
        PageResponse<FindCommentListRspVO> response = PageResponse.success(rootPage, voList);
        response.setTotal(totalCount);
        return response;
    }

    @Override
    public Response publishComment(PublishCommentReqVO publishCommentReqVO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BizException(ResponseCodeEnum.COMMENT_LOGIN_REQUIRED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserDO userDO = userMapper.findByUsername(username);

        Long parentId = publishCommentReqVO.getParentId();
        Long replyToUserId = null;

        if (parentId != null) {
            CommentDO parentComment = commentMapper.selectById(parentId);
            if (parentComment == null || parentComment.getIsDeleted()) {
                throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
            }
            if (parentComment.getParentId() != null) {
                throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
            }
            replyToUserId = parentComment.getUserId();
        }

        CommentDO commentDO = CommentDO.builder()
                .articleId(publishCommentReqVO.getArticleId())
                .userId(userDO.getId())
                .content(publishCommentReqVO.getContent())
                .parentId(parentId)
                .replyToUserId(replyToUserId)
                .likeCount(0)
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .build();

        commentMapper.insert(commentDO);
        return Response.success();
    }

    @Override
    public Response likeComment(LikeCommentReqVO likeCommentReqVO) {
        Long commentId = likeCommentReqVO.getCommentId();
        Boolean liked = likeCommentReqVO.getLiked();

        if (liked) {
            commentMapper.increaseLikeCount(commentId);
        } else {
            commentMapper.decreaseLikeCount(commentId);
        }

        return Response.success();
    }
}
