package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.event.SendGlobalNotificationEvent;
import com.quanxiaoha.weblog.admin.model.vo.notification.SendGlobalNotificationReqVO;
import com.quanxiaoha.weblog.admin.service.AdminNotificationService;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminNotificationServiceImpl implements AdminNotificationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Response sendGlobalNotification(SendGlobalNotificationReqVO reqVO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDO admin = userMapper.findByUsername(userDetails.getUsername());

        eventPublisher.publishEvent(new SendGlobalNotificationEvent(
                this, admin.getId(), reqVO.getTitle(), reqVO.getContent()
        ));

        log.info("==> 管理员发送全局通知, adminId: {}, title: {}", admin.getId(), reqVO.getTitle());
        return Response.success();
    }
}
