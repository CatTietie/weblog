package com.quanxiaoha.weblog.jwt.handler;

import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.jwt.model.LoginRspVO;
import com.quanxiaoha.weblog.jwt.utils.JwtTokenHelper;
import com.quanxiaoha.weblog.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        TenantContext.setIgnore(true);
        try {
            UserDO user = userMapper.findByUsername(username);
            Long tenantId = (user != null && user.getTenantId() != null) ? user.getTenantId() : 0L;
            String token = jwtTokenHelper.generateToken(username, tenantId);
            LoginRspVO loginRspVO = LoginRspVO.builder().token(token).build();
            ResultUtil.ok(response, Response.success(loginRspVO));
        } finally {
            TenantContext.setIgnore(false);
        }
    }
}
