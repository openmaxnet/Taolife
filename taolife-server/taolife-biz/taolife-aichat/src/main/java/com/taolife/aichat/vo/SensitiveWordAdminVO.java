package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员敏感词VO
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SensitiveWordAdminVO {

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
     * 敏感词类型名称
     */
    private String wordTypeName;

    /**
     * 严重程度：1-低，2-中，3-高
     */
    private Integer severity;

    /**
     * 严重程度名称
     */
    private String severityName;

    /**
     * 处理方式：1-拒绝回答，2-替换，3-警告
     */
    private Integer actionType;

    /**
     * 处理方式名称
     */
    private String actionTypeName;

    /**
     * 替换词
     */
    private String replaceWord;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
