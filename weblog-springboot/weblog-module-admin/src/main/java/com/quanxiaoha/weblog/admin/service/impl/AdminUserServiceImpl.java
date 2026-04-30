package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.user.CreateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.FindUserInfoRspVO;
import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.ResetPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UserPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UserRspVO;
import com.quanxiaoha.weblog.admin.service.AdminUserService;
import com.quanxiaoha.weblog.common.domain.dos.BlogSettingsDO;
import com.quanxiaoha.weblog.common.domain.dos.RoleDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserRoleDO;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingsMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RoleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RolePermissionMapper;
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
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

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
        Integer status = userPageListReqVO.getStatus();
        Long roleId = userPageListReqVO.getRoleId();

        // 执行分页查询
        Page<UserDO> userDOPage = userMapper.selectPageList(current, size, username, status, roleId);

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
                        Integer userStatus = userDO.getStatus();
                        String statusName = "未知";
                        if (Objects.nonNull(userStatus)) {
                            UserStatusEnum statusEnum = UserStatusEnum.valueOf(userStatus);
                            statusName = Objects.nonNull(statusEnum) ? statusEnum.getDescription() : "未知";
                        }

                        return UserRspVO.builder()
                                .id(userDO.getId())
                                .username(userDO.getUsername())
                                .role(roleCode)
                                .roleName(roleName)
                                .status(userStatus)
                                .statusName(statusName)
                                .createTime(userDO.getCreateTime())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PageResponse.success(userDOPage, vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateUser(UpdateUserReqVO updateUserReqVO) {
        Long userId = updateUserReqVO.getId();
        Long roleId = updateUserReqVO.getRoleId();
        Integer status = updateUserReqVO.getStatus();

        // 检查用户是否存在
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }

        // 校验角色是否存在（如果有提供角色）
        RoleDO roleDO = null;
        if (Objects.nonNull(roleId)) {
            roleDO = roleMapper.selectById(roleId);
            if (Objects.isNull(roleDO)) {
                return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
            }
        }

        // 校验状态是否合法（如果有提供状态）
        if (Objects.nonNull(status)) {
            if (!UserStatusEnum.ENABLED.getCode().equals(status) && !UserStatusEnum.DISABLED.getCode().equals(status)) {
                return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
            }
        }

        // 更新用户信息
        UserDO updateDO = UserDO.builder().id(userId).build();
        
        if (Objects.nonNull(roleId)) {
            updateDO.setRoleId(roleId);
        }
        if (Objects.nonNull(status)) {
            updateDO.setStatus(status);
        }
        updateDO.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(updateDO);

        // 同时更新 t_user_role 表（兼容旧逻辑）
        if (Objects.nonNull(roleId) && Objects.nonNull(roleDO)) {
            // 删除旧的角色关联
            List<UserRoleDO> oldRoles = userRoleMapper.selectByUsername(userDO.getUsername());
            if (!CollectionUtils.isEmpty(oldRoles)) {
                for (UserRoleDO oldRole : oldRoles) {
                    userRoleMapper.deleteById(oldRole.getId());
                }
            }
            
            // 插入新的角色关联
            UserRoleDO newUserRole = UserRoleDO.builder()
                    .username(userDO.getUsername())
                    .role(roleDO.getCode())
                    .createTime(new Date())
                    .build();
            userRoleMapper.insert(newUserRole);
        }

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteUser(Long id) {
        // 检查用户是否存在
        UserDO userDO = userMapper.selectById(id);
        if (Objects.isNull(userDO)) {
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }

        // 禁止删除自己（防止管理员误删自己）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (currentUsername.equals(userDO.getUsername())) {
            return Response.fail("不能删除当前登录用户");
        }

        // 禁止删除管理员角色（至少保留一个管理员）
        if (Objects.nonNull(userDO.getRoleId()) && ADMIN_ROLE_ID.equals(userDO.getRoleId())) {
            // 检查是否还有其他管理员
            Long count = userMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserDO>()
                    .eq(UserDO::getRoleId, ADMIN_ROLE_ID)
                    .ne(UserDO::getId, id));
            
            if (count == 0) {
                return Response.fail("至少需要保留一个管理员用户");
            }
        }

        // 删除用户角色关联
        List<UserRoleDO> userRoles = userRoleMapper.selectByUsername(userDO.getUsername());
        if (!CollectionUtils.isEmpty(userRoles)) {
            for (UserRoleDO userRole : userRoles) {
                userRoleMapper.deleteById(userRole.getId());
            }
        }

        // 删除用户
        userMapper.deleteById(id);

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response resetPassword(ResetPasswordReqVO resetPasswordReqVO) {
        Long userId = resetPasswordReqVO.getId();
        String newPassword = resetPasswordReqVO.getNewPassword();

        // 检查用户是否存在
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }

        // 密码长度校验
        if (newPassword.length() < 4) {
            return Response.fail("密码长度不能少于4位");
        }

        // 加密新密码
        String encodePassword = passwordEncoder.encode(newPassword);

        // 更新密码
        int count = userMapper.updatePasswordById(userId, encodePassword);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public Response findUserDetail(Long id) {
        // 检查用户是否存在
        UserDO userDO = userMapper.selectById(id);
        if (Objects.isNull(userDO)) {
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }

        String roleCode = null;
        String roleName = null;
        Long roleId = null;
        
        // 优先通过 roleId 从角色表获取角色
        if (Objects.nonNull(userDO.getRoleId())) {
            roleId = userDO.getRoleId();
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
                    roleId = roleDO.getId();
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

        return Response.success(UserRspVO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .role(roleCode)
                .roleName(roleName)
                .status(status)
                .statusName(statusName)
                .createTime(userDO.getCreateTime())
                .build());
    }
}
