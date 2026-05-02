package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 敏感词实体
 * 对应数据库表 tf_sensitive_word
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
@Table("tl_ai_sensitive_word")
public class SensitiveWord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 敏感词ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 敏感词
     */
    private String word;

    /**
     * 敏感词类型：1-医疗诊断，2-政治敏感，3-不当内容
     */
    private Integer wordType;

    /**
     * 严重程度：1-低，2-中，3-高
     */
    private Integer severity;

    /**
     * 处理方式：1-拒绝回答，2-替换，3-警告
     */
    private Integer actionType;

    /**
     * 替换词
     */
    private String replaceWord;

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