package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

public interface AdminFileService {

    Response uploadFile(MultipartFile file);

    PageResponse findFilePageList(Long current, Long size);

    Response deleteFile(Long id);
}
