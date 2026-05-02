package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员文档VO
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class DocAdminVO {

    /** 记录ID */
    private Long id;

    /** 文档ID */
    private String docId;

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 分类编码 */
    private String category;

    /** 分类名称 */
    private String categoryName;

    /** 标签列表 */
    private List<String> tags;

    /** 来源 */
    private String source;

    /** 体质类型 */
    private String constitutionType;

    /** 季节 */
    private String season;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
