package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS 配置属性
 */
@ConfigurationProperties(prefix = "spring.web.cors")
public class CorsProperties {

    /**
     * 允许的源
     */
    private String allowedOrigins;

    /**
     * 允许的HTTP方法
     */
    private String allowedMethods;

    /**
     * 允许的请求头
     */
    private String allowedHeaders;

    /**
     * 是否允许携带凭证
     */
    private Boolean allowCredentials;

    /**
     * 预检请求的缓存时间（秒）
     */
    private Long maxAge;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public Boolean getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }
}
