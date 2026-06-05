package com.quanxiaoha.weblog.jwt.handler;

import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.jwt.exception.UsernameOrPasswordNullException;
import com.quanxiaoha.weblog.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("AuthenticationException: ", exception);

        if (exception instanceof UsernameOrPasswordNullException) {
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
        } else if (exception instanceof BadCredentialsException) {
            ResultUtil.fail(response, Response.fail(ResponseCodeEnum.USERNAME_OR_PWD_ERROR));
        } else if (exception instanceof UsernameNotFoundException) {
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
        } else if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof UsernameNotFoundException) {
                ResultUtil.fail(response, Response.fail(cause.getMessage()));
            } else {
                if (cause != null) {
                    log.error("InternalAuthenticationServiceException cause: ", cause);
                }
                ResultUtil.fail(response, Response.fail(ResponseCodeEnum.LOGIN_FAIL));
            }
        } else {
            ResultUtil.fail(response, Response.fail(ResponseCodeEnum.LOGIN_FAIL));
        }
    }
}
