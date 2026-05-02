package com.taolife.fee.service.impl;

import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.security.UserContext;
import com.taolife.fee.enums.AdActionEnum;
import com.taolife.fee.entity.AdConfig;
import com.taolife.fee.entity.AdDisplayLog;
import com.taolife.fee.mapper.AdConfigMapper;
import com.taolife.fee.mapper.AdDisplayLogMapper;
import com.taolife.fee.service.IAdConfigService;
import com.taolife.fee.vo.AdConfigVO;
import com.taolife.fee.service.IPointsService;
import com.taolife.fee.entity.PointsRule;
import com.taolife.fee.service.IPointsRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 广告配置服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdConfigServiceImpl implements IAdConfigService {

    private final AdConfigMapper adConfigMapper;
    private final AdDisplayLogMapper adDisplayLogMapper;
    private final IPointsService pointsService;
    private final IPointsRuleService pointsRuleService;
    private final StringRedisTemplate redisTemplate;

    private static final String AD_REWARD_REDIS_KEY = "taolife:ad:reward:daily:";

    /**
     * 获取当前用户可见的广告配置列表
     * 根据用户会员等级过滤：付费用户不展示限免广告
     *
     * @param accountId 用户账号ID
     * @return 可见的广告配置列表
     */
    @Override
    public List<AdConfigVO> getVisibleAdConfigs(String accountId) {
        UserContext ctx = UserContext.get();
        int memberLevel = ctx != null && ctx.getMemberLevel() != null ? ctx.getMemberLevel() : 0;
        boolean isMember = memberLevel > 0;

        List<AdConfig> allConfigs = adConfigMapper.selectEnabled();

        return allConfigs.stream()
                .filter(c -> {
                    if (isMember && c.getFreeUserOnly() != null && c.getFreeUserOnly() == 1) {
                        return false;
                    }
                    return true;
                })
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 上报广告动作（展示、点击、关闭、获得奖励）
     * 记录广告展示日志；当动作为"获得奖励"时，校验每日奖励上限并发放积分
     *
     * @param accountId   用户账号ID
     * @param adConfigKey 广告配置键
     * @param action      动作类型（展示/点击/关闭/奖励）
     * @param duration    广告停留时长（秒）
     */
    @Override
    public void reportAdAction(String accountId, String adConfigKey, Integer action, Integer duration) {
        AdConfig config = adConfigMapper.selectOneByQuery(
                com.mybatisflex.core.query.QueryWrapper.create()
                        .where(AdConfig::getConfigKey).eq(adConfigKey)
        );
        if (config == null) {
            throw new BusinessException(ExceptionCode.AD_CONFIG_NOT_FOUND);
        }

        AdDisplayLog logEntry = new AdDisplayLog();
        logEntry.setAccountId(accountId);
        logEntry.setAdConfigId(config.getId());
        logEntry.setAdType(config.getAdType());
        logEntry.setAction(action);
        logEntry.setDuration(duration);
        logEntry.setCreateTime(LocalDateTime.now());
        adDisplayLogMapper.insert(logEntry);

        if (AdActionEnum.REWARD.getValue().equals(action)) {
            PointsRule adRule = pointsRuleService.getRuleByCode("AD_REWARD");
            int adRewardPoints = adRule != null && adRule.getPoints() != null ? adRule.getPoints() : 0;
            int adRewardDailyLimit = adRule != null && adRule.getDailyLimit() != null ? adRule.getDailyLimit() : 3;

            String today = LocalDate.now().toString();
            String redisKey = AD_REWARD_REDIS_KEY + accountId + ":" + today;
            Long newCount = redisTemplate.opsForValue().increment(redisKey);
            if (newCount != null && newCount == 1) {
                LocalDateTime midnight = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
                long secondsUntilMidnight = Duration.between(LocalDateTime.now(), midnight).getSeconds();
                redisTemplate.expire(redisKey, secondsUntilMidnight, TimeUnit.SECONDS);
            }
            if (newCount != null && newCount > adRewardDailyLimit) {
                throw new BusinessException(ExceptionCode.AD_REWARD_LIMIT);
            }
            pointsService.addPoints(accountId, adRewardPoints,
                    com.taolife.fee.enums.PointsTypeEnum.AD_REWARD.getValue(), "AD_REWARD",
                    config.getId(), "观看广告奖励");
        }
    }

    private AdConfigVO convertToVO(AdConfig config) {
        AdConfigVO vo = new AdConfigVO();
        vo.setConfigKey(config.getConfigKey());
        vo.setAdType(config.getAdType());
        vo.setAdUnitId(config.getAdUnitId());
        vo.setPlacement(config.getPlacement());
        vo.setDisplayIntervalSeconds(config.getDisplayIntervalSeconds());
        return vo;
    }
}
