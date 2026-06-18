package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.AddResumeApplicationReqVO;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.DeleteResumeApplicationReqVO;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.FindResumeApplicationPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.UpdateResumeApplicationReqVO;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminResumeApplicationService {

    Response addApplication(AddResumeApplicationReqVO vo);

    PageResponse findApplicationPageList(FindResumeApplicationPageListReqVO vo);

    Response updateApplication(UpdateResumeApplicationReqVO vo);

    Response deleteApplication(DeleteResumeApplicationReqVO vo);

    Response getApplicationStatistics();
}
