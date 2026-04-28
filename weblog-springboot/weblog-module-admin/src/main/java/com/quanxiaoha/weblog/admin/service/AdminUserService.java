package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminUserService {
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    Response findUserInfo();

    Response register(RegisterUserReqVO registerUserReqVO);
}
