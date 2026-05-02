package com.taolife.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import com.taolife.common.config.LoggingAutoConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RequestLoggingAdvice implements HandlerInterceptor {

    private final LoggingAutoConfig.LoggingProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 请求预处理拦截
     * 记录入站HTTP请求的方法、路径、查询参数、请求体和请求头
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器对象
     * @return 是否继续处理请求
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        if (!properties.isEnabled()) {
            return true;
        }

        // 检查是否需要排除该路径
        String requestPath = request.getRequestURI();
        for (String pattern : properties.getExcludePaths()) {
            if (pathMatcher.match(pattern, requestPath)) {
                return true;
            }
        }

        // 添加对验证码等公共API的排除，减少日志噪音
        if (requestPath.contains("/captcha/") || requestPath.contains("/public-api/")) {
            return true;
        }

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("==>入站请求: ")
                .append(request.getMethod())
                .append(" ")
                .append(requestPath);

        // 添加GET请求的查询参数
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            logMessage.append("?").append(queryString);
        }

        // 添加POST请求的请求体参数
        String method = request.getMethod();
        if ("POST".equals(method) && request.getContentType() != null
                && request.getContentType().contains("application/json")) {
            // 认证相关接口脱敏处理，防止密码泄露到日志
            if (requestPath.contains("/auth/") || requestPath.contains("/login")) {
                logMessage.append(", Body: [REDACTED]");
            } else {
                String body = getRequestBody(request);
                if (body != null && !body.isEmpty()) {
                    logMessage.append(", Body: ").append(body);
                }
            }
        }

        if (properties.isIncludeHeaders()) {
            logMessage.append(", Headers: {");
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                logMessage.append(headerName)
                        .append("=")
                        .append(request.getHeader(headerName))
                        .append(", ");
            });
            if (logMessage.toString().endsWith(", ")) {
                logMessage.setLength(logMessage.length() - 2);
            }
            logMessage.append("}");
        }

        log.info(logMessage.toString());
        return true;
    }

    /**
     * 获取请求体内容
     * 使用CachedBodyHttpServletRequest读取缓存内容
     *
     * @param request HTTP请求对象
     * @return 请求体内容
     */
    private String getRequestBody(HttpServletRequest request) {
        if (request instanceof LoggingAutoConfig.CachedBodyHttpServletRequest wrapper) {
            return wrapper.getCachedBodyAsString();
        }
        return null;
    }

}