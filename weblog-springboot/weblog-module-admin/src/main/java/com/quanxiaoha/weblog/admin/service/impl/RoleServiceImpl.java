package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.model.vo.role.AssignRolePermissionsReqVO;
import com.quanxiaoha.weblog.admin.model.vo.role.CreateRoleReqVO;
import com.quanxiaoha.weblog.admin.model.vo.role.PermissionRspVO;
import com.quanxiaoha.weblog.admin.model.vo.role.RoleDetailRspVO;
import com.quanxiaoha.weblog.admin.model.vo.role.RoleListRspVO;
import com.quanxiaoha.weblog.admin.model.vo.role.RoleRspVO;
import com.quanxiaoha.weblog.admin.model.vo.role.UpdateRoleReqVO;
import com.quanxiaoha.weblog.admin.service.RoleService;
import com.quanxiaoha.weblog.common.domain.dos.PermissionDO;
import com.quanxiaoha.weblog.common.domain.dos.RoleDO;
import com.quanxiaoha.weblog.common.domain.dos.RolePermissionDO;
import com.quanxiaoha.weblog.common.domain.mapper.PermissionMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RoleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RolePermissionMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private UserMapper userMapper;

    private static final Long ADMIN_ROLE_ID = 1L;

    @Override
    public Response findRoleSelectList() {
        List<RoleDO> roleDOS = roleMapper.findAll();

        List<RoleRspVO> vos = null;
        if (!CollectionUtils.isEmpty(roleDOS)) {
            vos = roleDOS.stream()
                    .map(roleDO -> RoleRspVO.builder()
                            .id(roleDO.getId())
                            .name(roleDO.getName())
                            .code(roleDO.getCode())
                            .description(roleDO.getDescription())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    @Override
    public Response findAllRoles() {
        List<RoleDO> roleDOS = roleMapper.findAll();

        List<RoleListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(roleDOS)) {
            vos = roleDOS.stream()
                    .map(roleDO -> {
                        // 查询角色拥有的权限数量
                        List<RolePermissionDO> rolePermissions = rolePermissionMapper.findByRoleId(roleDO.getId());
                        
                        return RoleListRspVO.builder()
                                .id(roleDO.getId())
                                .name(roleDO.getName())
                                .code(roleDO.getCode())
                                .description(roleDO.getDescription())
                                .sort(roleDO.getSort())
                                .permissionCount(rolePermissions.size())
                                .createTime(roleDO.getCreateTime())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    @Override
    public Response findRoleById(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        if (Objects.isNull(roleDO)) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 查询角色拥有的权限
        List<RolePermissionDO> rolePermissions = rolePermissionMapper.findByRoleId(id);
        List<Long> permissionIds = new ArrayList<>();
        List<PermissionRspVO> permissions = new ArrayList<>();

        if (!CollectionUtils.isEmpty(rolePermissions)) {
            permissionIds = rolePermissions.stream()
                    .map(RolePermissionDO::getPermissionId)
                    .collect(Collectors.toList());
            
            // 查询权限详情
            List<PermissionDO> permissionDOS = permissionMapper.selectBatchIds(permissionIds);
            if (!CollectionUtils.isEmpty(permissionDOS)) {
                permissions = permissionDOS.stream()
                        .map(permissionDO -> PermissionRspVO.builder()
                                .id(permissionDO.getId())
                                .name(permissionDO.getName())
                                .code(permissionDO.getCode())
                                .type(permissionDO.getType())
                                .path(permissionDO.getPath())
                                .method(permissionDO.getMethod())
                                .parentId(permissionDO.getParentId())
                                .sort(permissionDO.getSort())
                                .description(permissionDO.getDescription())
                                .build())
                        .collect(Collectors.toList());
            }
        }

        RoleDetailRspVO vo = RoleDetailRspVO.builder()
                .id(roleDO.getId())
                .name(roleDO.getName())
                .code(roleDO.getCode())
                .description(roleDO.getDescription())
                .sort(roleDO.getSort())
                .permissionIds(permissionIds)
                .permissions(permissions)
                .build();

        return Response.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createRole(CreateRoleReqVO createRoleReqVO) {
        String name = createRoleReqVO.getName();
        String code = createRoleReqVO.getCode();
        String description = createRoleReqVO.getDescription();
        Integer sort = createRoleReqVO.getSort();

        // 校验角色编码是否已存在
        RoleDO existRole = roleMapper.findByCode(code);
        if (Objects.nonNull(existRole)) {
            return Response.fail("角色编码已存在");
        }

        // 创建角色
        RoleDO roleDO = RoleDO.builder()
                .name(name)
                .code(code)
                .description(description)
                .sort(sort != null ? sort : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        roleMapper.insert(roleDO);

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateRole(UpdateRoleReqVO updateRoleReqVO) {
        Long id = updateRoleReqVO.getId();
        String name = updateRoleReqVO.getName();
        String description = updateRoleReqVO.getDescription();
        Integer sort = updateRoleReqVO.getSort();

        // 检查角色是否存在
        RoleDO roleDO = roleMapper.selectById(id);
        if (Objects.isNull(roleDO)) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 禁止修改管理员角色编码
        if (ADMIN_ROLE_ID.equals(id)) {
            // 管理员角色不允许修改编码（但可以修改名称、描述等）
        }

        // 更新角色
        RoleDO updateDO = RoleDO.builder().id(id).build();
        if (Objects.nonNull(name)) {
            updateDO.setName(name);
        }
        if (Objects.nonNull(description)) {
            updateDO.setDescription(description);
        }
        if (Objects.nonNull(sort)) {
            updateDO.setSort(sort);
        }
        updateDO.setUpdateTime(LocalDateTime.now());

        roleMapper.updateById(updateDO);

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteRole(Long id) {
        // 检查角色是否存在
        RoleDO roleDO = roleMapper.selectById(id);
        if (Objects.isNull(roleDO)) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 禁止删除管理员角色
        if (ADMIN_ROLE_ID.equals(id)) {
            return Response.fail("不能删除管理员角色");
        }

        // 检查是否有用户使用该角色
        Long userCount = userMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.quanxiaoha.weblog.common.domain.dos.UserDO>()
                        .eq(com.quanxiaoha.weblog.common.domain.dos.UserDO::getRoleId, id));
        
        if (userCount > 0) {
            return Response.fail("该角色下存在用户，无法删除");
        }

        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(id);

        // 删除角色
        roleMapper.deleteById(id);

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response assignPermissions(AssignRolePermissionsReqVO assignRolePermissionsReqVO) {
        Long roleId = assignRolePermissionsReqVO.getRoleId();
        List<Long> permissionIds = assignRolePermissionsReqVO.getPermissionIds();

        // 检查角色是否存在
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        // 删除旧的角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);

        // 插入新的角色权限关联
        if (!CollectionUtils.isEmpty(permissionIds)) {
            for (Long permissionId : permissionIds) {
                // 检查权限是否存在
                PermissionDO permissionDO = permissionMapper.selectById(permissionId);
                if (Objects.isNull(permissionDO)) {
                    continue; // 跳过不存在的权限
                }

                RolePermissionDO rolePermissionDO = RolePermissionDO.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .createTime(LocalDateTime.now())
                        .build();

                rolePermissionMapper.insert(rolePermissionDO);
            }
        }

        return Response.success();
    }

    @Override
    public Response findAllPermissions() {
        List<PermissionDO> permissionDOS = permissionMapper.findAll();

        List<PermissionRspVO> vos = null;
        if (!CollectionUtils.isEmpty(permissionDOS)) {
            vos = permissionDOS.stream()
                    .map(permissionDO -> PermissionRspVO.builder()
                            .id(permissionDO.getId())
                            .name(permissionDO.getName())
                            .code(permissionDO.getCode())
                            .type(permissionDO.getType())
                            .path(permissionDO.getPath())
                            .method(permissionDO.getMethod())
                            .parentId(permissionDO.getParentId())
                            .sort(permissionDO.getSort())
                            .description(permissionDO.getDescription())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }
}
