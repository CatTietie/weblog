package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.recommendation.AddRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.DeleteRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.UpdateRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.UpdateUserProfileReqVO;
import com.quanxiaoha.weblog.admin.service.AdminRecommendationService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/recommendation")
@Api(tags = "推荐管理")
public class AdminRecommendationController {

    @Autowired
    private AdminRecommendationService adminRecommendationService;

    @PostMapping("/dashboard")
    @ApiOperation(value = "推荐效果看板")
    @ApiOperationLog(description = "推荐效果看板")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findDashboard() {
        return adminRecommendationService.findDashboard();
    }

    @PostMapping("/config/list")
    @ApiOperation(value = "推荐配置列表")
    @ApiOperationLog(description = "推荐配置列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findConfigList() {
        return adminRecommendationService.findConfigList();
    }

    @PostMapping("/config/add")
    @ApiOperation(value = "添加推荐配置")
    @ApiOperationLog(description = "添加推荐配置")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addConfig(@RequestBody @Validated AddRecommendConfigReqVO addRecommendConfigReqVO) {
        return adminRecommendationService.addConfig(addRecommendConfigReqVO);
    }

    @PostMapping("/config/update")
    @ApiOperation(value = "更新推荐配置")
    @ApiOperationLog(description = "更新推荐配置")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateConfig(@RequestBody @Validated UpdateRecommendConfigReqVO updateRecommendConfigReqVO) {
        return adminRecommendationService.updateConfig(updateRecommendConfigReqVO);
    }

    @PostMapping("/config/delete")
    @ApiOperation(value = "删除推荐配置")
    @ApiOperationLog(description = "删除推荐配置")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteConfig(@RequestBody @Validated DeleteRecommendConfigReqVO deleteRecommendConfigReqVO) {
        return adminRecommendationService.deleteConfig(deleteRecommendConfigReqVO);
    }

    @PostMapping("/profile/list")
    @ApiOperation(value = "用户画像列表")
    @ApiOperationLog(description = "用户画像列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findProfileList(@RequestParam(defaultValue = "1") Long current,
                                    @RequestParam(defaultValue = "10") Long size) {
        return adminRecommendationService.findProfileList(current, size);
    }

    @PostMapping("/profile/update")
    @ApiOperation(value = "修正用户画像")
    @ApiOperationLog(description = "修正用户画像")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateProfile(@RequestBody @Validated UpdateUserProfileReqVO updateUserProfileReqVO) {
        return adminRecommendationService.updateProfile(updateUserProfileReqVO);
    }
}
