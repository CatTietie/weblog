package com.quanxiaoha.weblog.jwt.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.quanxiaoha.weblog.common.domain.dos.PermissionDO;
import com.quanxiaoha.weblog.common.domain.dos.RolePermissionDO;
import com.quanxiaoha.weblog.common.domain.mapper.PermissionMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RolePermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionCacheService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    private final Cache<Long, List<String>> rolePermissionCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final Cache<String, List<String>> allPermissionCache = Caffeine.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public List<String> getPermissionCodesByRoleId(Long roleId) {
        return rolePermissionCache.get(roleId, this::loadPermissionCodes);
    }

    public List<String> getAllPermissionCodes() {
        return allPermissionCache.get("ALL", k -> {
            List<PermissionDO> allPermissions = permissionMapper.findAll();
            if (CollectionUtils.isEmpty(allPermissions)) {
                return Collections.emptyList();
            }
            return allPermissions.stream()
                    .map(PermissionDO::getCode)
                    .collect(Collectors.toList());
        });
    }

    public void evictByRoleId(Long roleId) {
        rolePermissionCache.invalidate(roleId);
        log.info("已清除角色权限缓存: roleId={}", roleId);
    }

    public void evictAll() {
        rolePermissionCache.invalidateAll();
        allPermissionCache.invalidateAll();
        log.info("已清除所有权限缓存");
    }

    private List<String> loadPermissionCodes(Long roleId) {
        List<RolePermissionDO> rolePermissions = rolePermissionMapper.findByRoleId(roleId);
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return Collections.emptyList();
        }

        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermissionDO::getPermissionId)
                .collect(Collectors.toList());

        List<PermissionDO> permissionDOS = permissionMapper.selectBatchIds(permissionIds);
        if (CollectionUtils.isEmpty(permissionDOS)) {
            return Collections.emptyList();
        }

        return permissionDOS.stream()
                .map(PermissionDO::getCode)
                .collect(Collectors.toList());
    }
}
