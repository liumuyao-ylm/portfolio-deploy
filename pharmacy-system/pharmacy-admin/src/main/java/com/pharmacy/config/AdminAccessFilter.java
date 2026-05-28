package com.pharmacy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.common.Result;
import com.pharmacy.util.AuthTokenUtil;
import com.pharmacy.util.AuthTokenUtil.TokenClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminAccessFilter extends OncePerRequestFilter {
    private static final String DEMO_READONLY_MESSAGE = "演示账号仅支持查看，不能修改数据。";
    private static final String LOGIN_REQUIRED_MESSAGE = "请先登录后再访问管理端接口。";
    private static final String ACCESS_DENIED_MESSAGE = "当前账号无权访问管理端接口。";

    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper objectMapper;

    public AdminAccessFilter(AuthTokenUtil authTokenUtil, ObjectMapper objectMapper) {
        this.authTokenUtil = authTokenUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (!path.startsWith("/api/") || isPublicPath(path) || HttpMethod.OPTIONS.matches(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        TokenClaims claims = authTokenUtil.parseToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (claims == null) {
            writeJson(response, HttpStatus.UNAUTHORIZED, Result.error(401, LOGIN_REQUIRED_MESSAGE));
            return;
        }

        if (!isAdminRole(claims.role())) {
            writeJson(response, HttpStatus.FORBIDDEN, Result.error(403, ACCESS_DENIED_MESSAGE));
            return;
        }

        if (claims.isDemo() && isRestrictedDemoRequest(method, path)) {
            writeJson(response, HttpStatus.FORBIDDEN, Result.error(403, DEMO_READONLY_MESSAGE));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/client/")
                || "/api/user/login".equals(path)
                || "/api/user/password-hint".equals(path)
                || path.startsWith("/api/test/");
    }

    private boolean isRestrictedDemoRequest(String method, String path) {
        if (path.contains("/export") || path.contains("/import")) {
            return true;
        }
        return HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.PATCH.matches(method)
                || HttpMethod.DELETE.matches(method);
    }

    private boolean isAdminRole(String role) {
        return "ADMIN".equals(role) || "STAFF".equals(role) || "DEMO".equals(role);
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, Result<Void> result) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
