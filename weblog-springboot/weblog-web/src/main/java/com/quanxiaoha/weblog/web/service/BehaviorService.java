package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.behavior.ReportBehaviorReqVO;
import com.quanxiaoha.weblog.web.model.vo.behavior.ReportRecommendClickReqVO;

public interface BehaviorService {

    Response reportBehavior(ReportBehaviorReqVO reportBehaviorReqVO);

    Response reportRecommendClick(ReportRecommendClickReqVO reportRecommendClickReqVO);
}
