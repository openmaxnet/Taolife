package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 积分总览VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 可用积分余额 */
    private Integer availablePoints;
    /** 今日获得积分 */
    private Integer todayEarned;
    /** 用户等级 */
    private Integer userLevel;
}
