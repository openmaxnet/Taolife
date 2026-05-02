package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.MemberPlan;
import com.taolife.fee.mapper.MemberPlanMapper;
import com.taolife.fee.param.MemberPlanPageParam;
import com.taolife.fee.param.MemberPlanSaveParam;
import com.taolife.fee.service.IMemberPlanService;
import com.taolife.fee.vo.MemberPlanVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员套餐服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberPlanServiceImpl implements IMemberPlanService {

    private final MemberPlanMapper memberPlanMapper;

    /**
     * 分页查询会员套餐列表
     *
     * @param param 分页查询参数（含页码、每页条数、套餐代码、启用状态过滤条件）
     * @return 会员套餐分页结果
     */
    @Override
    public PageResult<MemberPlanVO> getMemberPlanPage(MemberPlanPageParam param) {
        Page<MemberPlan> page = memberPlanMapper.selectMemberPlanPage(
                param.getPageNo(),
                param.getPageSize(),
                param.getPlanCode(),
                param.getIsEnabled());
        List<MemberPlanVO> list = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 按 ID 查询会员套餐
     *
     * @param id 会员套餐ID
     * @return 会员套餐VO
     */
    @Override
    public MemberPlanVO getMemberPlan(String id) {
        MemberPlan entity = memberPlanMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.MEMBER_PLAN_NOT_FOUND);
        }
        return convertToVO(entity);
    }

    /**
     * 获取所有已启用的会员套餐列表
     *
     * @return 会员套餐VO列表
     */
    @Override
    public List<MemberPlanVO> getEnabledPlans() {
        return memberPlanMapper.selectEnabledList().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 创建会员套餐
     *
     * @param param 会员套餐保存参数
     * @return 新套餐ID
     */
    @Override
    public String createMemberPlan(MemberPlanSaveParam param) {
        MemberPlan existing = memberPlanMapper.selectByPlanCodeIgnoreEnabled(param.getPlanCode());
        if (existing != null) {
            throw new BusinessException(ExceptionCode.MEMBER_PLAN_CODE_DUPLICATE);
        }
        MemberPlan entity = new MemberPlan();
        entity.setPlanCode(param.getPlanCode());
        entity.setPlanName(param.getPlanName());
        entity.setMemberLevel(param.getMemberLevel());
        entity.setDurationDays(param.getDurationDays());
        entity.setOriginalPrice(param.getOriginalPrice());
        entity.setCurrentPrice(param.getCurrentPrice());
        entity.setDiscountLabel(param.getDiscountLabel());
        entity.setAiDailyQuota(param.getAiDailyQuota());
        entity.setMaxActivePlans(param.getMaxActivePlans());
        entity.setMaxCycleDays(param.getMaxCycleDays());
        entity.setMaxAdjustments(param.getMaxAdjustments());
        entity.setAssessmentMonthlyQuota(param.getAssessmentMonthlyQuota());
        entity.setPointsMultiplier(param.getPointsMultiplier());
        entity.setStoreDiscount(param.getStoreDiscount());
        entity.setPointsToYuanRatio(param.getPointsToYuanRatio());
        entity.setBenefitsJson(param.getBenefitsJson());
        entity.setSubGrowthBonus(param.getSubGrowthBonus());
        entity.setSortOrder(param.getSortOrder());
        entity.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        memberPlanMapper.insert(entity);
        log.info("创建会员套餐成功，ID：{}，套餐代码：{}", entity.getId(), entity.getPlanCode());
        return entity.getId();
    }

    /**
     * 修改会员套餐
     *
     * @param id    会员套餐ID
     * @param param 会员套餐保存参数
     */
    @Override
    public void modifyMemberPlan(String id, MemberPlanSaveParam param) {
        MemberPlan entity = memberPlanMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.MEMBER_PLAN_NOT_FOUND);
        }
        if (!entity.getPlanCode().equals(param.getPlanCode())) {
            MemberPlan existing = memberPlanMapper.selectByPlanCodeIgnoreEnabled(param.getPlanCode());
            if (existing != null) {
                throw new BusinessException(ExceptionCode.MEMBER_PLAN_CODE_DUPLICATE);
            }
        }
        entity.setPlanCode(param.getPlanCode());
        entity.setPlanName(param.getPlanName());
        entity.setMemberLevel(param.getMemberLevel());
        entity.setDurationDays(param.getDurationDays());
        entity.setOriginalPrice(param.getOriginalPrice());
        entity.setCurrentPrice(param.getCurrentPrice());
        entity.setDiscountLabel(param.getDiscountLabel());
        entity.setAiDailyQuota(param.getAiDailyQuota());
        entity.setMaxActivePlans(param.getMaxActivePlans());
        entity.setMaxCycleDays(param.getMaxCycleDays());
        entity.setMaxAdjustments(param.getMaxAdjustments());
        entity.setAssessmentMonthlyQuota(param.getAssessmentMonthlyQuota());
        entity.setPointsMultiplier(param.getPointsMultiplier());
        entity.setStoreDiscount(param.getStoreDiscount());
        entity.setPointsToYuanRatio(param.getPointsToYuanRatio());
        entity.setBenefitsJson(param.getBenefitsJson());
        entity.setSubGrowthBonus(param.getSubGrowthBonus());
        entity.setSortOrder(param.getSortOrder());
        entity.setIsEnabled(param.getIsEnabled());
        entity.setUpdateTime(LocalDateTime.now());
        memberPlanMapper.update(entity);
        log.info("修改会员套餐成功，ID：{}", id);
    }

    /**
     * 删除会员套餐
     *
     * @param id 会员套餐ID
     */
    @Override
    public void removeMemberPlan(String id) {
        MemberPlan entity = memberPlanMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.MEMBER_PLAN_NOT_FOUND);
        }
        memberPlanMapper.deleteById(id);
        log.info("删除会员套餐成功，ID：{}", id);
    }

    private MemberPlanVO convertToVO(MemberPlan entity) {
        MemberPlanVO vo = new MemberPlanVO();
        vo.setId(entity.getId());
        vo.setPlanCode(entity.getPlanCode());
        vo.setPlanName(entity.getPlanName());
        vo.setMemberLevel(entity.getMemberLevel());
        vo.setDurationDays(entity.getDurationDays());
        vo.setOriginalPrice(entity.getOriginalPrice());
        vo.setCurrentPrice(entity.getCurrentPrice());
        vo.setDiscountLabel(entity.getDiscountLabel());
        vo.setAiDailyQuota(entity.getAiDailyQuota());
        vo.setMaxActivePlans(entity.getMaxActivePlans());
        vo.setMaxCycleDays(entity.getMaxCycleDays());
        vo.setMaxAdjustments(entity.getMaxAdjustments());
        vo.setAssessmentMonthlyQuota(entity.getAssessmentMonthlyQuota());
        vo.setPointsMultiplier(entity.getPointsMultiplier());
        vo.setStoreDiscount(entity.getStoreDiscount());
        vo.setPointsToYuanRatio(entity.getPointsToYuanRatio());
        vo.setBenefitsJson(entity.getBenefitsJson());
        vo.setSubGrowthBonus(entity.getSubGrowthBonus());
        vo.setSortOrder(entity.getSortOrder());
        vo.setIsEnabled(entity.getIsEnabled());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
