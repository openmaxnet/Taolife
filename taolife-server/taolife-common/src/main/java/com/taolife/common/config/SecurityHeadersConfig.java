package com.taolife.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全响应头配置
 * 为所有 HTTP 响应添加安全相关的响应头，防止常见的 Web 攻击：
 * - X-Content-Type-Options: 防止 MIME 类型嗅探
 * - X-Frame-Options: 防止点击劫持
 * - X-XSS-Protection: 禁用浏览器内置 XSS 过滤器（已废弃，设为 0 避免误判）
 * - Referrer-Policy: 控制 Referer 头的发送策略
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    /**
     * 注册安全响应头拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("X-XSS-Protection", "0");
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                return true;
            }
        });
    }
}
