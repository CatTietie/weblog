package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.tenant.AddTenantReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.FindTenantPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.UpdateTenantReqVO;
import com.quanxiaoha.weblog.admin.service.AdminTenantService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tenant")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminTenantController {

    @Autowired
    private AdminTenantService adminTenantService;

    @PostMapping("/add")
    @ApiOperationLog(description = "新增租户")
    public Response addTenant(@RequestBody @Validated AddTenantReqVO addTenantReqVO) {
        return adminTenantService.addTenant(addTenantReqVO);
    }

    @PostMapping("/update")
    @ApiOperationLog(description = "更新租户")
    public Response updateTenant(@RequestBody @Validated UpdateTenantReqVO updateTenantReqVO) {
        return adminTenantService.updateTenant(updateTenantReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除租户")
    public Response deleteTenant(@RequestParam Long id) {
        return adminTenantService.deleteTenant(id);
    }

    @PostMapping("/toggle-status")
    @ApiOperationLog(description = "启用/禁用租户")
    public Response toggleTenantStatus(@RequestParam Long id, @RequestParam Integer status) {
        return adminTenantService.toggleTenantStatus(id, status);
    }

    @PostMapping("/list")
    @ApiOperationLog(description = "获取租户分页列表")
    public PageResponse findTenantPageList(@RequestBody FindTenantPageListReqVO findTenantPageListReqVO) {
        return adminTenantService.findTenantPageList(findTenantPageListReqVO);
    }

    @GetMapping("/detail")
    @ApiOperationLog(description = "获取租户详情")
    public Response findTenantDetail(@RequestParam Long id) {
        return adminTenantService.findTenantDetail(id);
    }
}
