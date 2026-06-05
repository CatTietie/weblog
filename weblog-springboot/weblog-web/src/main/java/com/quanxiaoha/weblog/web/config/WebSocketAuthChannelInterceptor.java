package com.quanxiaoha.weblog.web.config;

import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Map;

@Slf4j
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes != null) {
                String username = (String) sessionAttributes.get("username");
                if (username != null) {
                    TenantContext.setIgnore(true);
                    try {
                        UserDO user = userMapper.findByUsername(username);
                        if (user != null) {
                            accessor.setUser(new StompPrincipal(String.valueOf(user.getId())));
                            sessionAttributes.put("tenantId", user.getTenantId());
                            log.info("==> WebSocket用户连接: username={}, userId={}, tenantId={}", username, user.getId(), user.getTenantId());
                        }
                    } finally {
                        TenantContext.setIgnore(false);
                    }
                }
            }
        }

        return message;
    }
}
