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
import com.quanxiaoha.weblog.common.domain.dos.RoleDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserRoleDO;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingsMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RoleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserRoleMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
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
import java.util.ArrayList;
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
    @Autowired
    private RoleMapper roleMapper;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final Long ADMIN_ROLE_ID = 1L;

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

        // 查询用户信息
        UserDO userDO = userMapper.findByUsername(username);
        
        List<String> roles = new ArrayList<>();
        
        // 优先通过 roleId 从角色表获取角色
        if (Objects.nonNull(userDO) && Objects.nonNull(userDO.getRoleId())) {
            RoleDO roleDO = roleMapper.selectById(userDO.getRoleId());
            if (Objects.nonNull(roleDO)) {
                roles.add(roleDO.getCode());
            }
        }
        
        // 如果没有 roleId，则从 t_user_role 表获取（兼容旧数据）
        if (CollectionUtils.isEmpty(roles)) {
            List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(username);
            if (!CollectionUtils.isEmpty(roleDOS)) {
                roles = roleDOS.stream().map(UserRoleDO::getRole).collect(Collectors.toList());
            }
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
                .status(UserStatusEnum.ENABLED.getCode())
                .roleId(ADMIN_ROLE_ID)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        userMapper.insert(userDO);

        // 同时插入 t_user_role 表（兼容旧逻辑）
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
        Long roleId = createUserReqVO.getRoleId();
        Integer status = createUserReqVO.getStatus();

        // 校验角色是否存在
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
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
                .roleId(roleId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        userMapper.insert(userDO);

        // 同时插入 t_user_role 表（兼容旧逻辑）
        UserRoleDO userRoleDO = UserRoleDO.builder()
                .username(username)
                .role(roleDO.getCode())
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
                        String roleCode = null;
                        String roleName = null;
                        
                        // 优先通过 roleId 从角色表获取角色
                        if (Objects.nonNull(userDO.getRoleId())) {
                            RoleDO roleDO = roleMapper.selectById(userDO.getRoleId());
                            if (Objects.nonNull(roleDO)) {
                                roleCode = roleDO.getCode();
                                roleName = roleDO.getName();
                            }
                        }
                        
                        // 如果没有 roleId，则从 t_user_role 表获取（兼容旧数据）
                        if (Objects.isNull(roleCode)) {
                            List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(userDO.getUsername());
                            if (!CollectionUtils.isEmpty(roleDOS)) {
                                roleCode = roleDOS.get(0).getRole();
                                // 从角色表获取角色名称
                                RoleDO roleDO = roleMapper.findByCode(roleCode);
                                if (Objects.nonNull(roleDO)) {
                                    roleName = roleDO.getName();
                                } else {
                                    roleName = roleCode;
                                }
                            }
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
                                .role(roleCode)
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
