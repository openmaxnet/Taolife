package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员分类规则保存参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class QuestionRuleSaveAdminParam {

    /**
     * ID（修改时传入）
     */
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
     * 关键词列表（逗号分隔）
     */
    private String keywords;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;
}
