package com.taolife.fee.service.impl;

import tools.jackson.databind.ObjectMapper;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.fee.entity.MemberPlan;
import com.taolife.fee.mapper.MemberPlanMapper;
import com.taolife.fee.service.IMemberService;
import com.taolife.fee.service.IMemberGrowthService;
import com.taolife.fee.vo.MemberGrowthStatusVO;
import com.taolife.fee.vo.MemberStatusVO;
import com.taolife.identity.entity.Account;
import com.taolife.identity.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 会员服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements IMemberService {

    private final AccountMapper accountMapper;
    private final MemberPlanMapper memberPlanMapper;
    private final IMemberGrowthService memberGrowthService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String MEMBER_STATUS_CACHE_PREFIX = "taolife:member:status:";
    private static final Duration MEMBER_STATUS_CACHE_TTL = Duration.ofMinutes(5);

    /**
     * 获取用户会员状态
     * 支持 Redis 缓存读取/写入；计算剩余天数、AI配额、成长等级权益叠加
     *
     * @param accountId 用户账号ID
     * @return 会员状态信息
     */
    @Override
    public MemberStatusVO getMemberStatus(String accountId) {
        // 先从缓存读取
        try {
            String cacheKey = MEMBER_STATUS_CACHE_PREFIX + accountId;
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, MemberStatusVO.class);
            }
        } catch (Exception e) {
            log.warn("读取会员状态缓存失败，accountId：{}", accountId, e);
        }

        Account account = accountMapper.selectOneById(accountId);
        if (account == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND);
        }

        MemberStatusVO vo = new MemberStatusVO();
        vo.setMemberLevel(account.getMemberLevel());

        boolean isActive = isActiveMember(account);
        vo.setIsActiveMember(isActive);
        vo.setExpireTime(account.getMemberExpireTime());
        vo.setMemberLevelName(getMemberLevelName(account.getMemberLevel()));

        if (isActive) {
            vo.setRemainDays(ChronoUnit.DAYS.between(LocalDateTime.now(), account.getMemberExpireTime()));
        } else {
            vo.setRemainDays(0L);
        }

        // 获取配额信息
        MemberPlan plan = getPlanByMemberLevel(account.getMemberLevel());
        if (plan != null) {
            vo.setAiQuotaTotal(plan.getAiDailyQuota());
            vo.setMaxActivePlans(plan.getMaxActivePlans());
            vo.setPointsMultiplier(plan.getPointsMultiplier());
            vo.setStoreDiscount(plan.getStoreDiscount());
            vo.setPointsToYuanRatio(plan.getPointsToYuanRatio());
        } else {
            // 免费用户默认配额
            vo.setAiQuotaTotal(5);
            vo.setMaxActivePlans(1);
            vo.setPointsMultiplier(1);
            vo.setStoreDiscount(1.0);
            vo.setPointsToYuanRatio(100);
        }

        // 获取AI今日已用配额
        Integer aiUsed = getAiQuotaUsed(accountId);
        vo.setAiQuotaUsed(aiUsed);

        // 成长等级权益叠加
        if (isActive) {
            MemberGrowthStatusVO growthStatus = memberGrowthService.getGrowthStatus(accountId);
            if (growthStatus != null) {
                vo.setGrowthValue(growthStatus.getGrowthValue());
                vo.setGrowthLevel(growthStatus.getGrowthLevel());
                vo.setGrowthLevelName(growthStatus.getGrowthLevelName());
                vo.setBonusAiQuota(growthStatus.getBonusAiQuota());
                vo.setBonusPointsMultiplier(growthStatus.getBonusPointsMultiplier());
                vo.setBonusStoreDiscount(growthStatus.getBonusStoreDiscount());
                // AI配额叠加成长加成
                vo.setAiQuotaTotal(vo.getAiQuotaTotal() + growthStatus.getBonusAiQuota());
            }
        }

        vo.setAiQuotaRemaining(Math.max(0, vo.getAiQuotaTotal() - aiUsed));

        // 写入缓存
        try {
            String cacheKey = MEMBER_STATUS_CACHE_PREFIX + accountId;
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(vo), MEMBER_STATUS_CACHE_TTL);
        } catch (Exception e) {
            log.warn("写入会员状态缓存失败，accountId：{}", accountId, e);
        }

        return vo;
    }

    /**
     * 判断用户是否为活跃会员
     *
     * @param accountId 用户账号ID
     * @return true-活跃会员，false-非活跃会员
     */
    @Override
    public boolean isActiveMember(String accountId) {
        Account account = accountMapper.selectOneById(accountId);
        if (account == null) {
            return false;
        }
        return isActiveMember(account);
    }

    /**
     * 获取用户会员等级
     *
     * @param accountId 用户账号ID
     * @return 会员等级（0-普通用户）
     */
    @Override
    public int getMemberLevel(String accountId) {
        Account account = accountMapper.selectOneById(accountId);
        if (account == null) {
            return 0;
        }
        return account.getMemberLevel();
    }

    private boolean isActiveMember(Account account) {
        if (account.getMemberLevel() == null || account.getMemberLevel() == 0) {
            return false;
        }
        if (account.getMemberExpireTime() == null) {
            return false;
        }
        return account.getMemberExpireTime().isAfter(LocalDateTime.now());
    }

    private MemberPlan getPlanByMemberLevel(Integer memberLevel) {
        if (memberLevel == null || memberLevel == 0) {
            return null;
        }
        return memberPlanMapper.selectEnabledList().stream()
                .filter(p -> p.getMemberLevel().equals(memberLevel))
                .findFirst()
                .orElse(null);
    }

    private String getMemberLevelName(Integer memberLevel) {
        if (memberLevel == null || memberLevel == 0) {
            return "普通用户";
        }
        return switch (memberLevel) {
            case 1 -> "月卡会员";
            case 2 -> "年卡会员";
            case 3 -> "终身会员";
            default -> "未知";
        };
    }

    private Integer getAiQuotaUsed(String accountId) {
        String key = "taolife:quota:ai:" + accountId + ":" + LocalDate.now();
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }
}
