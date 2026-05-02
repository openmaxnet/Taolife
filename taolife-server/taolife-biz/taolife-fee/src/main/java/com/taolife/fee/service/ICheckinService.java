package com.taolife.fee.service;

import com.taolife.fee.vo.CheckinCalendarVO;
import com.taolife.fee.vo.CheckinResultVO;
import com.taolife.fee.vo.CheckinDailyStatusVO;

/**
 * 每日签到服务接口
 * 定义每日签到相关的业务操作
 *
 * @author 文二
 * @date 2026-04-02
 */
public interface ICheckinService {

    /**
     * 获取今日签到状态
     * 查询用户今日是否可以签到（默认每日签到类型）
     *
     * @param accountId 账号ID
     * @return 今日签到状态
     */
    CheckinDailyStatusVO getTodayCheckinStatus(String accountId);

    /**
     * 执行每日签到
     * 用户进行每日签到操作
     *
     * @param accountId 账号ID
     * @return 签到结果
     */
    CheckinResultVO dailyCheckin(String accountId);

    /**
     * 获取签到日历
     * 查询指定月份的签到记录
     *
     * @param accountId 账号ID
     * @param year      年份
     * @param month     月份
     * @return 签到日历
     */
    CheckinCalendarVO getCheckinCalendar(String accountId, Integer year, Integer month);

    /**
     * 计算连续签到天数
     * 根据最后签到日期计算连续签到天数
     *
     * @param accountId 账号ID
     * @return 连续签到天数
     */
    Integer calculateConsecutiveDays(String accountId);
}
