package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.comment.BatchUpdateCommentStatusReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.quanxiaoha.weblog.admin.service.AdminCommentService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/comment")
@Api(tags = "Admin 评论模块")
public class AdminCommentController {

    @Autowired
    private AdminCommentService adminCommentService;

    @PostMapping("/list")
    @ApiOperation(value = "查询评论分页数据")
    @ApiOperationLog(description = "查询评论分页数据")
    @PreAuthorize("hasAuthority('comment:list')")
    public Response findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO findCommentPageListReqVO) {
        return adminCommentService.findCommentPageList(findCommentPageListReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除评论")
    @ApiOperationLog(description = "删除评论")
    @PreAuthorize("hasAuthority('comment:delete')")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return adminCommentService.deleteComment(deleteCommentReqVO);
    }

    @PostMapping("/status/batch-update")
    @ApiOperation(value = "批量更新评论状态")
    @ApiOperationLog(description = "批量更新评论状态")
    @PreAuthorize("hasAuthority('comment:update')")
    public Response batchUpdateCommentStatus(@RequestBody @Validated BatchUpdateCommentStatusReqVO batchUpdateCommentStatusReqVO) {
        return adminCommentService.batchUpdateStatus(batchUpdateCommentStatusReqVO);
    }
}
