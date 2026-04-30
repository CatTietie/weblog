package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.model.vo.role.RoleRspVO;
import com.quanxiaoha.weblog.admin.service.RoleService;
import com.quanxiaoha.weblog.common.domain.dos.RoleDO;
import com.quanxiaoha.weblog.common.domain.mapper.RoleMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

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
}
