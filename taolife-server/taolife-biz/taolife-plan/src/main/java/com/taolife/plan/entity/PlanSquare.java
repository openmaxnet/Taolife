package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 方案广场实体类
 * 对应数据库表 tf_plan_square
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
@Table("tl_plan_square")
public class PlanSquare implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案广场ID（主键）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 用户方案ID
     */
    private String userPlanId;

    /**
     * 方案类型：1-综合，2-饮食，3-运动，4-穴位，5-生活
     */
    private Integer planType;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
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
     * 方案摘要
     */
    private String planSummary;

    /**
     * 方案标签（JSON数组）
     */
    private String planTags;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 调整次数
     */
    private Integer adjustmentCount;

    /**
     * 查看次数
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
     * 状态：1-正常，2-隐藏，3-删除
     */
    private Integer status;

    /**
     * 是否官方推荐：0-否，1-是
     */
    private Integer isOfficial;

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
