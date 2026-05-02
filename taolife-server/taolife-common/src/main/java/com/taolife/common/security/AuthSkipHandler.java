package com.taolife.common.security;

import com.taolife.common.annotation.AuthSkip;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 跳过认证处理器
 * 用于检查Controller方法或类上是否有@AuthSkip注解
 *
 * @author 文二
 * @date 2026-04-04
 */
@Slf4j
@Component
public class AuthSkipHandler {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * 缓存路径是否需要跳过认证
     * key: 请求路径, value: 是否跳过认证
     */
    private final Map<String, Boolean> authSkipCache = new ConcurrentHashMap<>();

    public AuthSkipHandler(
            @Qualifier("requestMappingHandlerMapping")
            RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        log.info("AuthSkipHandler 初始化完成");
    }

    /**
     * 检查请求是否需要跳过认证
     * 检查顺序：
     * 1. 先检查缓存
     * 2. 缓存未命中则检查Controller方法上的@AuthSkip注解
     * 3. 再检查Controller类上的@AuthSkip注解
     *
     * @param request HTTP请求
     * @return true表示需要跳过认证，false表示需要认证
     */
    public boolean shouldSkipAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 先检查缓存
        String cacheKey = method + ":" + path;
        Boolean cachedResult = authSkipCache.get(cacheKey);
        if (cachedResult != null) {
            log.debug("从缓存获取跳过认证结果 - 路径: {}, 方法: {}, 结果: {}", path, method, cachedResult);
            return cachedResult;
        }

        try {
            // 获取HandlerMethod
            HandlerMethod handlerMethod = getHandlerMethod(request);
            if (handlerMethod == null) {
                log.debug("未找到HandlerMethod - 路径: {}, 方法: {}", path, method);
                return false;
            }

            // 检查方法上的@AuthSkip注解
            AuthSkip methodAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), AuthSkip.class);
            if (methodAnnotation != null && methodAnnotation.value()) {
                log.info("方法上存在@AuthSkip注解，跳过认证 - 路径: {}, 方法: {}, 原因: {}",
                        path, method, methodAnnotation.reason());
                authSkipCache.put(cacheKey, true);
                return true;
            }

            // 检查类上的@AuthSkip注解
            AuthSkip classAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), AuthSkip.class);
            if (classAnnotation != null && classAnnotation.value()) {
                log.info("类上存在@AuthSkip注解，跳过认证 - 路径: {}, 方法: {}, 原因: {}",
                        path, method, classAnnotation.reason());
                authSkipCache.put(cacheKey, true);
                return true;
            }

            log.debug("未找到@AuthSkip注解，需要认证 - 路径: {}, 方法: {}", path, method);
            authSkipCache.put(cacheKey, false);
            return false;

        } catch (Exception e) {
            log.error("检查@AuthSkip注解时发生异常 - 路径: {}, 方法: {}, 错误: {}",
                    path, method, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取请求对应的HandlerMethod
     *
     * @param request HTTP请求
     * @return HandlerMethod对象，如果未找到则返回null
     */
    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            // 从请求属性中获取HandlerMethod（Spring MVC会自动设置）
            Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (handler instanceof HandlerMethod) {
                return (HandlerMethod) handler;
            }

            // 如果请求属性中没有，则通过RequestMappingHandlerMapping查找
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                if (mappingInfo.getMatchingCondition(request) != null) {
                    return entry.getValue();
                }
            }

            return null;
        } catch (Exception e) {
            log.error("获取HandlerMethod失败 - 路径: {}, 错误: {}", request.getRequestURI(), e.getMessage());
            return null;
        }
    }

    /**
     * 检查请求是否映射到登录端点
     * 通过检查@AuthSkip注解的loginEndpoint属性判断
     *
     * @param request HTTP请求
     * @return true表示是登录端点
     */
    public boolean isLoginEndpoint(HttpServletRequest request) {
        try {
            HandlerMethod handlerMethod = getHandlerMethod(request);
            if (handlerMethod == null) {
                return false;
            }

            AuthSkip methodAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), AuthSkip.class);
            if (methodAnnotation != null && methodAnnotation.loginEndpoint()) {
                return true;
            }

            AuthSkip classAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), AuthSkip.class);
            return classAnnotation != null && classAnnotation.loginEndpoint();
        } catch (Exception e) {
            log.error("检查loginEndpoint时发生异常 - 路径: {}, 错误: {}",
                    request.getRequestURI(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 清除缓存
     * 用于Controller方法更新后清除缓存
     */
    public void clearCache() {
        authSkipCache.clear();
        log.info("已清除@AuthSkip注解缓存");
    }

    /**
     * 清除指定路径的缓存
     *
     * @param path   请求路径
     * @param method 请求方法
     */
    public void clearCache(String path, String method) {
        String cacheKey = method + ":" + path;
        authSkipCache.remove(cacheKey);
        log.info("已清除指定路径的@AuthSkip注解缓存 - 路径: {}, 方法: {}", path, method);
    }
}
