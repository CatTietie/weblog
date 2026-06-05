package com.quanxiaoha.weblog.jwt.filter;

import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.UserVisitStatsDO;
import com.quanxiaoha.weblog.common.domain.mapper.UserVisitStatsMapper;
import com.quanxiaoha.weblog.jwt.utils.JwtTokenHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author: Group 5
 * @date: 2023-08-27 16:58
 * @description: Token 校验过滤器
 **/
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    //TODO
    @Autowired
    private UserVisitStatsMapper userVisitStatsMapper;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @Value("${jwt.tokenHeaderKey}")
    private String tokenHeaderKey;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // WebSocket路径直接放行，由JwtHandshakeInterceptor处理认证
        if (requestURI.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String deviceType = getDeviceType(request);
        String browserName = getBrowserName(request);
        String osName = getOsName(request);
        boolean newVisitor = isNewVisitor(request, response);

        UserVisitStatsDO userVisitStatsDO = UserVisitStatsDO.builder()
                .browser_name(browserName)
                .device_type(deviceType)
                .user_ip(clientIp)
                .os_name(osName)
                .page_url(requestURI)
                .is_new_visitor(newVisitor)
                .visit_time(LocalDateTime.now())
                .build();
//        userVisitStatsMapper.insert(userVisitStatsDO);

        boolean isAdminPath = requestURI.startsWith("/admin");
        boolean isCommentPath = requestURI.startsWith("/comment");
        boolean isNotificationPath = requestURI.startsWith("/notification");
        boolean isBehaviorPath = requestURI.startsWith("/behavior");
        boolean isRecommendationPath = requestURI.startsWith("/recommendation");

        if (isAdminPath || isCommentPath || isNotificationPath || isBehaviorPath || isRecommendationPath) {
            String header = request.getHeader(tokenHeaderKey);

            if (StringUtils.startsWith(header, tokenPrefix)) {
                String token = StringUtils.substring(header, 7);
                log.info("Token: {}", token);

                if (StringUtils.isNotBlank(token)) {
                    try {
                        jwtTokenHelper.validateToken(token);
                    } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                        if (isAdminPath) {
                            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 不可用"));
                            return;
                        }
                        filterChain.doFilter(request, response);
                        return;
                    } catch (ExpiredJwtException e) {
                        if (isAdminPath) {
                            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 已失效"));
                            return;
                        }
                        filterChain.doFilter(request, response);
                        return;
                    }

                    String username = jwtTokenHelper.getUsernameByToken(token);
                    Long tenantId = jwtTokenHelper.getTenantIdByToken(token);
                    if (tenantId != null) {
                        TenantContext.setTenantId(tenantId);
                    }

                    if (StringUtils.isNotBlank(username)
                            && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }

        // 继续执行写一个过滤器
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 获取设备信息
     *
     * @param request
     * @return
     */
    private static String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("mobile")) {
            return "移动端";
        }
        return "桌面端";
    }


    /**
     * 获取浏览器信息
     *
     * @param request
     * @return
     */
    private static String getBrowserName(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("edg")) {
            return "Edge";
        } else if (userAgent.toLowerCase().contains("chrome")) {
            return "Chrome";
        } else if (userAgent.toLowerCase().contains("firefox")) {
            return "Firefox";
        } else if (userAgent.toLowerCase().contains("safari")) {
            return "Safari";
        } else if (userAgent.toLowerCase().contains("opera")) {
            return "Opera";
        } else if (userAgent.toLowerCase().contains("msie") || userAgent.toLowerCase().contains("trident")) {
            return "Explorer";
        } else {
            return "Unknown";
        }
    }

    /**
     * 获取操作系统
     *
     * @param request
     * @return
     */
    private static String getOsName(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("windows")) {
            return "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "macOS";
        } else if (userAgent.toLowerCase().contains("linux")) {
            return "Linux";
        } else if (userAgent.toLowerCase().contains("ios")) {
            return "iOS";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "Android";
        }
        return "其他";
    }


    /**
     * 判断是否为新老访客
     *
     * @param request
     * @param response
     * @return
     */
    public static boolean isNewVisitor(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        boolean isNew = true;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("visited_before".equals(cookie.getName())) {
                    isNew = false;
                    break;
                }
            }
        }
        if (isNew) {
            // 如果是新访客，设置一个标识cookie
            Cookie visitedCookie = new Cookie("visited_before", "true");
            visitedCookie.setMaxAge(60 * 60 * 24 * 30);  // 设置有效期一个月为例
            response.addCookie(visitedCookie);
        }
        return isNew;
    }

}
