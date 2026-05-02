package com.taolife.fee.service;

import java.time.LocalDate;

/**
 * 积分服务接口
 * 定义积分计算、记录等业务操作
 *
 * @author 文二
 * @date 2026-04-02
 */
public interface IPointsService {

    /**
     * 增加积分
     *
     * @param accountId    账号ID
     * @param points       积分数量
     * @param pointsType   积分类型：1-签到，2-健康计划，3-消费
     * @param businessType 业务类型
     * @param businessId   业务ID
     * @param remark       备注
     * @return 变化后余额
     */
    Integer addPoints(String accountId, Integer points, Integer pointsType,
                      String businessType, String businessId, String remark);

    /**
     * 扣除积分
     *
     * @param accountId    账号ID
     * @param points       积分数量
     * @param businessType 业务类型
     * @param businessId   业务ID
     * @param remark       备注
     * @return 变化后余额
     */
    Integer deductPoints(String accountId, Integer points,
                         String businessType, String businessId, String remark);

    /**
     * 获取用户可用积分
     *
     * @param accountId 账号ID
     * @return 可用积分
     */
    Integer getAvailablePoints(String accountId);

    /**
     * 获取用户等级
     *
     * @param accountId 账号ID
     * @return 用户等级
     */
    Integer getUserLevel(String accountId);

    /**
     * 获取等级加成积分
     *
     * @param level 用户等级
     * @return 加成积分
     */
    Integer getLevelBonusPoints(Integer level);

    /**
     * 获取今日已获得积分
     *
     * @param accountId 账号ID
     * @param today     今日日期
     * @return 今日积分
     */
    Integer getTodayPoints(String accountId, LocalDate today);
}
