package com.taolife.common.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 异步访问拒绝处理器
 * 处理异步请求（如SSE流式响应）中响应已提交时的访问拒绝情况
 * 避免在响应已提交后尝试发送错误响应导致的异常
 *
 * @author 文二
 * @date 2026-03-27
 */
@Slf4j
public class AsyncAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 检查响应是否已提交
        if (response.isCommitted()) {
            // 响应已提交，无法发送错误响应，只记录日志
            log.warn("访问拒绝但响应已提交，无法发送错误响应 - 路径: {}, 异常: {}",
                    request.getRequestURI(), accessDeniedException.getMessage());
            return;
        }

        // 响应未提交，正常处理访问拒绝
        log.warn("访问被拒绝 - 路径: {}, 异常: {}",
                request.getRequestURI(), accessDeniedException.getMessage());
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"403\",\"msg\":\"访问被拒绝\"}");
    }
}
