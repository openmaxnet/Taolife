package com.taolife.common.config;

import jakarta.annotation.PostConstruct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import com.taolife.common.utils.UUIDKeyGeneratorUtil;

/**
 * MyBatis-Flex 配置
 * 配置 Mapper 扫描路径、UUID 主键生成器、下划线转驼峰映射
 *
 * @author 文二
 * @date 2026-04-18
 */
@Configuration
@MapperScan({ "com.taolife.*.mapper" })
public class MybatisFlexConfig implements ConfigurationCustomizer {

    /**
     * 初始化UUID主键生成器
     */
    @PostConstruct
    public void init() {
        // 注册UUID主键生成器
        KeyGeneratorFactory.register("Flex-UUID", new UUIDKeyGeneratorUtil());
    }

    /**
     * 自定义MyBatis-Flex配置（启用下划线转驼峰）
     *
     * @param configuration MyBatis-Flex配置对象
     */
    @Override
    public void customize(FlexConfiguration configuration) {
        configuration.setMapUnderscoreToCamelCase(true);
    }
}