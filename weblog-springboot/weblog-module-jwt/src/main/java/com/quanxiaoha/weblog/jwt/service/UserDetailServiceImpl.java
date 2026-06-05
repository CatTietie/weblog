package com.quanxiaoha.weblog.jwt.service;

import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.RoleDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserRoleDO;
import com.quanxiaoha.weblog.common.domain.mapper.RoleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserRoleMapper;
import com.quanxiaoha.weblog.common.enums.UserStatusEnum;
import com.quanxiaoha.weblog.common.utils.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionCacheService permissionCacheService;

    private static final String ROLE_ADMIN_CODE = "ROLE_ADMIN";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDO userDO = userMapper.findByUsername(username);

        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException(I18nUtil.getMessage("error.20003"));
        }

        if (Objects.nonNull(userDO.getStatus()) && UserStatusEnum.DISABLED.getCode().equals(userDO.getStatus())) {
            throw new UsernameNotFoundException(I18nUtil.getMessage("error.20014"));
        }

        List<String> roles = new ArrayList<>();

        if (Objects.nonNull(userDO.getRoleId())) {
            RoleDO roleDO = roleMapper.selectById(userDO.getRoleId());
            if (Objects.nonNull(roleDO)) {
                roles.add(roleDO.getCode());
            }
        }

        if (CollectionUtils.isEmpty(roles)) {
            List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(username);
            if (!CollectionUtils.isEmpty(roleDOS)) {
                roles = roleDOS.stream().map(UserRoleDO::getRole).collect(Collectors.toList());
            }
        }

        if (CollectionUtils.isEmpty(roles)) {
            roles = new ArrayList<>();
        }

        // 通过缓存加载权限
        List<String> permissionCodes;
        if (roles.contains(ROLE_ADMIN_CODE)) {
            permissionCodes = permissionCacheService.getAllPermissionCodes();
        } else if (Objects.nonNull(userDO.getRoleId())) {
            permissionCodes = permissionCacheService.getPermissionCodesByRoleId(userDO.getRoleId());
        } else {
            permissionCodes = new ArrayList<>();
        }

        List<String> allAuthorities = new ArrayList<>(roles);
        allAuthorities.addAll(permissionCodes);

        String[] authorityArr = allAuthorities.toArray(new String[0]);

        return User.withUsername(userDO.getUsername())
                .password(userDO.getPassword())
                .authorities(authorityArr)
                .build();
    }
}
