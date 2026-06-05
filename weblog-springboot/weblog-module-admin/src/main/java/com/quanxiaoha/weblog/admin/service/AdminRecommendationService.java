package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.recommendation.AddRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.DeleteRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.UpdateRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.model.vo.recommendation.UpdateUserProfileReqVO;
import com.quanxiaoha.weblog.common.utils.Response;

public interface AdminRecommendationService {

    Response findDashboard();

    Response findConfigList();

    Response addConfig(AddRecommendConfigReqVO addRecommendConfigReqVO);

    Response updateConfig(UpdateRecommendConfigReqVO updateRecommendConfigReqVO);

    Response deleteConfig(DeleteRecommendConfigReqVO deleteRecommendConfigReqVO);

    Response findProfileList(Long current, Long size);

    Response updateProfile(UpdateUserProfileReqVO updateUserProfileReqVO);
}
