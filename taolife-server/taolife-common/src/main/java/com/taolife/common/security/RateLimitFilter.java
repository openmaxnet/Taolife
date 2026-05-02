package com.taolife.common.security;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.taolife.common.properties.SecurityProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 全接口限流过滤器
 * 基于 Redis 固定窗口计数实现限流：
 * - 已登录请求：按用户 ID 维度限流（默认 120 次/窗口期）
 * - 认证接口（/auth/）：按 IP 维度严格限流（默认 10 次/窗口期）
 * - 未登录普通请求：按 IP + 路径维度限流（默认 60 次/窗口期）
 */
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String RATE_LIMIT_PREFIX = "rate_limit:"; // Redis key 前缀
    private static final String AUTH_PATH_MARKER = "/auth/";       // 认证接口路径标识

    private final StringRedisTemplate redisTemplate;               // Redis 模板
    private final SecurityProperties.RateLimitConfig config;       // 限流配置

    /**
     * 构造方法
     *
     * @param redisTemplate Redis 模板
     * @param config        限流配置
     */
    public RateLimitFilter(StringRedisTemplate redisTemplate, SecurityProperties.RateLimitConfig config) {
        this.redisTemplate = redisTemplate;
        this.config = config;
    }

    /**
     * 限流过滤器核心逻辑
     * 根据请求类型确定限流维度和阈值，超过阈值返回 429
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIp(request);
        boolean isAuthEndpoint = path.contains(AUTH_PATH_MARKER);

        // 确定限流维度和阈值
        String limitKey;
        int limit;
        String userId = UserContext.getAccountId();

        if (userId != null) {
            // 已登录：按用户 ID 维度限流
            limitKey = RATE_LIMIT_PREFIX + "user:" + userId;
            limit = config.getUserLimit();
        } else if (isAuthEndpoint) {
            // 认证接口：按 IP 维度严格限流
            limitKey = RATE_LIMIT_PREFIX + "auth:" + clientIp;
            limit = config.getAuthLimit();
        } else {
            // 未登录：按 IP 维度限流
            limitKey = RATE_LIMIT_PREFIX + "ip:" + clientIp + ":" + path;
            limit = config.getIpLimit();
        }

        if (!checkRateLimit(limitKey, limit)) {
            log.warn("请求限流 - key: {}, 路径: {}, IP: {}", limitKey, path, clientIp);
            writeRateLimitResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 固定窗口限流检查
     * 使用 Redis INCR 原子操作计数，首次请求设置过期时间
     *
     * @param key      限流 key
     * @param maxCount 窗口期内最大请求数
     * @return 是否在限流阈值内
     */
    private boolean checkRateLimit(String key, int maxCount) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            return true;
        }
        if (count == 1) {
            // 首次请求，设置窗口过期时间
            redisTemplate.expire(key, config.getSeconds(), TimeUnit.SECONDS);
        }
        return count <= maxCount;
    }

    /**
     * 获取客户端真实 IP
     * 支持通过 X-Forwarded-For 和 X-Real-IP 头获取代理前的真实 IP
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 写入限流响应（HTTP 429）
     *
     * @param response HTTP 响应
     */
    private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(429);
        response.getWriter().write("{\"code\":\"429\",\"msg\":\"请求过于频繁，请稍后再试\"}");
    }
}
