package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.comment.BatchUpdateCommentStatusReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminCommentService {

    Response findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO);

    Response deleteComment(DeleteCommentReqVO deleteCommentReqVO);

    Response batchUpdateStatus(BatchUpdateCommentStatusReqVO batchUpdateCommentStatusReqVO);
}
