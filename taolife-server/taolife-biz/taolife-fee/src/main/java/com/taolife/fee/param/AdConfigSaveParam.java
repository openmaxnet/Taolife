package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 广告配置保存参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdConfigSaveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 配置ID，为空表示新增 */
    private String id;

    /** 配置唯一键 */
    @NotBlank(message = "配置Key不能为空")
    private String configKey;

    /** 广告类型：1-Banner，2-插屏，3-激励视频，4-开屏 */
    @NotNull(message = "广告类型不能为空")
    private Integer adType;

    /** 微信广告单元ID */
    @NotBlank(message = "广告单元ID不能为空")
    private String adUnitId;

    /** 页面展示位置标识 */
    @NotBlank(message = "展示位置不能为空")
    private String placement;

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;

    /** 仅免费用户可见：0-否，1-是 */
    private Integer freeUserOnly;

    /** 展示间隔秒数 */
    private Integer displayIntervalSeconds;

    /** 额外配置JSON */
    private String extraConfig;

    /** 排序权重 */
    private Integer priority;
}
