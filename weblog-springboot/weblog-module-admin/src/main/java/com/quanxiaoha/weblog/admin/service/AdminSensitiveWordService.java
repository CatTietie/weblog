package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.sensitiveword.*;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminSensitiveWordService {

    Response addSensitiveWord(AddSensitiveWordReqVO addSensitiveWordReqVO);

    Response deleteSensitiveWord(Long id);

    Response findSensitiveWordPageList(FindSensitiveWordPageListReqVO findSensitiveWordPageListReqVO);

    Response batchImport(BatchImportSensitiveWordReqVO batchImportSensitiveWordReqVO);

    Response startScan();

    Response getScanTaskList();

    Response getScanTaskProgress(Long taskId);

    Response findScanResultPageList(FindScanResultPageListReqVO findScanResultPageListReqVO);

    Response handleScanResult(HandleScanResultReqVO handleScanResultReqVO);
}
