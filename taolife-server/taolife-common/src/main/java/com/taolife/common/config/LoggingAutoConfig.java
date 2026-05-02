package com.taolife.common.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tools.jackson.databind.ObjectMapper;
import com.taolife.common.advice.RequestLoggingAdvice;
import com.taolife.common.advice.ResponseLoggingAdvice;
import com.taolife.common.exception.ResponseInterceptor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@AutoConfiguration
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "taolife.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LoggingAutoConfig.LoggingProperties.class)
public class LoggingAutoConfig implements WebMvcConfigurer {

    private final LoggingProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * 构造日志自动配置
     *
     * @param properties   日志配置属性
     * @param objectMapper JSON对象映射器
     */
    public LoggingAutoConfig(LoggingProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 请求日志拦截器
     *
     * @return 请求日志拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingAdvice requestLoggingInterceptor() {
        return new RequestLoggingAdvice(properties);
    }

    /**
     * 响应日志拦截器
     *
     * @return 响应日志拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseLoggingAdvice responseLoggingAdvice() {
        return new ResponseLoggingAdvice(properties, objectMapper);
    }

    /**
     * 统一响应格式拦截器
     *
     * @return 响应拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseInterceptor responseInterceptor() {
        return new ResponseInterceptor(objectMapper);
    }

    /**
     * 请求体缓存过滤器
     *
     * @return 请求体缓存过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public Filter contentCachingRequestFilter() {
        return new ContentCachingRequestFilter();
    }

    /**
     * 注册拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor());
        // 添加响应拦截器，统一处理响应格式
        registry.addInterceptor(responseInterceptor());
    }

    /**
     * 请求体缓存过滤器
     * 将HttpServletRequest包装为CachedBodyHttpServletRequest，以便多次读取请求体
     */
    public static class ContentCachingRequestFilter implements Filter {

        /**
         * 缓存请求体并传递包装后的请求
         *
         * @param request  HTTP请求
         * @param response HTTP响应
         * @param chain    过滤链
         */
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            // 包装请求，缓存请求体内容
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(
                    (HttpServletRequest) request);
            
            // 继续过滤器链
            chain.doFilter(wrappedRequest, response);
        }
    }

    /**
     * 可缓存的请求包装器
     * 支持多次读取请求体内容
     */
    public static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

        private final byte[] cachedBody;

        /**
         * 构造可缓存请求包装器
         *
         * @param request 原始HTTP请求
         */
        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            // 读取并缓存请求体
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        /**
         * 获取可重复读取的输入流
         *
         * @return 缓存体输入流
         */
        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        /**
         * 获取可重复读取的字符流
         *
         * @return 缓存体字符流
         */
        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        /**
         * 获取缓存的请求体内容
         *
         * @return 请求体字节数组
         */
        public byte[] getCachedBody() {
            return this.cachedBody;
        }

        /**
         * 获取缓存的请求体内容（字符串）
         *
         * @return 请求体字符串
         */
        public String getCachedBodyAsString() {
            return new String(this.cachedBody, StandardCharsets.UTF_8);
        }
    }

    /**
     * 可缓存的Servlet输入流
     * 支持多次读取
     */
    public static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        /**
         * 构造可缓存输入流
         *
         * @param cachedBody 缓存请求体字节数组
         */
        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        /**
         * 输入流是否已读完
         *
         * @return 是否已读完
         */
        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        /**
         * 输入流是否可读
         *
         * @return 是否可读
         */
        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        /**
         * 读取下一个字节
         *
         * @return 下一个字节，-1表示已读完
         */
        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

    @Data
    @ConfigurationProperties(prefix = "taolife.logging")
    public static class LoggingProperties {

        /**
         * 是否启用请求日志记录
         */
        private boolean enabled = true;

        /**
         * 需要排除的路径
         */
        private String[] excludePaths = new String[] { "/actuator/**" };

        /**
         * 是否记录请求头
         */
        private boolean includeHeaders = false;

        /**
         * 是否记录响应体
         */
        private boolean includeResponseBody = true;
    }
}