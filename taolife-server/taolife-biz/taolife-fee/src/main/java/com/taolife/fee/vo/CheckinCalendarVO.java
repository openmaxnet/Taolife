package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 签到日历VO
 * 返回签到日历信息
 *
 * @author 文二
 * @date 2026-04-02
 */
@Data
public class CheckinCalendarVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 已签到日期列表（日期的日数）
     */
    private List<Integer> checkedDays;

    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;

    /**
     * 累计签到天数
     */
    private Integer totalDays;

    /**
     * 当月获得积分
     */
    private Integer currentMonthPoints;
}
