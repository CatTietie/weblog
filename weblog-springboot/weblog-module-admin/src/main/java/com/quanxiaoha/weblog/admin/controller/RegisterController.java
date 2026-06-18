package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.service.AdminUserService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "用户注册模块")
public class RegisterController {

    @Autowired
    private AdminUserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    @ApiOperationLog(description = "用户注册")
    public Response register(@RequestBody @Validated RegisterUserReqVO registerUserReqVO) {
        return userService.register(registerUserReqVO);
    }
}
