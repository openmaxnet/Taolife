package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广告配置管理端VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdConfigAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 广告配置ID */
    private String id;
    /** 配置唯一键 */
    private String configKey;
    /** 广告类型：1-Banner，2-插屏，3-激励视频，4-开屏 */
    private Integer adType;
    /** 微信广告单元ID */
    private String adUnitId;
    /** 页面展示位置 */
    private String placement;
    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;
    /** 仅免费用户可见：0-否，1-是 */
    private Integer freeUserOnly;
    /** 展示间隔（秒） */
    private Integer displayIntervalSeconds;
    /** 额外配置JSON */
    private String extraConfig;
    /** 排序权重 */
    private Integer priority;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
