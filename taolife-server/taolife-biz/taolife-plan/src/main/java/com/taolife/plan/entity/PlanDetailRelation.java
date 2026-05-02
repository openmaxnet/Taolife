package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 方案详情关联实体
 * 对应数据库表 tf_plan_detail_relation
 *
 * @author 文二
 * @date 2026-04-05
 */
@Data
@Table("tl_plan_detail_relation")
public class PlanDetailRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 方案历史ID
     */
    private String planHistoryId;

    /**
     * 关联类型：1-食材 2-运动 3-穴位 4-文章
     */
    private Integer relationType;

    /**
     * 关联内容ID
     */
    private String relationId;

    /**
     * 关联内容名称
     */
    private String relationName;

    /**
     * 是否收藏：0-否 1-是
     */
    private Integer isCollected;

    /**
     * 查看次数
     */
    private Integer viewCount;

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
