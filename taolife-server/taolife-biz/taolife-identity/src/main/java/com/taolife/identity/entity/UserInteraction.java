package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户互动记录实体
 * 对应数据库表 tf_user_interaction
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
@Table("tl_id_user_interaction")
public class UserInteraction implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 互动记录ID */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 账号ID */
    private String accountId;

    /** 目标类型：1-方案广场 2-文章 3-食物 4-运动 5-方案详情 */
    private Integer targetType;

    /** 目标ID */
    private String targetId;

    /** 互动类型：1-点赞 2-收藏 */
    private Integer interactionType;

    /** 逻辑删除标记 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
