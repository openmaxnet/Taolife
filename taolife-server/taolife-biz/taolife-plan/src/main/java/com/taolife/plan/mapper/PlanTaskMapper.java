package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanTask;
import com.taolife.plan.param.PlanTaskQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 方案任务Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface PlanTaskMapper extends BaseMapper<PlanTask> {

    /**
     * 根据ID查询方案任务
     *
     * @param id 任务ID
     * @return 方案任务对象
     */
    default PlanTask selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanTask::getId).eq(id)
                .limit(1)
        );
    }

    /**
     * 根据用户方案ID分页查询任务列表
     *
     * @param userPlanId 用户方案ID
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<PlanTask> selectByUserPlanId(String userPlanId, PlanTaskQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        wrapper.where(PlanTask::getUserPlanId).eq(userPlanId);

        if (param.getPlanType() != null) {
            wrapper.and(PlanTask::getPlanType).eq(param.getPlanType());
        }

        if (param.getTaskDate() != null) {
            wrapper.and(PlanTask::getTaskDate).eq(param.getTaskDate());
        }

        if (param.getStatus() != null) {
            wrapper.and(PlanTask::getStatus).eq(param.getStatus());
        }

        wrapper.orderBy(PlanTask::getCreateTime, false);

        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 查询某方案下的所有任务（用于统计汇总）
     *
     * @param userPlanId 用户方案ID
     * @return 该方案的全部任务列表
     */
    default List<PlanTask> selectAllByUserPlanId(String userPlanId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
        );
    }

    /**
     * 统计某方案下未完成的任务数（status=1）
     *
     * @param userPlanId 用户方案ID
     * @return 未完成任务数
     */
    default long countPendingByUserPlanId(String userPlanId) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
                .and(PlanTask::getStatus).eq(1)
        );
    }

    /**
     * 统计某方案下的任务总数
     *
     * @param userPlanId 用户方案ID
     * @return 任务总数
     */
    default long countByUserPlanId(String userPlanId) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
        );
    }

    /**
     * 统计某方案下已完成的任务数（status=2）
     *
     * @param userPlanId 用户方案ID
     * @return 已完成任务数
     */
    default long countCompletedByUserPlanId(String userPlanId) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
                .and(PlanTask::getStatus).eq(2)
        );
    }

    /**
     * 根据用户方案ID和日期查询任务列表
     *
     * @param userPlanId 用户方案ID
     * @param taskDate 任务日期
     * @return 任务列表
     */
    default List<PlanTask> selectByUserPlanIdAndDate(String userPlanId, LocalDate taskDate) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
                .and(PlanTask::getTaskDate).eq(taskDate)
                .orderBy(PlanTask::getCreateTime, false)
        );
    }

    /**
     * 统计某方案下指定类型的任务总数
     *
     * @param userPlanId 用户方案ID
     * @param planType   方案类型
     * @return 任务总数
     */
    default long countByUserPlanIdAndType(String userPlanId, Integer planType) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
                .and(PlanTask::getPlanType).eq(planType)
        );
    }

    /**
     * 统计某方案下指定类型已完成的任务数（status=2）
     *
     * @param userPlanId 用户方案ID
     * @param planType   方案类型
     * @return 已完成任务数
     */
    default long countCompletedByUserPlanIdAndType(String userPlanId, Integer planType) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(PlanTask::getUserPlanId).eq(userPlanId)
                .and(PlanTask::getPlanType).eq(planType)
                .and(PlanTask::getStatus).eq(2)
        );
    }

    /**
     * 管理后台分页查询方案任务列表
     *
     * @param page       分页参数
     * @param userPlanId 用户方案ID（可选）
     * @return 分页结果
     */
    default Page<PlanTask> selectAdminPage(Page<PlanTask> page, String userPlanId) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (userPlanId != null && !userPlanId.isEmpty()) {
            wrapper.where(PlanTask::getUserPlanId).eq(userPlanId);
        }
        wrapper.orderBy(PlanTask::getTaskDate, false);
        return paginate(page, wrapper);
    }
}
