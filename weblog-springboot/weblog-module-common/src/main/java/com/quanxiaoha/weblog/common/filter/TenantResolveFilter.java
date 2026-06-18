package com.quanxiaoha.weblog.common.filter;

import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.TenantDO;
import com.quanxiaoha.weblog.common.domain.mapper.TenantMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class TenantResolveFilter implements Filter {

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/admin") || requestURI.startsWith("/ws")) {
            chain.doFilter(request, response);
            return;
        }

        if (TenantContext.getTenantId() != null) {
            chain.doFilter(request, response);
            return;
        }

        String host = request.getHeader("Host");
        if (host != null && host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }

        if (host != null) {
            TenantContext.setIgnore(true);
            try {
                LambdaQueryWrapper<TenantDO> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TenantDO::getDomain, host);
                TenantDO tenant = tenantMapper.selectOne(wrapper);

                if (tenant != null) {
                    if (tenant.getStatus() != null && tenant.getStatus() == 1) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"success\":false,\"errorCode\":\"20027\",\"message\":\"该站点已被禁用，请联系管理员！\"}");
                        return;
                    }
                    TenantContext.setTenantId(tenant.getId());
                } else {
                    TenantContext.setTenantId(0L);
                }
            } finally {
                TenantContext.setIgnore(false);
            }
        } else {
            TenantContext.setTenantId(0L);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
