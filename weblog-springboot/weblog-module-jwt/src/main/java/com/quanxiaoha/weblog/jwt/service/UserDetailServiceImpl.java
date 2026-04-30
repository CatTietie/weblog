package com.quanxiaoha.weblog.jwt.service;

import com.quanxiaoha.weblog.common.domain.dos.RoleDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserRoleDO;
import com.quanxiaoha.weblog.common.domain.mapper.RoleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserRoleMapper;
import com.quanxiaoha.weblog.common.enums.UserStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: Group 5

 * @date: 2023-08-24 9:14
 * @description: TODO
 **/
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询
        UserDO userDO = userMapper.findByUsername(username);

        // 判断用户是否存在
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException("该用户不存在");
        }

        // 检查用户状态是否被禁用
        if (Objects.nonNull(userDO.getStatus()) && UserStatusEnum.DISABLED.getCode().equals(userDO.getStatus())) {
            throw new UsernameNotFoundException("该用户已被禁用，请联系管理员！");
        }

        List<String> roles = new ArrayList<>();
        
        // 优先通过 roleId 从新的角色表获取角色
        if (Objects.nonNull(userDO.getRoleId())) {
            RoleDO roleDO = roleMapper.selectById(userDO.getRoleId());
            if (Objects.nonNull(roleDO)) {
                roles.add(roleDO.getCode());
                log.info("从 roleId 获取角色: userId={}, roleId={}, roleCode={}", 
                    userDO.getId(), userDO.getRoleId(), roleDO.getCode());
            }
        }
        
        // 如果没有 roleId 或者从角色表获取失败，则从 t_user_role 表获取（兼容旧数据）
        if (CollectionUtils.isEmpty(roles)) {
            List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(username);
            if (!CollectionUtils.isEmpty(roleDOS)) {
                roles = roleDOS.stream().map(p -> p.getRole()).collect(Collectors.toList());
                log.info("从 t_user_role 表获取角色: username={}, roles={}", username, roles);
            }
        }

        // 如果用户没有角色，默认给一个空的角色列表，防止 authorities 为 null
        if (CollectionUtils.isEmpty(roles)) {
            log.warn("用户没有找到任何角色: username={}", username);
            // 至少需要一个空的列表，不能为 null
            roles = new ArrayList<>();
        }

        String[] roleArr = roles.toArray(new String[roles.size()]);

        log.info("用户登录: username={}, roles={}", username, roles);

        // authorities 用于指定角色
        return User.withUsername(userDO.getUsername())
                .password(userDO.getPassword())
                .authorities(roleArr)
                .build();
    }
}
