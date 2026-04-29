package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.user.CreateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.FindUserInfoRspVO;
import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UserPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UserRspVO;
import com.quanxiaoha.weblog.admin.service.AdminUserService;
import com.quanxiaoha.weblog.common.domain.dos.BlogSettingsDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserRoleDO;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingsMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserRoleMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.enums.UserRoleEnum;
import com.quanxiaoha.weblog.common.enums.UserStatusEnum;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();

        String encodePassword = passwordEncoder.encode(password);

        int count = userMapper.updatePasswordByUsername(username, encodePassword);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    @Override
    public Response findUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 查询用户角色
        List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(username);
        List<String> roles = null;
        if (!CollectionUtils.isEmpty(roleDOS)) {
            roles = roleDOS.stream().map(UserRoleDO::getRole).collect(Collectors.toList());
        }

        return Response.success(FindUserInfoRspVO.builder()
                .username(username)
                .roles(roles)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response register(RegisterUserReqVO registerUserReqVO) {
        String username = registerUserReqVO.getUsername();
        String password = registerUserReqVO.getPassword();

        UserDO existUser = userMapper.findByUsername(username);
        if (Objects.nonNull(existUser)) {
            return Response.fail(ResponseCodeEnum.USERNAME_IS_EXISTED);
        }

        String encodePassword = passwordEncoder.encode(password);

        UserDO userDO = UserDO.builder()
                .username(username)
                .password(encodePassword)
                .status(UserStatusEnum.ENABLED.getCode()) // 注册用户默认启用
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        userMapper.insert(userDO);

        UserRoleDO userRoleDO = UserRoleDO.builder()
                .username(username)
                .role(ROLE_ADMIN)
                .createTime(new Date())
                .build();
        userRoleMapper.insert(userRoleDO);

        BlogSettingsDO blogSettingsDO = BlogSettingsDO.builder()
                .username(username)
                .author(username)
                .logo("")
                .name("")
                .introduction("")
                .avatar("")
                .githubHomepage("")
                .csdnHomepage("")
                .giteeHomepage("")
                .zhihuHomepage("")
                .build();
        blogSettingsMapper.insert(blogSettingsDO);

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createUser(CreateUserReqVO createUserReqVO) {
        String username = createUserReqVO.getUsername();
        String password = createUserReqVO.getPassword();
        String role = createUserReqVO.getRole();
        Integer status = createUserReqVO.getStatus();

        // 校验角色是否合法
        if (!ROLE_ADMIN.equals(role) && !UserRoleEnum.ROLE_USER.getCode().equals(role)) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 校验状态是否合法
        if (!UserStatusEnum.ENABLED.getCode().equals(status) && !UserStatusEnum.DISABLED.getCode().equals(status)) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 检查用户名是否已存在
        UserDO existUser = userMapper.findByUsername(username);
        if (Objects.nonNull(existUser)) {
            return Response.fail(ResponseCodeEnum.USERNAME_IS_EXISTED);
        }

        String encodePassword = passwordEncoder.encode(password);

        // 创建用户
        UserDO userDO = UserDO.builder()
                .username(username)
                .password(encodePassword)
                .status(status)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        userMapper.insert(userDO);

        // 创建用户角色
        UserRoleDO userRoleDO = UserRoleDO.builder()
                .username(username)
                .role(role)
                .createTime(new Date())
                .build();
        userRoleMapper.insert(userRoleDO);

        // 为新用户创建博客设置
        BlogSettingsDO blogSettingsDO = BlogSettingsDO.builder()
                .username(username)
                .author(username)
                .logo("")
                .name("")
                .introduction("")
                .avatar("")
                .githubHomepage("")
                .csdnHomepage("")
                .giteeHomepage("")
                .zhihuHomepage("")
                .build();
        blogSettingsMapper.insert(blogSettingsDO);

        return Response.success();
    }

    @Override
    public PageResponse findUserPageList(UserPageListReqVO userPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = userPageListReqVO.getCurrent();
        Long size = userPageListReqVO.getSize();
        String username = userPageListReqVO.getUsername();

        // 执行分页查询
        Page<UserDO> userDOPage = userMapper.selectPageList(current, size, username);

        List<UserDO> userDOS = userDOPage.getRecords();

        // DO 转 VO
        List<UserRspVO> vos = null;
        if (!CollectionUtils.isEmpty(userDOS)) {
            vos = userDOS.stream()
                    .map(userDO -> {
                        // 查询用户角色
                        List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(userDO.getUsername());
                        String role = null;
                        String roleName = null;
                        if (!CollectionUtils.isEmpty(roleDOS)) {
                            role = roleDOS.get(0).getRole();
                            UserRoleEnum roleEnum = UserRoleEnum.valueOfCode(role);
                            roleName = Objects.nonNull(roleEnum) ? roleEnum.getDescription() : role;
                        }

                        // 转换状态
                        Integer status = userDO.getStatus();
                        String statusName = "未知";
                        if (Objects.nonNull(status)) {
                            UserStatusEnum statusEnum = UserStatusEnum.valueOf(status);
                            statusName = Objects.nonNull(statusEnum) ? statusEnum.getDescription() : "未知";
                        }

                        return UserRspVO.builder()
                                .id(userDO.getId())
                                .username(userDO.getUsername())
                                .role(role)
                                .roleName(roleName)
                                .status(status)
                                .statusName(statusName)
                                .createTime(userDO.getCreateTime())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PageResponse.success(userDOPage, vos);
    }
}
