package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 方案广场列表VO
 * 用于返回方案广场的分页列表
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanSquareListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案ID
     */
    private String id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 体质名称
     */
    private String constitutionName;

    /**
     * 方案标题
     */
    private String planTitle;

    /**
     * 方案摘要（截断）
     */
    private String planSummary;

    /**
     * 方案标签
     */
    private String planTags;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 是否官方方案
     */
    private Integer isOfficial;

    /**
     * 创建时间（格式化后的日期时间）
     */
    private String createTime;
}
