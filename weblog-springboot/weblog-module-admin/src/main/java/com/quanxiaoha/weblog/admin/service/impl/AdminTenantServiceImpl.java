package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.tenant.AddTenantReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.FindTenantPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.FindTenantRspVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.UpdateTenantReqVO;
import com.quanxiaoha.weblog.admin.service.AdminTenantService;
import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.BlogSettingsDO;
import com.quanxiaoha.weblog.common.domain.dos.TenantDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingsMapper;
import com.quanxiaoha.weblog.common.domain.mapper.TenantMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTenantServiceImpl implements AdminTenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addTenant(AddTenantReqVO addTenantReqVO) {
        LambdaQueryWrapper<TenantDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenantDO::getDomain, addTenantReqVO.getDomain());
        TenantDO existTenant = tenantMapper.selectOne(wrapper);
        if (existTenant != null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "域名已被使用");
        }

        TenantDO tenantDO = TenantDO.builder()
                .name(addTenantReqVO.getName())
                .domain(addTenantReqVO.getDomain())
                .logo(addTenantReqVO.getLogo())
                .description(addTenantReqVO.getDescription())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        tenantMapper.insert(tenantDO);

        TenantContext.setTenantId(tenantDO.getId());
        try {
            UserDO adminUser = UserDO.builder()
                    .username(addTenantReqVO.getAdminUsername())
                    .password(passwordEncoder.encode(addTenantReqVO.getAdminPassword()))
                    .status(0)
                    .roleId(1L)
                    .tenantId(tenantDO.getId())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
            userMapper.insert(adminUser);

            tenantDO.setAdminUserId(adminUser.getId());
            tenantMapper.updateById(tenantDO);

            BlogSettingsDO blogSettings = BlogSettingsDO.builder()
                    .name(addTenantReqVO.getName())
                    .logo(addTenantReqVO.getLogo() != null ? addTenantReqVO.getLogo() : "")
                    .author(addTenantReqVO.getAdminUsername())
                    .introduction("")
                    .avatar("")
                    .githubHomepage("")
                    .csdnHomepage("")
                    .giteeHomepage("")
                    .zhihuHomepage("")
                    .tenantId(tenantDO.getId())
                    .build();
            blogSettingsMapper.insert(blogSettings);
        } finally {
            TenantContext.clear();
        }

        return Response.success();
    }

    @Override
    public Response updateTenant(UpdateTenantReqVO updateTenantReqVO) {
        TenantDO tenantDO = tenantMapper.selectById(updateTenantReqVO.getId());
        if (tenantDO == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "租户不存在");
        }

        if (updateTenantReqVO.getName() != null) {
            tenantDO.setName(updateTenantReqVO.getName());
        }
        if (updateTenantReqVO.getDomain() != null) {
            tenantDO.setDomain(updateTenantReqVO.getDomain());
        }
        if (updateTenantReqVO.getLogo() != null) {
            tenantDO.setLogo(updateTenantReqVO.getLogo());
        }
        if (updateTenantReqVO.getDescription() != null) {
            tenantDO.setDescription(updateTenantReqVO.getDescription());
        }
        if (updateTenantReqVO.getStatus() != null) {
            tenantDO.setStatus(updateTenantReqVO.getStatus());
        }
        tenantDO.setUpdateTime(LocalDateTime.now());
        tenantMapper.updateById(tenantDO);

        return Response.success();
    }

    @Override
    public Response deleteTenant(Long id) {
        tenantMapper.deleteById(id);
        return Response.success();
    }

    @Override
    public Response toggleTenantStatus(Long id, Integer status) {
        TenantDO tenantDO = tenantMapper.selectById(id);
        if (tenantDO == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "租户不存在");
        }
        tenantDO.setStatus(status);
        tenantDO.setUpdateTime(LocalDateTime.now());
        tenantMapper.updateById(tenantDO);
        return Response.success();
    }

    @Override
    public PageResponse findTenantPageList(FindTenantPageListReqVO findTenantPageListReqVO) {
        Long current = findTenantPageListReqVO.getCurrent();
        Long size = findTenantPageListReqVO.getSize();

        Page<TenantDO> page = new Page<>(current, size);
        LambdaQueryWrapper<TenantDO> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(findTenantPageListReqVO.getName())) {
            wrapper.like(TenantDO::getName, findTenantPageListReqVO.getName().trim());
        }
        if (Objects.nonNull(findTenantPageListReqVO.getStatus())) {
            wrapper.eq(TenantDO::getStatus, findTenantPageListReqVO.getStatus());
        }
        wrapper.orderByDesc(TenantDO::getCreateTime);

        Page<TenantDO> resultPage = tenantMapper.selectPage(page, wrapper);

        List<FindTenantRspVO> vos = resultPage.getRecords().stream().map(tenant -> {
            FindTenantRspVO vo = FindTenantRspVO.builder()
                    .id(tenant.getId())
                    .name(tenant.getName())
                    .domain(tenant.getDomain())
                    .logo(tenant.getLogo())
                    .description(tenant.getDescription())
                    .status(tenant.getStatus())
                    .createTime(tenant.getCreateTime())
                    .updateTime(tenant.getUpdateTime())
                    .build();

            if (tenant.getAdminUserId() != null) {
                TenantContext.setIgnore(true);
                try {
                    UserDO adminUser = userMapper.selectById(tenant.getAdminUserId());
                    if (adminUser != null) {
                        vo.setAdminUsername(adminUser.getUsername());
                    }
                } finally {
                    TenantContext.setIgnore(false);
                }
            }
            return vo;
        }).collect(Collectors.toList());

        return PageResponse.success(resultPage, vos);
    }

    @Override
    public Response findTenantDetail(Long id) {
        TenantDO tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "租户不存在");
        }

        FindTenantRspVO vo = FindTenantRspVO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .domain(tenant.getDomain())
                .logo(tenant.getLogo())
                .description(tenant.getDescription())
                .status(tenant.getStatus())
                .createTime(tenant.getCreateTime())
                .updateTime(tenant.getUpdateTime())
                .build();

        if (tenant.getAdminUserId() != null) {
            TenantContext.setIgnore(true);
            try {
                UserDO adminUser = userMapper.selectById(tenant.getAdminUserId());
                if (adminUser != null) {
                    vo.setAdminUsername(adminUser.getUsername());
                }
            } finally {
                TenantContext.setIgnore(false);
            }
        }

        return Response.success(vo);
    }
}
