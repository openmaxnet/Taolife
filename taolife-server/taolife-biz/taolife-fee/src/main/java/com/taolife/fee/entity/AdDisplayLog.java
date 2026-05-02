package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广告展示日志实体
 * 对应数据库表 tf_ad_display_log
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_ad_display_log")
public class AdDisplayLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 账号ID */
    private String accountId;

    /** 广告配置ID */
    private String adConfigId;

    /** 广告类型：1-Banner，2-插屏，3-激励视频，4-开屏 */
    private Integer adType;

    /** 动作：1-展示，2-点击，3-关闭，4-获得奖励 */
    private Integer action;

    /** 观看时长（秒） */
    private Integer duration;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;
}
