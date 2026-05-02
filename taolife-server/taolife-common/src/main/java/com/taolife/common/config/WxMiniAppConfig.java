package com.taolife.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.taolife.common.properties.WxApiProperties;

/**
 * 微信API相关配置
 * 提供RestTemplate Bean
 *
 * @author 文二
 * @date 2026-03-16
 */
@Configuration
@EnableConfigurationProperties(WxApiProperties.class)
public class WxMiniAppConfig {

    /**
     * 创建RestTemplate Bean
     * 用于调用微信API
     *
     * @return RestTemplate实例
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 设置连接超时（毫秒）
        factory.setConnectTimeout(5000);
        // 设置读取超时（毫秒）
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }
}
