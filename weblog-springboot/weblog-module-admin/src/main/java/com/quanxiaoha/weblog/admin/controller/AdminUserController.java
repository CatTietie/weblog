package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.user.CreateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.ResetPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UserPageListReqVO;
import com.quanxiaoha.weblog.admin.service.AdminUserService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.PageResponse;
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

/**
 * @author: Group 5

 * @date: 2023-09-15 14:01
 * @description: 用户
 **/
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 用户模块")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    @PostMapping("/password/update")
    @ApiOperation(value = "修改用户密码")
    @ApiOperationLog(description = "修改用户密码")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return userService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/user/info")
    @ApiOperation(value = "获取用户信息")
    @ApiOperationLog(description = "获取用户信息")
    public Response findUserInfo() {
        return userService.findUserInfo();
    }

    @PostMapping("/users")
    @ApiOperation(value = "管理员创建用户")
    @ApiOperationLog(description = "管理员创建用户")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response createUser(@RequestBody @Validated CreateUserReqVO createUserReqVO) {
        return userService.createUser(createUserReqVO);
    }

    @PostMapping("/users/list")
    @ApiOperation(value = "用户分页列表查询")
    @ApiOperationLog(description = "用户分页列表查询")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PageResponse findUserPageList(@RequestBody @Validated UserPageListReqVO userPageListReqVO) {
        return userService.findUserPageList(userPageListReqVO);
    }

    @GetMapping("/users/{id}")
    @ApiOperation(value = "获取用户详情")
    @ApiOperationLog(description = "获取用户详情")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findUserDetail(@PathVariable("id") Long id) {
        return userService.findUserDetail(id);
    }

    @PutMapping("/users/{id}")
    @ApiOperation(value = "编辑用户")
    @ApiOperationLog(description = "编辑用户")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateUser(@PathVariable("id") Long id, @RequestBody @Validated UpdateUserReqVO updateUserReqVO) {
        updateUserReqVO.setId(id);
        return userService.updateUser(updateUserReqVO);
    }

    @DeleteMapping("/users/{id}")
    @ApiOperation(value = "删除用户")
    @ApiOperationLog(description = "删除用户")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/users/reset-password")
    @ApiOperation(value = "重置用户密码")
    @ApiOperationLog(description = "重置用户密码")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response resetPassword(@RequestBody @Validated ResetPasswordReqVO resetPasswordReqVO) {
        return userService.resetPassword(resetPasswordReqVO);
    }
}
