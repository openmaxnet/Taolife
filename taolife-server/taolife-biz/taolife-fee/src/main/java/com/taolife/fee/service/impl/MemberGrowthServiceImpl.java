package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.MemberGrowthLevel;
import com.taolife.fee.entity.MemberGrowthRecord;
import com.taolife.fee.enums.GrowthSourceEnum;
import com.taolife.fee.mapper.MemberGrowthLevelMapper;
import com.taolife.fee.mapper.MemberGrowthRecordMapper;
import com.taolife.fee.service.IMemberGrowthService;
import com.taolife.fee.vo.GrowthDetailVO;
import com.taolife.fee.vo.GrowthLevelVO;
import com.taolife.fee.vo.MemberGrowthRecordAdminVO;
import com.taolife.fee.vo.MemberGrowthStatusVO;
import com.taolife.identity.entity.Account;
import com.taolife.identity.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员成长服务实现
 * 处理成长值变动、每日成长值、等级评估等业务逻辑
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberGrowthServiceImpl implements IMemberGrowthService {

    private final MemberGrowthRecordMapper memberGrowthRecordMapper;
    private final MemberGrowthLevelMapper memberGrowthLevelMapper;
    private final AccountMapper accountMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String DAILY_GROWTH_KEY_PREFIX = "taolife:growth:daily:";
    private static final String MEMBER_STATUS_CACHE_PREFIX = "taolife:member:status:";

    /**
     * 增加成长值
     * 更新用户成长值字段并重新评估等级，同时写入成长值变动记录和清除会员缓存
     *
     * @param accountId    用户账号ID
     * @param growth       成长值增量
     * @param source       成长值来源
     * @param businessType 业务类型
     * @param businessId   业务ID
     * @param remark       备注说明
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGrowth(String accountId, int growth, int source, String businessType, String businessId, String remark) {
        if (growth <= 0) {
            return;
        }

        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            return;
        }

        int currentGrowth = account.getGrowthValue() != null ? account.getGrowthValue() : 0;
        int newGrowth = currentGrowth + growth;

        MemberGrowthLevel newLevel = resolveGrowthLevel(newGrowth);

        accountMapper.updateGrowthInfo(accountId, newGrowth, newLevel != null ? newLevel.getLevel() : 0);

        MemberGrowthRecord record = new MemberGrowthRecord();
        record.setAccountId(accountId);
        record.setGrowthChange(growth);
        record.setGrowthSource(source);
        record.setBusinessType(businessType);
        record.setBusinessId(businessId);
        record.setRemark(remark);
        record.setGrowthValueAfter(newGrowth);
        record.setCreateTime(LocalDateTime.now());
        memberGrowthRecordMapper.insert(record);

        redisTemplate.delete(MEMBER_STATUS_CACHE_PREFIX + accountId);

        String levelName = newLevel != null ? newLevel.getLevelName() : "无";
        log.info("成长值增加，accountId: {}, +{}, after: {}, level: {}", accountId, growth, newGrowth, levelName);
    }

    /**
     * 发放每日登录成长值
     * 通过 Redis 防重复发放，仅对有效付费会员发放，根据会员等级发放不同额度
     *
     * @param accountId 用户账号ID
     */
    @Override
    public void grantDailyGrowth(String accountId) {
        String today = LocalDate.now().toString();
        String key = DAILY_GROWTH_KEY_PREFIX + accountId + ":" + today;

        Boolean granted = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(2));
        if (Boolean.FALSE.equals(granted)) {
            return;
        }

        Account account = accountMapper.selectById(accountId);
        if (account == null || account.getMemberLevel() == null || account.getMemberLevel() == 0) {
            return;
        }

        if (account.getMemberExpireTime() != null && account.getMemberExpireTime().isBefore(LocalDateTime.now())) {
            return;
        }

        int dailyGrowth = switch (account.getMemberLevel()) {
            case 1 -> 5;
            case 2 -> 10;
            case 3 -> 15;
            default -> 0;
        };

        if (dailyGrowth > 0) {
            addGrowth(accountId, dailyGrowth, GrowthSourceEnum.DAILY_LOGIN.getValue(),
                    "daily_login", today, "每日登录成长值");
        }
    }

    /**
     * 获取成长值状态
     * 查询当前等级权益、下级等级门槛和升级进度百分比
     *
     * @param accountId 用户账号ID
     * @return 成长值状态（含等级权益、升级进度）
     */
    @Override
    public MemberGrowthStatusVO getGrowthStatus(String accountId) {
        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            return null;
        }

        int growthValue = account.getGrowthValue() != null ? account.getGrowthValue() : 0;
        int growthLevelNum = account.getGrowthLevel() != null ? account.getGrowthLevel() : 0;

        MemberGrowthLevel currentLevel = memberGrowthLevelMapper.selectByLevel(growthLevelNum);
        MemberGrowthLevel nextLevel = findNextLevel(growthLevelNum);

        MemberGrowthStatusVO vo = new MemberGrowthStatusVO();
        vo.setGrowthValue(growthValue);

        if (currentLevel != null) {
            vo.setGrowthLevel(currentLevel.getLevel());
            vo.setGrowthLevelName(currentLevel.getLevelName());
            vo.setCurrentLevelMinGrowth(currentLevel.getMinGrowthValue());
            vo.setBonusAiQuota(currentLevel.getBonusAiQuota());
            vo.setBonusPointsMultiplier(currentLevel.getBonusPointsMultiplier());
            vo.setBonusStoreDiscount(currentLevel.getBonusStoreDiscount());
            vo.setPrivilege(currentLevel.getPrivilege());
        } else {
            vo.setGrowthLevel(0);
            vo.setGrowthLevelName("无");
            vo.setCurrentLevelMinGrowth(0);
            vo.setBonusAiQuota(0);
            vo.setBonusPointsMultiplier(1.0);
            vo.setBonusStoreDiscount(1.0);
            vo.setPrivilege("");
        }

        if (nextLevel != null) {
            vo.setNextLevelMinGrowth(nextLevel.getMinGrowthValue());
            int range = nextLevel.getMinGrowthValue() - vo.getCurrentLevelMinGrowth();
            int progress = growthValue - vo.getCurrentLevelMinGrowth();
            vo.setProgressPercent(range > 0 ? Math.min(100, progress * 100 / range) : 100);
        } else {
            vo.setNextLevelMinGrowth(null);
            vo.setProgressPercent(100);
        }

        return vo;
    }

    /**
     * 分页查询成长值变动记录
     *
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param accountId 用户账号ID
     * @return 成长值记录分页结果
     */
    @Override
    public PageResult<MemberGrowthRecordAdminVO> getGrowthRecordPage(Integer pageNo, Integer pageSize, String accountId) {
        Page<MemberGrowthRecord> page = memberGrowthRecordMapper.selectAdminPage(new Page<>(pageNo, pageSize), accountId);
        List<MemberGrowthRecordAdminVO> voList = page.getRecords().stream().map(this::toRecordVO).toList();
        return new PageResult<>(voList, pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 管理员手动调整成长值
     * 通过 addGrowth 实现，来源标记为管理员调整
     *
     * @param accountId 用户账号ID
     * @param growth    成长值调整量
     * @param remark    调整原因
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustGrowth(String accountId, int growth, String remark) {
        addGrowth(accountId, growth, GrowthSourceEnum.ADMIN_ADJUST.getValue(),
                "admin_adjust", null, remark != null ? remark : "管理员手动调整");
    }

    private MemberGrowthLevel resolveGrowthLevel(int growthValue) {
        List<MemberGrowthLevel> levels = memberGrowthLevelMapper.selectEnabledLevels();
        MemberGrowthLevel result = null;
        for (MemberGrowthLevel level : levels) {
            if (growthValue >= level.getMinGrowthValue()) {
                result = level;
            }
        }
        return result;
    }

    private MemberGrowthLevel findNextLevel(int currentLevel) {
        if (currentLevel <= 0) {
            List<MemberGrowthLevel> levels = memberGrowthLevelMapper.selectEnabledLevels();
            return levels.isEmpty() ? null : levels.get(0);
        }
        List<MemberGrowthLevel> levels = memberGrowthLevelMapper.selectEnabledLevels();
        for (MemberGrowthLevel level : levels) {
            if (level.getLevel() != null && level.getLevel() == currentLevel + 1) {
                return level;
            }
        }
        return null;
    }

    private MemberGrowthRecordAdminVO toRecordVO(MemberGrowthRecord record) {
        MemberGrowthRecordAdminVO vo = new MemberGrowthRecordAdminVO();
        vo.setId(record.getId());
        vo.setAccountId(record.getAccountId());
        vo.setGrowthChange(record.getGrowthChange());
        vo.setGrowthSource(record.getGrowthSource());
        vo.setBusinessType(record.getBusinessType());
        vo.setBusinessId(record.getBusinessId());
        vo.setRemark(record.getRemark());
        vo.setGrowthValueAfter(record.getGrowthValueAfter());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    /**
     * 获取成长值详情
     * 返回用户当前成长值信息和所有已启用等级定义列表
     *
     * @param accountId 用户账号ID
     * @return 成长值详情（含等级列表）
     */
    @Override
    public GrowthDetailVO getGrowthDetail(String accountId) {
        Account account = accountMapper.selectById(accountId);
        int growthValue = account != null && account.getGrowthValue() != null ? account.getGrowthValue() : 0;
        int currentLevelNum = account != null && account.getGrowthLevel() != null ? account.getGrowthLevel() : 0;

        List<MemberGrowthLevel> allLevels = memberGrowthLevelMapper.selectEnabledLevels();

        GrowthDetailVO vo = new GrowthDetailVO();
        vo.setGrowthValue(growthValue);
        vo.setGrowthLevel(currentLevelNum);

        List<GrowthLevelVO> levelVOList = allLevels.stream().map(level -> {
            GrowthLevelVO levelVO = new GrowthLevelVO();
            levelVO.setId(level.getId());
            levelVO.setLevel(level.getLevel());
            levelVO.setLevelName(level.getLevelName());
            levelVO.setMinGrowthValue(level.getMinGrowthValue());
            levelVO.setBonusAiQuota(level.getBonusAiQuota());
            levelVO.setBonusPointsMultiplier(level.getBonusPointsMultiplier());
            levelVO.setBonusStoreDiscount(level.getBonusStoreDiscount());
            levelVO.setPrivilege(level.getPrivilege());
            levelVO.setCurrentLevel(level.getLevel() != null && level.getLevel().equals(currentLevelNum));
            return levelVO;
        }).toList();

        vo.setLevels(levelVOList);
        return vo;
    }
}
