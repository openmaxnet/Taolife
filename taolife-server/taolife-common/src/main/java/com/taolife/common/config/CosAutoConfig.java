package com.taolife.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.taolife.common.properties.CosProperties;

/**
 * COS自动配置类
 */
@Configuration
@EnableConfigurationProperties(CosProperties.class)
public class CosAutoConfig {

    /**
     * COS配置属性Bean
     *
     * @return COS配置属性
     */
    @Bean
    @ConditionalOnMissingBean
    public CosProperties cosProperties() {
        return new CosProperties();
    }
}
