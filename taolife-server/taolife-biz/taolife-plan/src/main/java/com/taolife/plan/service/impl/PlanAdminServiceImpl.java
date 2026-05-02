package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.AcupointPlan;
import com.taolife.plan.entity.ExercisePlan;
import com.taolife.plan.entity.FoodPlan;
import com.taolife.plan.entity.LifestylePlan;
import com.taolife.plan.entity.MeridianPlan;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.mapper.AcupointPlanMapper;
import com.taolife.plan.mapper.ExercisePlanMapper;
import com.taolife.plan.mapper.FoodPlanMapper;
import com.taolife.plan.mapper.LifestylePlanMapper;
import com.taolife.plan.mapper.MeridianPlanMapper;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.service.IPlanAdminService;
import com.taolife.plan.vo.UserPlanDetailAdminVO;
import com.taolife.plan.vo.UserPlanAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户方案管理服务实现类（管理后台）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanAdminServiceImpl implements IPlanAdminService {

    private final UserPlanMapper userPlanMapper;
    private final FoodPlanMapper foodPlanMapper;
    private final ExercisePlanMapper exercisePlanMapper;
    private final AcupointPlanMapper acupointPlanMapper;
    private final MeridianPlanMapper meridianPlanMapper;
    private final LifestylePlanMapper lifestylePlanMapper;

    /**
     * 分页查询用户方案
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param keyword  关键词（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<UserPlanAdminVO> getUserPlanPage(Integer pageNo, Integer pageSize, String keyword, Integer status) {
        Page<UserPlan> page = userPlanMapper.selectAdminPage(new Page<>(pageNo, pageSize), keyword, status);
        List<UserPlanAdminVO> voList = page.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 获取用户方案详情
     * 查询方案基本信息及5个子方案内容
     *
     * @param id 方案ID
     * @return 方案详细信息
     */
    @Override
    public UserPlanDetailAdminVO getUserPlanDetail(String id) {
        UserPlan plan = userPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "方案不存在");
        }
        return buildDetailVO(plan);
    }

    /**
     * 修改用户方案状态
     *
     * @param id     方案ID
     * @param status 状态值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserPlanStatus(String id, Integer status) {
        UserPlan plan = userPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "方案不存在");
        }
        plan.setStatus(status);
        plan.setUpdateTime(LocalDateTime.now());
        userPlanMapper.update(plan);
        log.info("修改用户方案状态：id={}, status={}", id, status);
    }

    private UserPlanAdminVO convertToListVO(UserPlan plan) {
        UserPlanAdminVO vo = new UserPlanAdminVO();
        vo.setId(plan.getId());
        vo.setAccountId(plan.getAccountId());
        vo.setConstitutionName(plan.getConstitutionName());
        vo.setSeason(plan.getSeason());
        vo.setSeasonName(plan.getSeasonName());
        vo.setPlanTitle(plan.getPlanTitle());
        vo.setStatus(plan.getStatus());
        vo.setCompletionRate(plan.getCompletionRate());
        vo.setTotalTasks(plan.getTotalTasks());
        vo.setCompletedTasks(plan.getCompletedTasks());
        vo.setUserRating(plan.getUserRating());
        vo.setAdjustmentCount(plan.getAdjustmentCount());
        vo.setStartDate(plan.getStartDate() != null ? plan.getStartDate().toString() : null);
        vo.setEndDate(plan.getEndDate() != null ? plan.getEndDate().toString() : null);
        vo.setCreateTime(plan.getCreateTime() != null ? plan.getCreateTime().toString() : null);
        return vo;
    }

    private UserPlanDetailAdminVO buildDetailVO(UserPlan plan) {
        UserPlanDetailAdminVO vo = new UserPlanDetailAdminVO();
        vo.setId(plan.getId());
        vo.setAccountId(plan.getAccountId());
        vo.setConstitutionCode(plan.getConstitutionCode());
        vo.setConstitutionName(plan.getConstitutionName());
        vo.setSeason(plan.getSeason());
        vo.setSeasonName(plan.getSeasonName());
        vo.setStartDate(plan.getStartDate() != null ? plan.getStartDate().toString() : null);
        vo.setEndDate(plan.getEndDate() != null ? plan.getEndDate().toString() : null);
        vo.setCycleDays(plan.getCycleDays());
        vo.setStatus(plan.getStatus());
        vo.setCompletionRate(plan.getCompletionRate());
        vo.setTotalTasks(plan.getTotalTasks());
        vo.setCompletedTasks(plan.getCompletedTasks());
        vo.setUserRating(plan.getUserRating());
        vo.setIsEffective(plan.getIsEffective());
        vo.setAdjustmentCount(plan.getAdjustmentCount());
        vo.setAiAdjustmentCount(plan.getAiAdjustmentCount());
        vo.setPlanTitle(plan.getPlanTitle());
        vo.setPlanTags(plan.getPlanTags());
        vo.setUserNotes(plan.getUserNotes());
        vo.setUserFeedback(plan.getUserFeedback());
        vo.setCreateTime(plan.getCreateTime() != null ? plan.getCreateTime().toString() : null);
        vo.setUpdateTime(plan.getUpdateTime() != null ? plan.getUpdateTime().toString() : null);

        // 聚合5个子方案内容
        if (plan.getFoodPlanId() != null) {
            FoodPlan foodPlan = foodPlanMapper.selectByIdForAdmin(plan.getFoodPlanId());
            if (foodPlan != null) {
                vo.setFoodPlanContent(foodPlan.getGeneratedContent());
                vo.setFoodPlanTags(foodPlan.getTags());
            }
        }
        if (plan.getExercisePlanId() != null) {
            ExercisePlan exercisePlan = exercisePlanMapper.selectByIdForAdmin(plan.getExercisePlanId());
            if (exercisePlan != null) {
                vo.setExercisePlanContent(exercisePlan.getGeneratedContent());
                vo.setExercisePlanTags(exercisePlan.getTags());
            }
        }
        if (plan.getAcupointPlanId() != null) {
            AcupointPlan acupointPlan = acupointPlanMapper.selectByIdForAdmin(plan.getAcupointPlanId());
            if (acupointPlan != null) {
                vo.setAcupointPlanContent(acupointPlan.getGeneratedContent());
                vo.setAcupointPlanTags(acupointPlan.getTags());
            }
        }
        if (plan.getMeridianPlanId() != null) {
            MeridianPlan meridianPlan = meridianPlanMapper.selectByIdForAdmin(plan.getMeridianPlanId());
            if (meridianPlan != null) {
                vo.setMeridianPlanContent(meridianPlan.getGeneratedContent());
                vo.setMeridianPlanTags(meridianPlan.getTags());
            }
        }
        if (plan.getLifestylePlanId() != null) {
            LifestylePlan lifestylePlan = lifestylePlanMapper.selectByIdForAdmin(plan.getLifestylePlanId());
            if (lifestylePlan != null) {
                vo.setLifestylePlanContent(lifestylePlan.getGeneratedContent());
                vo.setLifestylePlanTags(lifestylePlan.getTags());
            }
        }

        return vo;
    }
}
