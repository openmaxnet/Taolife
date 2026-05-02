package com.taolife.aichat.enums;

import lombok.Getter;

/**
 * Milvus Collection 字段枚举
 * 统一管理 health_documents 集合的字段名映射
 *
 * @author 文二
 * @date 2026-04-15
 */
@Getter
public enum MilvusField {

    ID("id", "id"),
    DOC_ID("doc_id", "docId"),
    TITLE("title", "title"),
    CONTENT("content", "content"),
    CATEGORY("category", "category"),
    TAGS("tags", "tags"),
    SOURCE("source", "source"),
    CONSTITUTION_TYPE("constitution_type", "constitutionType"),
    SEASON("season", "season"),
    EMBEDDING("embedding", "embedding"),
    CREATED_AT("created_at", "createdAt"),
    UPDATED_AT("updated_at", "updatedAt");

    /** Milvus Collection 列名（snake_case） */
    private final String columnName;

    /** Java 实体属性名（camelCase） */
    private final String propertyName;

    MilvusField(String columnName, String propertyName) {
        this.columnName = columnName;
        this.propertyName = propertyName;
    }
}
