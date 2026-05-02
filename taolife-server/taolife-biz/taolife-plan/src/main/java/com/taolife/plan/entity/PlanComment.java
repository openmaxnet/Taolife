package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 方案评论实体
 * 对应数据库表 tf_plan_comment
 *
 * @author 文二
 * @date 2026-04-05
 */
@Data
@Table("tl_plan_comment")
public class PlanComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 方案广场ID
     */
    private String planSquareId;

    /**
     * 评论者账号ID
     */
    private String accountId;

    /**
     * 评论者昵称
     */
    private String nickname;

    /**
     * 评论者头像
     */
    private String avatarUrl;

    /**
     * 父评论ID（回复评论）
     */
    private String parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 图片URL列表JSON
     */
    private String images;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 回复次数
     */
    private Integer replyCount;

    /**
     * 状态：1-正常 2-隐藏 3-删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否 1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;
}
