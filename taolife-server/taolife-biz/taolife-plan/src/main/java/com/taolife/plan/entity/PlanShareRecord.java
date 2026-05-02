package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 方案分享记录实体
 * 对应数据库表 tf_plan_share_record
 *
 * @author 文二
 * @date 2026-04-05
 */
@Data
@Table("tl_plan_share_record")
public class PlanShareRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享记录ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 方案历史ID
     */
    private String planHistoryId;

    /**
     * 分享者账号ID
     */
    private String accountId;

    /**
     * 分享类型：1-朋友圈 2-好友 3-方案广场
     */
    private Integer shareType;

    /**
     * 分享文案
     */
    private String shareText;

    /**
     * 分享图片URL
     */
    private String shareImageUrl;

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
     * 获得积分
     */
    private Integer pointsAwarded;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否删除：0-否 1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;
}
