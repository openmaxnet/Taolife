package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanCycleReport;
import com.taolife.plan.mapper.PlanCycleReportMapper;
import com.taolife.plan.service.ICycleReportAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 方案周期报告管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CycleReportAdminServiceImpl implements ICycleReportAdminService {

    private final PlanCycleReportMapper planCycleReportMapper;

    /**
     * 分页查询方案周期报告
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public PageResult<PlanCycleReport> getCycleReportPage(Integer pageNo, Integer pageSize) {
        Page<PlanCycleReport> page = planCycleReportMapper.selectAdminPage(new Page<>(pageNo, pageSize));
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }
}
