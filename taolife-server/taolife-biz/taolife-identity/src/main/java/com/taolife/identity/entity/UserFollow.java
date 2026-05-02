package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注实体
 * 对应数据库表 tf_user_follow
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
@Table("tl_id_user_follow")
public class UserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 关注ID */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 关注者ID */
    private String followerId;

    /** 被关注者ID */
    private String followingId;

    /** 逻辑删除标记 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
