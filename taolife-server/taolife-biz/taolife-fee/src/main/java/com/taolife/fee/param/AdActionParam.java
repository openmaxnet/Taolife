package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 广告行为上报参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdActionParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 广告配置Key */
    @NotBlank(message = "广告配置Key不能为空")
    private String adConfigKey;

    /** 动作：1-展示，2-点击，3-关闭，4-获得奖励 */
    @NotNull(message = "动作不能为空")
    private Integer action;

    /** 观看时长（秒） */
    private Integer duration;
}
