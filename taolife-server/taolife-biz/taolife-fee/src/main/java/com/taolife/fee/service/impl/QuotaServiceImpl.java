package com.taolife.fee.service.impl;

import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.security.UserContext;
import com.taolife.fee.service.IQuotaService;
import com.taolife.fee.vo.QuotaStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

/**
 * 配额管理服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaServiceImpl implements IQuotaService {

    private final StringRedisTemplate redisTemplate;

    private static final String AI_QUOTA_KEY = "taolife:quota:ai:";
    private static final String ASSESSMENT_QUOTA_KEY = "taolife:quota:assessment:";

    /** 免费用户每日AI问答配额 */
    private static final int FREE_AI_DAILY_QUOTA = 5;
    /** 月卡用户每日AI问答配额 */
    private static final int MONTHLY_AI_DAILY_QUOTA = 50;
    /** 年卡用户每日AI问答配额 */
    private static final int YEARLY_AI_DAILY_QUOTA = 200;
    /** 终身会员配额标记 */
    private static final int LIFETIME_QUOTA = -1;

    /** 免费用户体质评估总次数 */
    private static final int FREE_ASSESSMENT_QUOTA = 1;
    /** 月卡用户每月体质评估次数 */
    private static final int MONTHLY_ASSESSMENT_QUOTA = 3;

    /**
     * 检查并扣减 AI 问答配额
     * 根据会员等级获取每日总配额，已用超限时抛出额度耗尽异常
     *
     * @param accountId 用户账号ID
     */
    @Override
    public void checkAndDecrementAiQuota(String accountId) {
        UserContext ctx = UserContext.get();
        int memberLevel = ctx != null && ctx.getMemberLevel() != null ? ctx.getMemberLevel() : 0;
        int totalQuota = getAiQuotaByMemberLevel(memberLevel);

        if (totalQuota == LIFETIME_QUOTA) {
            return;
        }

        String today = LocalDate.now().toString();
        String key = AI_QUOTA_KEY + accountId + ":" + today;
        String usedStr = redisTemplate.opsForValue().get(key);
        int used = usedStr != null ? Integer.parseInt(usedStr) : 0;

        if (used >= totalQuota) {
            throw new BusinessException(ExceptionCode.AI_DAILY_QUOTA_EXHAUSTED);
        }

        long secondsUntilEndOfDay = Duration.between(
                LocalDateTime.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        ).getSeconds();
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofSeconds(secondsUntilEndOfDay + 1));
    }

    /**
     * 查询 AI 问答配额使用状态
     * 终身会员返回 -1 标记无限制
     *
     * @param accountId 用户账号ID
     * @return 配额状态信息（总量、已用、剩余）
     */
    @Override
    public QuotaStatusVO getAiQuotaStatus(String accountId) {
        UserContext ctx = UserContext.get();
        int memberLevel = ctx != null && ctx.getMemberLevel() != null ? ctx.getMemberLevel() : 0;
        int totalQuota = getAiQuotaByMemberLevel(memberLevel);

        QuotaStatusVO vo = new QuotaStatusVO();
        vo.setTotal(totalQuota);

        if (totalQuota == LIFETIME_QUOTA) {
            vo.setUsed(0);
            vo.setRemaining(LIFETIME_QUOTA);
            vo.setHasRemaining(true);
            return vo;
        }

        String today = LocalDate.now().toString();
        String key = AI_QUOTA_KEY + accountId + ":" + today;
        String usedStr = redisTemplate.opsForValue().get(key);
        int used = usedStr != null ? Integer.parseInt(usedStr) : 0;

        vo.setUsed(used);
        vo.setRemaining(Math.max(0, totalQuota - used));
        vo.setHasRemaining(used < totalQuota);
        return vo;
    }

    /**
     * 增加 AI 问答配额（积分兑换商品时调用）
     * 在 Redis 中记录负值以抵消当日已用额度
     *
     * @param accountId 用户账号ID
     * @param count     增加的配额数量
     */
    @Override
    public void incrementAiQuota(String accountId, int count) {
        String today = LocalDate.now().toString();
        String key = AI_QUOTA_KEY + accountId + ":" + today;

        long secondsUntilEndOfDay = Duration.between(
                LocalDateTime.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        ).getSeconds();

        String usedStr = redisTemplate.opsForValue().get(key);
        if (usedStr == null) {
            redisTemplate.opsForValue().set(key, String.valueOf(-count), Duration.ofSeconds(secondsUntilEndOfDay + 1));
        } else {
            int current = Integer.parseInt(usedStr);
            redisTemplate.opsForValue().set(key, String.valueOf(Math.max(0, current - count)), Duration.ofSeconds(secondsUntilEndOfDay + 1));
        }
    }

    /**
     * 检查体质评估配额（按月统计）
     * 超过月配额时抛出额度耗尽异常
     *
     * @param accountId 用户账号ID
     */
    @Override
    public void checkAssessmentQuota(String accountId) {
        UserContext ctx = UserContext.get();
        int memberLevel = ctx != null && ctx.getMemberLevel() != null ? ctx.getMemberLevel() : 0;
        int totalQuota = getAssessmentQuotaByMemberLevel(memberLevel);

        if (totalQuota == LIFETIME_QUOTA) {
            return;
        }

        YearMonth currentMonth = YearMonth.now();
        String key = ASSESSMENT_QUOTA_KEY + accountId + ":" + currentMonth;
        String usedStr = redisTemplate.opsForValue().get(key);
        int used = usedStr != null ? Integer.parseInt(usedStr) : 0;

        if (used >= totalQuota) {
            throw new BusinessException(ExceptionCode.ASSESSMENT_QUOTA_EXHAUSTED);
        }
    }

    /**
     * 增加体质评估配额（积分兑换商品时调用）
     * 在 Redis 中记录负值以抵消当月已用额度
     *
     * @param accountId 用户账号ID
     * @param count     增加的配额数量
     */
    @Override
    public void incrementAssessmentQuota(String accountId, int count) {
        YearMonth currentMonth = YearMonth.now();
        String key = ASSESSMENT_QUOTA_KEY + accountId + ":" + currentMonth;

        long secondsUntilEndOfMonth = Duration.between(
                LocalDateTime.now(),
                currentMonth.atEndOfMonth().atTime(LocalTime.MAX)
        ).getSeconds();

        String usedStr = redisTemplate.opsForValue().get(key);
        if (usedStr == null) {
            redisTemplate.opsForValue().set(key, String.valueOf(-count), Duration.ofSeconds(secondsUntilEndOfMonth + 1));
        } else {
            int current = Integer.parseInt(usedStr);
            redisTemplate.opsForValue().set(key, String.valueOf(Math.max(0, current - count)), Duration.ofSeconds(secondsUntilEndOfMonth + 1));
        }
    }

    private int getAiQuotaByMemberLevel(Integer memberLevel) {
        if (memberLevel == null || memberLevel == 0) return FREE_AI_DAILY_QUOTA;
        return switch (memberLevel) {
            case 1 -> MONTHLY_AI_DAILY_QUOTA;
            case 2 -> YEARLY_AI_DAILY_QUOTA;
            case 3 -> LIFETIME_QUOTA;
            default -> FREE_AI_DAILY_QUOTA;
        };
    }

    private int getAssessmentQuotaByMemberLevel(Integer memberLevel) {
        if (memberLevel == null || memberLevel == 0) return FREE_ASSESSMENT_QUOTA;
        return switch (memberLevel) {
            case 1 -> MONTHLY_ASSESSMENT_QUOTA;
            case 2, 3 -> LIFETIME_QUOTA;
            default -> FREE_ASSESSMENT_QUOTA;
        };
    }
}
