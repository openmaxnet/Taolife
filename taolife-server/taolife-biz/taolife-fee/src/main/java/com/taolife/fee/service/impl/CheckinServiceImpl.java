package com.taolife.fee.service.impl;

import com.taolife.identity.entity.Account;
import com.taolife.identity.mapper.AccountMapper;
import com.taolife.fee.enums.PointsTypeEnum;
import com.taolife.fee.mapper.CheckinRecordMapper;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.fee.entity.CheckinRecord;
import com.taolife.fee.service.ICheckinService;
import com.taolife.fee.service.IPointsService;
import com.taolife.fee.entity.PointsRule;
import com.taolife.fee.service.IPointsRuleService;
import com.taolife.fee.service.IMemberGrowthService;
import com.taolife.fee.enums.GrowthSourceEnum;
import com.taolife.fee.vo.CheckinCalendarVO;
import com.taolife.fee.vo.CheckinResultVO;
import com.taolife.fee.vo.CheckinDailyStatusVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 每日签到服务实现类
 *
 * @author 文二
 * @date 2026-04-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements ICheckinService {

    private final CheckinRecordMapper checkinRecordMapper;
    private final AccountMapper accountMapper;
    private final IPointsService pointsService;
    private final IPointsRuleService pointsRuleService;
    private final IMemberGrowthService memberGrowthService;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "checkin:";

    /**
     * 获取今日签到状态
     * 通过 Redis + 数据库双重判断是否已签到，返回连续天数、总天数等信息
     *
     * @param accountId 用户账号ID
     * @return 今日签到状态
     */
    @Override
    public CheckinDailyStatusVO getTodayCheckinStatus(@NonNull String accountId) {
        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        LocalDate today = LocalDate.now();
        String redisKey = REDIS_KEY_PREFIX + accountId + ":" + today;
        Boolean hasCheckin = redisTemplate.hasKey(redisKey);

        List<CheckinRecord> todayRecords = checkinRecordMapper.selectByConditions(accountId, null, null, today, null);
        Boolean hasCheckinInDB = !todayRecords.isEmpty();

        Integer consecutiveCheckinDays = 0;
        Integer totalCheckinDays = 0;
        Integer todayPoints = 0;

        List<CheckinRecord> allRecords = checkinRecordMapper.selectByConditions(accountId, null, null, null, false);

        if (!allRecords.isEmpty()) {
            CheckinRecord latestRecord = allRecords.get(0);
            totalCheckinDays = latestRecord.getTotalCheckinDays() != null ? latestRecord.getTotalCheckinDays() : 0;
            consecutiveCheckinDays = latestRecord.getConsecutiveCheckinDays() != null ? latestRecord.getConsecutiveCheckinDays() : 0;
            todayPoints = pointsService.getTodayPoints(accountId, today);
        }

        Integer userLevel = pointsService.getUserLevel(accountId);

        CheckinDailyStatusVO vo = new CheckinDailyStatusVO();
        vo.setCanCheckin(!hasCheckin && !hasCheckinInDB);
        vo.setConsecutiveDays(consecutiveCheckinDays);
        vo.setTotalDays(totalCheckinDays);
        vo.setUserLevel(userLevel);
        vo.setTodayPoints(todayPoints);

        log.info("今日签到状态 - accountId: {}, canCheckin: {}, consecutiveDays: {}, totalDays: {}",
                 accountId, vo.getCanCheckin(), consecutiveCheckinDays, totalCheckinDays);

        return vo;
    }

    /**
     * 执行每日签到
     * 发放基础积分、连续签到奖励、等级加成积分；写入签到记录和 Redis 防重复标记；
     * 付费用户同时发放签到成长值及每日登录成长值
     *
     * @param accountId 用户账号ID
     * @return 签到结果（含积分、连续天数、奖励明细、是否升级）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinResultVO dailyCheckin(String accountId) {
        log.info("执行每日签到，accountId: {}", accountId);

        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        LocalDate today = LocalDate.now();
        String redisKey = REDIS_KEY_PREFIX + accountId + ":" + today;
        Boolean hasCheckinInRedis = redisTemplate.hasKey(redisKey);

        List<CheckinRecord> todayRecords = checkinRecordMapper.selectByConditions(accountId, null, null, today, null);
        Boolean hasCheckinInDB = !todayRecords.isEmpty();

        if (hasCheckinInRedis || hasCheckinInDB) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "今日已签到，请明天再来");
        }

        List<CheckinRecord> latestRecords = checkinRecordMapper.selectByConditions(accountId, null, null, null, false);

        Integer totalCheckinDays = 0;
        Integer consecutiveCheckinDays = 0;

        if (!latestRecords.isEmpty()) {
            CheckinRecord latestRecord = latestRecords.get(0);
            totalCheckinDays = latestRecord.getTotalCheckinDays() != null ? latestRecord.getTotalCheckinDays() : 0;
            consecutiveCheckinDays = latestRecord.getConsecutiveCheckinDays() != null ? latestRecord.getConsecutiveCheckinDays() : 0;

            LocalDate lastCheckinDate = latestRecord.getCheckinDate();
            if (!lastCheckinDate.equals(today.minusDays(1))) {
                consecutiveCheckinDays = 0;
            }
        }

        Integer newTotalCheckinDays = totalCheckinDays + 1;
        Integer newConsecutiveCheckinDays = consecutiveCheckinDays + 1;

        // 从积分规则获取连续签到奖励
        Integer consecutiveReward = 0;
        List<PointsRule> consecutiveRules = pointsRuleService.getRulesByConditionType(1);
        for (PointsRule rule : consecutiveRules) {
            if (rule.getConditionValue() != null && rule.getConditionValue().equals(newConsecutiveCheckinDays)) {
                consecutiveReward = rule.getPoints() != null ? rule.getPoints() : 0;
                break;
            }
        }

        Integer userLevel = pointsService.getUserLevel(accountId);
        Integer levelBonus = pointsService.getLevelBonusPoints(userLevel);

        int basePoints = pointsRuleService.getPointsByCode("DAILY_CHECKIN");
        Integer totalPoints = basePoints + consecutiveReward + levelBonus;

        CheckinRecord record = new CheckinRecord();
        record.setAccountId(accountId);
        record.setCheckinDate(today);
        record.setTotalCheckinDays(newTotalCheckinDays);
        record.setConsecutiveCheckinDays(newConsecutiveCheckinDays);
        record.setPointsEarned(totalPoints);
        record.setRemark("每日签到");
        record.setCreateTime(LocalDateTime.now());
        checkinRecordMapper.insert(record);

        pointsService.addPoints(accountId, totalPoints, PointsTypeEnum.CHECKIN.getValue(), "daily_checkin",
                             record.getId(), "每日签到");

        // 会员成长值：签到+1，同时补发每日登录成长
        if (account.getMemberLevel() != null && account.getMemberLevel() > 0) {
            memberGrowthService.addGrowth(accountId, 1, GrowthSourceEnum.CHECKIN.getValue(),
                    "daily_checkin", record.getId(), "签到成长值");
            memberGrowthService.grantDailyGrowth(accountId);
        }

        long secondsUntilNextDayEnd = java.time.Duration.between(
            LocalDateTime.now(),
            LocalDate.now().plusDays(1).atTime(23, 59, 59)
        ).getSeconds();

        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "1",
            secondsUntilNextDayEnd, java.util.concurrent.TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "今日已签到，请明天再来");
        }

        Integer oldLevel = userLevel;
        Integer newLevel = pointsService.getUserLevel(accountId);
        Boolean levelUp = !oldLevel.equals(newLevel);

        List<CheckinResultVO.RewardVO> rewards = new ArrayList<>();
        if (consecutiveReward > 0) {
            CheckinResultVO.RewardVO reward = new CheckinResultVO.RewardVO();
            reward.setType("连续签到");
            reward.setName("连续" + newConsecutiveCheckinDays + "天奖励");
            reward.setPoints(consecutiveReward);
            rewards.add(reward);
        }
        if (levelBonus > 0) {
            CheckinResultVO.RewardVO reward = new CheckinResultVO.RewardVO();
            reward.setType("等级加成");
            reward.setName("等级" + newLevel + "加成");
            reward.setPoints(levelBonus);
            rewards.add(reward);
        }

        CheckinResultVO result = new CheckinResultVO();
        result.setPoints(totalPoints);
        result.setConsecutiveDays(newConsecutiveCheckinDays);
        result.setTotalDays(newTotalCheckinDays);
        result.setRewards(rewards);
        result.setLevelUp(levelUp);

        log.info("每日签到成功 - accountId: {}, points: {}, consecutiveDays: {}, totalDays: {}",
                 accountId, totalPoints, newConsecutiveCheckinDays, newTotalCheckinDays);

        return result;
    }

    /**
     * 查询签到日历
     * 返回指定月份中已签到的日期列表及该月获取总积分
     *
     * @param accountId 用户账号ID
     * @param year      年份
     * @param month     月份
     * @return 签到日历信息
     */
    @Override
    public CheckinCalendarVO getCheckinCalendar(String accountId, Integer year, Integer month) {
        log.info("查询签到日历，accountId: {}, year: {}, month: {}", accountId, year, month);

        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<CheckinRecord> records = checkinRecordMapper.selectByConditions(accountId, startDate, endDate, null, true);

        List<Integer> checkedDays = new ArrayList<>();
        Integer currentMonthPoints = 0;
        for (CheckinRecord record : records) {
            checkedDays.add(record.getCheckinDate().getDayOfMonth());
            currentMonthPoints += record.getPointsEarned();
        }

        List<CheckinRecord> allRecords = checkinRecordMapper.selectByConditions(accountId, null, null, null, false);
        Integer consecutiveCheckinDays = 0;
        Integer totalCheckinDays = 0;

        if (!allRecords.isEmpty()) {
            CheckinRecord latestRecord = allRecords.get(0);
            consecutiveCheckinDays = latestRecord.getConsecutiveCheckinDays() != null ? latestRecord.getConsecutiveCheckinDays() : 0;
            totalCheckinDays = latestRecord.getTotalCheckinDays() != null ? latestRecord.getTotalCheckinDays() : 0;
        }

        CheckinCalendarVO vo = new CheckinCalendarVO();
        vo.setCheckedDays(checkedDays);
        vo.setConsecutiveDays(consecutiveCheckinDays);
        vo.setTotalDays(totalCheckinDays);
        vo.setCurrentMonthPoints(currentMonthPoints);

        return vo;
    }

    /**
     * 计算用户连续签到天数
     *
     * @param accountId 用户账号ID
     * @return 连续签到天数
     */
    @Override
    public Integer calculateConsecutiveDays(@NonNull String accountId) {
        List<CheckinRecord> records = checkinRecordMapper.selectByConditions(accountId, null, null, null, false);

        if (records.isEmpty()) {
            return 0;
        }

        CheckinRecord latestRecord = records.get(0);
        return latestRecord.getConsecutiveCheckinDays() != null ? latestRecord.getConsecutiveCheckinDays() : 0;
    }

}
