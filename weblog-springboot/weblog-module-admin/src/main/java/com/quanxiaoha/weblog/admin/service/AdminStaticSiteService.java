package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.staticsite.FindGenTaskPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.staticsite.TriggerGenerationReqVO;
import com.quanxiaoha.weblog.admin.model.vo.staticsite.UpdateStaticSiteConfigReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

import javax.servlet.http.HttpServletResponse;

public interface AdminStaticSiteService {

    Response updateConfig(UpdateStaticSiteConfigReqVO vo);

    Response findConfigDetail();

    Response triggerGeneration(TriggerGenerationReqVO vo);

    Response triggerDeploy();

    void downloadZip(HttpServletResponse response);

    Response findTaskPageList(FindGenTaskPageListReqVO vo);

    Response findTaskProgress();
}
