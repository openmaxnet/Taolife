package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 签到结果VO
 * 返回签到结果信息
 *
 * @author 文二
 * @date 2026-04-02
 */
@Data
public class CheckinResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 获得积分
     */
    private Integer points;

    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;

    /**
     * 累计签到天数
     */
    private Integer totalDays;

    /**
     * 奖励列表
     */
    private List<RewardVO> rewards;

    /**
     * 是否升级
     */
    private Boolean levelUp;

    /**
     * 奖励VO
     */
    @Data
    public static class RewardVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 奖励类型
         */
        private String type;

        /**
         * 奖励名称
         */
        private String name;

        /**
         * 奖励积分
         */
        private Integer points;
    }
}
