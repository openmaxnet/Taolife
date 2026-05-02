package com.taolife.common.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.taolife.common.config.LoggingAutoConfig;

/**
 * 响应日志记录切面
 * 拦截出站响应，记录响应日志
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseLoggingAdvice implements ResponseBodyAdvice<Object> {

    private final LoggingAutoConfig.LoggingProperties properties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 判断是否支持响应拦截
     *
     * @param returnType   方法返回类型
     * @param converterType 消息转换器类型
     * @return 是否启用响应日志
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return properties.isEnabled();
    }

    /**
     * 响应体写入前拦截
     * 记录出站响应的路径、响应体内容和流式响应类型
     *
     * @param body                  响应体对象
     * @param returnType            方法返回类型
     * @param selectedContentType   选定的内容类型
     * @param selectedConverterType 选定的消息转换器
     * @param request               HTTP请求
     * @param response              HTTP响应
     * @return 原始响应体
     */
    @Override
    public Object beforeBodyWrite(@Nullable Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        if (!properties.isEnabled()) {
            return body;
        }

        // 检查是否需要排除该路径
        String requestPath = request.getURI().getPath();
        for (String pattern : properties.getExcludePaths()) {
            if (pathMatcher.match(pattern, requestPath)) {
                return body;
            }
        }

        // 构建响应日志
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("<==出站响应: ")
                .append(request.getURI().getPath());

        // 如果配置了记录响应体且body不为null，则记录响应内容
        if (properties.isIncludeResponseBody() && body != null) {
            // 对于流式响应（SseEmitter），不记录body内容
            if (!body.getClass().getName().contains("SseEmitter")) {
                try {
                    logMessage.append(", 响应体: ").append(objectMapper.writeValueAsString(body));
                } catch (JacksonException e) {
                    log.warn("序列化响应体失败", e);
                }
            } else {
                logMessage.append(", 类型: SSE流式响应");
            }
        }

        log.info(logMessage.toString());
        return body;
    }
}