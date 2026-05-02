package com.taolife.fee.vo;

import lombok.Data;
import java.math.BigDecimal;


/**
 * 今日打卡状态视图对象
 * 用于返回今日打卡状态信息给前端
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class CheckinDailyStatusVO {

    /**
     * 今日打卡状态：0-未打卡，1-已打卡
     */
    private Integer checkinStatus;

    /**
     * 今日任务总数
     */
    private Integer totalTasks;

    /**
     * 已完成任务数
     */
    private Integer completedTasks;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 连续打卡天数
     */
    private Integer consecutiveDays;

    /**
     * 是否有奖励可领取
     */
    private Boolean hasReward;

    /**
     * 是否可以打卡
     */
    private Boolean canCheckin;

    /**
     * 累计打卡天数
     */
    private Integer totalDays;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 今日积分
     */
    private Integer todayPoints;
}
