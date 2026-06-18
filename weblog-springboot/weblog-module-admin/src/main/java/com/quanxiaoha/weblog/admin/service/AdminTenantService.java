package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.tenant.AddTenantReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.FindTenantPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tenant.UpdateTenantReqVO;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminTenantService {

    Response addTenant(AddTenantReqVO addTenantReqVO);

    Response updateTenant(UpdateTenantReqVO updateTenantReqVO);

    Response deleteTenant(Long id);

    Response toggleTenantStatus(Long id, Integer status);

    PageResponse findTenantPageList(FindTenantPageListReqVO findTenantPageListReqVO);

    Response findTenantDetail(Long id);
}
