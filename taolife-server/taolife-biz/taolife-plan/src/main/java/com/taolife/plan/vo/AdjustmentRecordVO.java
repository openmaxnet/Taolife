package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 调整记录VO
 * 用于返回方案调整记录
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class AdjustmentRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private String id;

    /**
     * 方案类型
     */
    private Integer planType;

    /**
     * 调整类型
     */
    private Integer adjustmentType;

    /**
     * 调整原因
     */
    private String adjustmentReason;

    /**
     * 调整前内容（可能被截断）
     */
    private String beforeContent;

    /**
     * 调整后内容（可能被截断）
     */
    private String afterContent;

    /**
     * 有效性评分（1-5）
     */
    private Integer effectivenessScore;

    /**
     * 创建时间（格式化后的日期时间）
     */
    private String createTime;
}
