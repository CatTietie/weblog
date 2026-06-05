package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.NotificationDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.NotificationMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.notification.FindNotificationPageListReqVO;
import com.quanxiaoha.weblog.web.model.vo.notification.FindNotificationPageListRspVO;
import com.quanxiaoha.weblog.web.model.vo.notification.ReadNotificationReqVO;
import com.quanxiaoha.weblog.web.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response getUnreadCount() {
        Long userId = getCurrentUserId();
        Long count = notificationMapper.selectUnreadCount(userId);
        return Response.success(count);
    }

    @Override
    public Response findNotificationList(FindNotificationPageListReqVO reqVO) {
        Long userId = getCurrentUserId();
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        Page<NotificationDO> page = notificationMapper.selectPageByReceiverId(current, size, userId);

        List<FindNotificationPageListRspVO> voList = page.getRecords().stream()
                .map(n -> FindNotificationPageListRspVO.builder()
                        .id(n.getId())
                        .type(n.getType())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .articleId(n.getArticleId())
                        .commentId(n.getCommentId())
                        .isRead(n.getIsRead())
                        .createTime(n.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(page, voList);
    }

    @Override
    public Response readNotification(ReadNotificationReqVO reqVO) {
        Long userId = getCurrentUserId();
        NotificationDO notification = notificationMapper.selectById(reqVO.getId());

        if (notification == null || !notification.getReceiverId().equals(userId)) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notificationMapper.updateById(notification);
        }

        return Response.success();
    }

    @Override
    public Response readAllNotifications() {
        Long userId = getCurrentUserId();
        notificationMapper.markAllAsRead(userId);
        return Response.success();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BizException(ResponseCodeEnum.COMMENT_LOGIN_REQUIRED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserDO userDO = userMapper.findByUsername(username);
        return userDO.getId();
    }
}
