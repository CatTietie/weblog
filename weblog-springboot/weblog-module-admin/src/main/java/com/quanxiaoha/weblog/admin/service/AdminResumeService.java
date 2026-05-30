package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.resume.*;
import com.quanxiaoha.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

public interface AdminResumeService {

    Response findResumeList();

    Response findResumeDetail(FindResumeDetailReqVO findResumeDetailReqVO);

    Response createResume(CreateResumeReqVO createResumeReqVO);

    Response updateResume(UpdateResumeReqVO updateResumeReqVO);

    Response deleteResume(DeleteResumeReqVO deleteResumeReqVO);

    Response uploadResume(MultipartFile file, Long resumeId, String name);
}
