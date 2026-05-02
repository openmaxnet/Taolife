package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanCycleReport;

/**
 * 方案周期报告管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface ICycleReportAdminService {

    /**
     * 分页查询方案周期报告列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PlanCycleReport> getCycleReportPage(Integer pageNo, Integer pageSize);
}
