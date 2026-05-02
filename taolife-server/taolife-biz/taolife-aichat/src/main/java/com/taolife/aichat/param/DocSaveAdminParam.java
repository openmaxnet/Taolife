package com.taolife.aichat.param;

import lombok.Data;

import java.util.List;

/**
 * 管理员文档保存参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class DocSaveAdminParam {

    /**
     * ID（修改时传入）
     */
    private Long id;

    /**
     * 文档标题
     */
    private String title;

    /**
     * Markdown文档内容
     */
    private String content;

    /**
     * 分类：constitution/food/acupoint/health
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 来源
     */
    private String source;

    /**
     * 关联体质类型
     */
    private String constitutionType;

    /**
     * 季节
     */
    private String season;
}
