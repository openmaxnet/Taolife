package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员敏感词保存参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SensitiveWordSaveAdminParam {

    /**
     * ID（修改时传入）
     */
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
}
