package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.article.*;
import com.quanxiaoha.weblog.admin.service.AdminArticleService;
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

/**
 * @author: Group 5
 * @date: 2024-09-15 14:01
 * @description: 文章模块
 **/
@RestController
@RequestMapping("/admin/article")
@Api(tags = "Admin 文章模块")
public class AdminArticleController {

    @Autowired
    private AdminArticleService articleService;

    @PostMapping("/publish")
    @ApiOperation(value = "文章发布")
    @ApiOperationLog(description = "文章发布")
    @PreAuthorize("hasAuthority('article:publish')")
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "文章删除")
    @ApiOperationLog(description = "文章删除")
    @PreAuthorize("hasAuthority('article:delete')")
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询文章分页数据")
    @ApiOperationLog(description = "查询文章分页数据")
    public Response findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return articleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "查询文章详情")
    @ApiOperationLog(description = "查询文章详情")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新文章")
    @ApiOperationLog(description = "更新文章")
    @PreAuthorize("hasAuthority('article:update')")
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/status")
    @ApiOperation(value = "修改文章状态")
    @ApiOperationLog(description = "修改文章状态")
    @PreAuthorize("hasAuthority('article:update')")
    public Response changeArticleStatus(@RequestBody @Validated ChangeArticleStatusReqVO changeArticleStatusReqVO) {
        return articleService.changeArticleStatus(changeArticleStatusReqVO);
    }

    @PostMapping("/version/list")
    @ApiOperation(value = "查询文章版本列表")
    @ApiOperationLog(description = "查询文章版本列表")
    public Response findArticleVersionList(@RequestBody @Validated FindArticleVersionListReqVO findArticleVersionListReqVO) {
        return articleService.findArticleVersionList(findArticleVersionListReqVO);
    }

    @PostMapping("/version/detail")
    @ApiOperation(value = "查询文章版本详情")
    @ApiOperationLog(description = "查询文章版本详情")
    public Response findArticleVersionDetail(@RequestBody @Validated FindArticleVersionDetailReqVO findArticleVersionDetailReqVO) {
        return articleService.findArticleVersionDetail(findArticleVersionDetailReqVO);
    }

}
