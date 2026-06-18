package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.comment.FindCommentListReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.LikeCommentReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.PublishCommentReqVO;

public interface CommentService {

    Response findCommentList(FindCommentListReqVO findCommentListReqVO);

    Response publishComment(PublishCommentReqVO publishCommentReqVO);

    Response likeComment(LikeCommentReqVO likeCommentReqVO);
}
