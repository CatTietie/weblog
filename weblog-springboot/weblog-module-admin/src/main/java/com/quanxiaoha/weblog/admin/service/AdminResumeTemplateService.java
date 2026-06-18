package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.resumetemplate.*;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminResumeTemplateService {

    Response addResumeTemplate(AddResumeTemplateReqVO addResumeTemplateReqVO);

    PageResponse findResumeTemplatePageList(FindResumeTemplatePageListReqVO findResumeTemplatePageListReqVO);

    Response updateResumeTemplate(UpdateResumeTemplateReqVO updateResumeTemplateReqVO);

    Response deleteResumeTemplate(DeleteResumeTemplateReqVO deleteResumeTemplateReqVO);

    Response updateResumeTemplateStatus(UpdateResumeTemplateStatusReqVO updateResumeTemplateStatusReqVO);
}
