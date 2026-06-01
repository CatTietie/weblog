package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.comment.FindCommentListReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.LikeCommentReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.PublishCommentReqVO;
import com.quanxiaoha.weblog.web.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/list")
    @ApiOperation(value = "获取文章评论列表")
    @ApiOperationLog(description = "获取文章评论列表")
    public Response findCommentList(@RequestBody @Validated FindCommentListReqVO findCommentListReqVO) {
        return commentService.findCommentList(findCommentListReqVO);
    }

    @PostMapping("/publish")
    @ApiOperation(value = "发布评论")
    @ApiOperationLog(description = "发布评论")
    public Response publishComment(@RequestBody @Validated PublishCommentReqVO publishCommentReqVO) {
        return commentService.publishComment(publishCommentReqVO);
    }

    @PostMapping("/like")
    @ApiOperation(value = "点赞/取消点赞评论")
    @ApiOperationLog(description = "点赞/取消点赞评论")
    public Response likeComment(@RequestBody @Validated LikeCommentReqVO likeCommentReqVO) {
        return commentService.likeComment(likeCommentReqVO);
    }
}
