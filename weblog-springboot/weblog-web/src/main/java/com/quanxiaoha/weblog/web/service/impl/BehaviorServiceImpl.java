package com.quanxiaoha.weblog.web.service.impl;

import com.quanxiaoha.weblog.admin.event.UserBehaviorEvent;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendationLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.behavior.ReportBehaviorReqVO;
import com.quanxiaoha.weblog.web.model.vo.behavior.ReportRecommendClickReqVO;
import com.quanxiaoha.weblog.web.service.BehaviorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RecommendationLogMapper recommendationLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response reportBehavior(ReportBehaviorReqVO reportBehaviorReqVO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Response.success();
        }

        List<UserBehaviorEvent.BehaviorItem> items = reportBehaviorReqVO.getEvents().stream()
                .map(e -> new UserBehaviorEvent.BehaviorItem(
                        e.getEventType(),
                        e.getArticleId(),
                        e.getTagId(),
                        e.getKeyword(),
                        e.getDurationSeconds()))
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new UserBehaviorEvent(this, userId, items));
        return Response.success();
    }

    @Override
    public Response reportRecommendClick(ReportRecommendClickReqVO reportRecommendClickReqVO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Response.success();
        }

        recommendationLogMapper.updateClickedByUserAndArticle(
                userId,
                reportRecommendClickReqVO.getArticleId(),
                reportRecommendClickReqVO.getSource());
        return Response.success();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return null;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserDO userDO = userMapper.findByUsername(username);
        return Objects.nonNull(userDO) ? userDO.getId() : null;
    }
}
