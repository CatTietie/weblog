package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.role.AssignRolePermissionsReqVO;
import com.quanxiaoha.weblog.admin.model.vo.role.CreateRoleReqVO;
import com.quanxiaoha.weblog.admin.model.vo.role.UpdateRoleReqVO;
import com.quanxiaoha.weblog.admin.service.RoleService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 角色模块")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/role/select/list")
    @ApiOperation(value = "角色 Select 下拉列表数据获取")
    @ApiOperationLog(description = "角色 Select 下拉列表数据获取")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findRoleSelectList() {
        return roleService.findRoleSelectList();
    }

    @GetMapping("/roles")
    @ApiOperation(value = "获取所有角色列表")
    @ApiOperationLog(description = "获取所有角色列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findAllRoles() {
        return roleService.findAllRoles();
    }

    @GetMapping("/roles/{id}")
    @ApiOperation(value = "获取角色详情")
    @ApiOperationLog(description = "获取角色详情")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findRoleById(@PathVariable("id") Long id) {
        return roleService.findRoleById(id);
    }

    @PostMapping("/roles")
    @ApiOperation(value = "创建角色")
    @ApiOperationLog(description = "创建角色")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response createRole(@RequestBody @Validated CreateRoleReqVO createRoleReqVO) {
        return roleService.createRole(createRoleReqVO);
    }

    @PutMapping("/roles/{id}")
    @ApiOperation(value = "更新角色")
    @ApiOperationLog(description = "更新角色")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateRole(@PathVariable("id") Long id, @RequestBody @Validated UpdateRoleReqVO updateRoleReqVO) {
        updateRoleReqVO.setId(id);
        return roleService.updateRole(updateRoleReqVO);
    }

    @DeleteMapping("/roles/{id}")
    @ApiOperation(value = "删除角色")
    @ApiOperationLog(description = "删除角色")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteRole(@PathVariable("id") Long id) {
        return roleService.deleteRole(id);
    }

    @PostMapping("/roles/assign-permissions")
    @ApiOperation(value = "分配角色权限")
    @ApiOperationLog(description = "分配角色权限")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response assignPermissions(@RequestBody @Validated AssignRolePermissionsReqVO assignRolePermissionsReqVO) {
        return roleService.assignPermissions(assignRolePermissionsReqVO);
    }

    @GetMapping("/permissions")
    @ApiOperation(value = "获取所有权限列表")
    @ApiOperationLog(description = "获取所有权限列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findAllPermissions() {
        return roleService.findAllPermissions();
    }
}
