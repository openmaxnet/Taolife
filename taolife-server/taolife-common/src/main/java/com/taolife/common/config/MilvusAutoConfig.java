package com.taolife.common.config;

import com.taolife.common.properties.MilvusProperties;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus自动配置类
 *
 * @author 文二
 * @date 2026-04-15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MilvusProperties.class)
public class MilvusAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public MilvusClientV2 milvusClient(MilvusProperties props) {
        if (props.getHost() == null || props.getPort() == null) {
            throw new IllegalStateException("Milvus配置缺失，请在application.yml中配置milvus.host和milvus.port");
        }

        String uri = "http://" + props.getHost() + ":" + props.getPort();
        log.info("初始化Milvus连接: {}, database: {}, collection: {}, embeddingDimension: {}",
                uri, props.getDatabase(), props.getCollectionName(), props.getEmbeddingDimension());

        try {
            ConnectConfig config = ConnectConfig.builder()
                    .uri(uri)
                    .dbName(props.getDatabase())
                    .token(props.getUsername() != null && props.getPassword() != null
                            ? props.getUsername() + ":" + props.getPassword() : null)
                    .build();
            MilvusClientV2 client = new MilvusClientV2(config);
            log.info("Milvus连接成功");
            return client;
        } catch (Exception e) {
            log.error("Milvus连接失败: {}", uri, e);
            throw new IllegalStateException("Milvus连接失败: " + uri, e);
        }
    }
}
