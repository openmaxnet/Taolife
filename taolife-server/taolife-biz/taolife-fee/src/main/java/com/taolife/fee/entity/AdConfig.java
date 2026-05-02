package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广告配置实体
 * 对应数据库表 tf_ad_config
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_ad_config")
public class AdConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 配置唯一键，如 banner_home、rewarded_video_chat */
    private String configKey;

    /** 广告类型：1-Banner，2-插屏，3-激励视频，4-开屏 */
    private Integer adType;

    /** 微信广告单元ID */
    private String adUnitId;

    /** 页面展示位置标识 */
    private String placement;

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;

    /** 仅展示给免费用户：0-否，1-是 */
    private Integer freeUserOnly;

    /** 展示间隔秒数，0表示每次都展示 */
    private Integer displayIntervalSeconds;

    /** 额外配置JSON */
    private String extraConfig;

    /** 排序权重 */
    private Integer priority;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
