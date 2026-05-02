package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Milvus向量数据库配置属性
 * 必须在application.yml中配置milvus.*属性，无默认值
 *
 * @author 文二
 * @date 2026-04-15
 */
@ConfigurationProperties(prefix = "milvus")
public class MilvusProperties {

    /** Milvus服务地址 */
    private String host;
    /** Milvus服务端口 */
    private Integer port;
    /** 数据库名称 */
    private String database;
    /** 集合名称 */
    private String collectionName;
    /** 向量维度 */
    private Integer embeddingDimension;
    /** Milvus 认证用户名（可选，未设置账号密码时留空） */
    private String username;
    /** Milvus 认证密码（可选，未设置账号密码时留空） */
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Integer getEmbeddingDimension() {
        return embeddingDimension;
    }

    public void setEmbeddingDimension(Integer embeddingDimension) {
        this.embeddingDimension = embeddingDimension;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
