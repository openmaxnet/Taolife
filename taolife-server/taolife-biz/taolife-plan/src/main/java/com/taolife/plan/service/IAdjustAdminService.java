package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanAdjustRecord;

/**
 * 方案调整记录管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IAdjustAdminService {

    /**
     * 分页查询方案调整记录
     *
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @param userPlanId 用户方案ID（可选）
     * @return 分页结果
     */
    PageResult<PlanAdjustRecord> getAdjustmentPage(Integer pageNo, Integer pageSize, String userPlanId);
}
