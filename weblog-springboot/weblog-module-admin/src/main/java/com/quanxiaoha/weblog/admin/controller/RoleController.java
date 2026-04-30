package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.service.RoleService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/role")
@Api(tags = "Admin 角色模块")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/select/list")
    @ApiOperation(value = "角色 Select 下拉列表数据获取")
    @ApiOperationLog(description = "角色 Select 下拉列表数据获取")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findRoleSelectList() {
        return roleService.findRoleSelectList();
    }
}
