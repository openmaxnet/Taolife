package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问题分类规则实体
 * 对应数据库表 tl_ai_chat_rule
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
@Table("tl_ai_chat_rule")
public class ChatRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 关键词列表（JSON数组）
     */
    private String keywords;

    /**
     * 优先级（数字越大优先级越高）
     */
    private Integer priority;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
