package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员分类规则VO
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class QuestionRuleAdminVO {

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
     * 关键词列表
     */
    private List<String> keywords;

    /**
     * 关键词字符串（逗号分隔）
     */
    private String keywordsStr;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
