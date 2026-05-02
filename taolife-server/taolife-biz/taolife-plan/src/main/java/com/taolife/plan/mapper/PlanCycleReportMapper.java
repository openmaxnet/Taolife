package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanCycleReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方案周期报告Mapper
 *
 * @author 文二
 * @date 2026-04-08
 */
@Mapper
public interface PlanCycleReportMapper extends BaseMapper<PlanCycleReport> {

    /**
     * 查询用户最新的未读周期报告
     *
     * @param accountId 账号ID
     * @return 最新的未读报告，无则返回null
     */
    default PlanCycleReport selectUnreadByAccountId(String accountId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanCycleReport::getAccountId).eq(accountId)
                .and(PlanCycleReport::getIsRead).eq(0)
                .orderBy(PlanCycleReport::getCreateTime, false)
                .limit(1)
        );
    }

    /**
     * 查询用户指定方案关联的未读周期报告
     * 用于只返回当前活跃方案对应的上周期报告
     *
     * @param accountId 账号ID
     * @param newPlanId 当前活跃方案ID
     * @return 未读报告，无则返回null
     */
    default PlanCycleReport selectUnreadByAccountIdAndNewPlanId(String accountId, String newPlanId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanCycleReport::getAccountId).eq(accountId)
                .and(PlanCycleReport::getIsRead).eq(0)
                .and(PlanCycleReport::getNewPlanId).eq(newPlanId)
                .orderBy(PlanCycleReport::getCreateTime, false)
                .limit(1)
        );
    }

    /**
     * 查询指定方案的周期报告（用于历史方案查看自身执行总结）
     *
     * @param planId 方案ID（被总结的旧方案）
     * @param accountId 账号ID
     * @return 周期报告，无则返回null
     */
    default PlanCycleReport selectByPlanIdAndAccountId(String planId, String accountId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanCycleReport::getPlanId).eq(planId)
                .and(PlanCycleReport::getAccountId).eq(accountId)
                .orderBy(PlanCycleReport::getCreateTime, false)
                .limit(1)
        );
    }

    /**
     * 管理后台分页查询方案周期报告列表
     * 注意：PlanCycleReport没有isDeleted字段，无需逻辑删除过滤
     *
     * @param page 分页参数
     * @return 分页结果
     */
    default Page<PlanCycleReport> selectAdminPage(Page<PlanCycleReport> page) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.orderBy(PlanCycleReport::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
