package com.taolife.fee.service.impl;

import com.taolife.fee.enums.PointsTypeEnum;
import com.taolife.fee.enums.UserLevelEnum;
import com.taolife.fee.mapper.CheckinRecordMapper;
import com.taolife.fee.mapper.PointsRecordMapper;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.fee.entity.CheckinRecord;
import com.taolife.fee.entity.PointsRecord;
import com.taolife.fee.service.IPointsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分服务实现类
 * 实现积分计算、记录等业务逻辑
 *
 * @author 文二
 * @date 2026-04-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements IPointsService {

    private final PointsRecordMapper pointsRecordMapper;
    private final CheckinRecordMapper checkinRecordMapper;

    /**
     * 增加积分
     * 为用户增加积分并记录
     *
     * @param accountId 账号ID
     * @param points     积分数量
     * @param pointsType 积分类型：1-签到，2-健康计划，3-消费
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param remark     备注
     * @return 变化后余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addPoints(String accountId, Integer points, Integer pointsType,
                           String businessType, String businessId, String remark) {
        log.info("增加积分，accountId: {}, points: {}, type: {}", accountId, points, pointsType);

        // 查询当前可用积分
        Integer currentPoints = pointsRecordMapper.selectAvailablePointsByAccountId(accountId);
        Integer balanceAfter = currentPoints + points;

        // 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setAccountId(accountId);
        record.setPointsChange(points);
        record.setPointsType(pointsType);
        record.setBusinessType(businessType);
        record.setBusinessId(businessId);
        record.setRemark(remark);
        record.setBalanceAfter(balanceAfter);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("积分增加成功，accountId: {}, balanceAfter: {}", accountId, balanceAfter);
        return balanceAfter;
    }

    /**
     * 扣除积分
     * 为用户扣除积分并记录
     *
     * @param accountId 账号ID
     * @param points     积分数量
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param remark     备注
     * @return 变化后余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deductPoints(String accountId, Integer points,
                            String businessType, String businessId, String remark) {
        log.info("扣除积分，accountId: {}, points: {}, type: {}", accountId, points, businessType);

        // 查询当前可用积分
        Integer currentPoints = pointsRecordMapper.selectAvailablePointsByAccountId(accountId);
        if (currentPoints < points) {
            log.warn("积分不足，accountId: {}, currentPoints: {}, required: {}",
                      accountId, currentPoints, points);
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "积分不足");
        }

        // 计算变化后余额
        Integer balanceAfter = currentPoints - points;

        // 创建积分记录（负数表示扣除）
        PointsRecord record = new PointsRecord();
        record.setAccountId(accountId);
        record.setPointsChange(-points);
        record.setPointsType(PointsTypeEnum.CONSUME.getValue());
        record.setBusinessType(businessType);
        record.setBusinessId(businessId);
        record.setRemark(remark);
        record.setBalanceAfter(balanceAfter);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("积分扣除成功，accountId: {}, balanceAfter: {}", accountId, balanceAfter);
        return balanceAfter;
    }

    /**
     * 获取用户可用积分
     * 查询用户的可用积分余额
     *
     * @param accountId 账号ID
     * @return 可用积分
     */
    @Override
    public Integer getAvailablePoints(String accountId) {
        Integer availablePoints = pointsRecordMapper.selectAvailablePointsByAccountId(accountId);
        return availablePoints != null ? availablePoints : 0;
    }

    /**
     * 获取用户等级
     * 根据用户总积分计算等级
     *
     * @param accountId 账号ID
     * @return 用户等级
     */
    @Override
    public Integer getUserLevel(String accountId) {
        // 从 PointsRecord 表查询累计积分
        Integer totalPoints = pointsRecordMapper.selectTotalPointsByAccountId(accountId);

        // 根据总积分计算等级
        UserLevelEnum levelEnum = UserLevelEnum.getByTotalPoints(totalPoints);
        return levelEnum.getLevel();
    }

    /**
     * 获取等级加成积分
     * 根据用户等级获取签到加成积分
     *
     * @param level 用户等级
     * @return 加成积分
     */
    @Override
    public Integer getLevelBonusPoints(Integer level) {
        UserLevelEnum levelEnum = UserLevelEnum.getByLevel(level);
        if (levelEnum == null) {
            return 0;
        }
        // 等级加成：等级1-0，等级2-2，等级3-5，等级4-10，等级5-15，等级6-20
        return switch (levelEnum) {
            case BEGINNER -> 0;
            case APPRENTICE -> 2;
            case EXPERT -> 5;
            case MASTER -> 10;
            case GRANDMASTER -> 15;
            case SUPREME -> 20;
        };
    }

    /**
     * 获取今日已获得积分
     * 查询用户今日已获得的积分
     *
     * @param accountId 账号ID
     * @param today     今日日期
     * @return 今日积分
     */
    @Override
    public Integer getTodayPoints(String accountId, LocalDate today) {
        List<CheckinRecord> records = checkinRecordMapper.selectByConditions(accountId, null, null, today, null);

        return records.stream().mapToInt(CheckinRecord::getPointsEarned).sum();
    }

}
