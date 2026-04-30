package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.user.CreateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.ResetPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UserPageListReqVO;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminUserService {
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    Response findUserInfo();

    Response register(RegisterUserReqVO registerUserReqVO);

    Response createUser(CreateUserReqVO createUserReqVO);

    PageResponse findUserPageList(UserPageListReqVO userPageListReqVO);

    Response updateUser(UpdateUserReqVO updateUserReqVO);

    Response deleteUser(Long id);

    Response resetPassword(ResetPasswordReqVO resetPasswordReqVO);

    Response findUserDetail(Long id);
}
