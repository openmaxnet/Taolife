package com.taolife.common.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.taolife.common.properties.CorsProperties;

/**
 * CORS 自动配置类
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(CorsProperties.class)
public class CorsAutoConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public CorsAutoConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    /**
     * 配置全局CORS映射
     *
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String origins = corsProperties.getAllowedOrigins();
        if (origins == null || origins.isBlank()) {
            return;
        }
        registry.addMapping("/**")
                .allowedOriginPatterns(origins.split(","))
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(corsProperties.getAllowCredentials() != null ? corsProperties.getAllowCredentials() : true)
                .maxAge(corsProperties.getMaxAge() != null ? corsProperties.getMaxAge() : 3600);
    }

    /**
     * CORS配置源
     *
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String origins = corsProperties.getAllowedOrigins();
        if (origins != null && !origins.isBlank()) {
            configuration.setAllowedOriginPatterns(Arrays.asList(origins.split(",")));
        }
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(corsProperties.getAllowCredentials() != null ? corsProperties.getAllowCredentials() : true);
        configuration.setMaxAge(corsProperties.getMaxAge() != null ? corsProperties.getMaxAge() : 3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}