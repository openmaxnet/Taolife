package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广告日志管理端VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdLogAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private String id;
    /** 用户账号ID */
    private String accountId;
    /** 广告配置ID */
    private String adConfigId;
    /** 广告类型：1-Banner，2-插屏，3-激励视频，4-开屏 */
    private Integer adType;
    /** 动作：1-展示，2-点击，3-关闭，4-获得奖励 */
    private Integer action;
    /** 观看时长（秒） */
    private Integer duration;
    /** 创建时间 */
    private LocalDateTime createTime;
}
