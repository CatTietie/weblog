package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.role.AssignRolePermissionsReqVO;
import com.quanxiaoha.weblog.admin.model.vo.role.CreateRoleReqVO;
import com.quanxiaoha.weblog.admin.model.vo.role.UpdateRoleReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

public interface RoleService {

    Response findRoleSelectList();

    Response findAllRoles();

    Response findRoleById(Long id);

    Response createRole(CreateRoleReqVO createRoleReqVO);

    Response updateRole(UpdateRoleReqVO updateRoleReqVO);

    Response deleteRole(Long id);

    Response assignPermissions(AssignRolePermissionsReqVO assignRolePermissionsReqVO);

    Response findAllPermissions();
}
