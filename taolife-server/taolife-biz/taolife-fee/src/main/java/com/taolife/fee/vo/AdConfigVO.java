package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 广告配置VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 配置唯一键 */
    private String configKey;
    /** 广告类型：1-Banner，2-插屏，3-激励视频，4-开屏 */
    private Integer adType;
    /** 微信广告单元ID */
    private String adUnitId;
    /** 页面展示位置标识 */
    private String placement;
    /** 展示间隔秒数 */
    private Integer displayIntervalSeconds;
}
