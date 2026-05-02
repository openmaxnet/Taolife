package com.taolife.common.exception;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应拦截器
 * 统一处理响应格式，为空响应体设置默认的成功响应
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    /**
     * 请求处理完成后执行
     * 
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理器对象
     * @param ex       异常对象（如果有）
     * @throws Exception 处理异常
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler, @Nullable Exception ex) throws Exception {
        // 如果已经有异常处理器处理了异常，这里就不需要再处理
        if (ex != null) {
            return;
        }

        // 检查是否是API请求
        if (isApiRequest(request)) {
            // 检查响应是否已经被处理过
            if (!response.isCommitted() && response.getStatus() == 200) {
                // 如果响应体为空，设置统一的成功响应
                if (response.getBufferSize() == 0) {
                    ExceptionResult<Object> exceptionResult = ExceptionResult.success();
                    writeResponse(response, exceptionResult);
                }
            }
        }
    }

    /**
     * 判断是否是API请求
     * 
     * @param request HTTP请求对象
     * @return 是否是API请求
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/taolife/") || uri.startsWith("/test/");
    }

    /**
     * 写入响应
     * 
     * @param response        HTTP响应对象
     * @param exceptionResult 响应结果对象
     * @throws IOException IO异常
     */
    private void writeResponse(HttpServletResponse response, ExceptionResult<?> exceptionResult) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = objectMapper.writeValueAsString(exceptionResult);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}