package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanTask;
import com.taolife.plan.mapper.PlanTaskMapper;
import com.taolife.plan.service.ITaskAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 方案任务管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAdminServiceImpl implements ITaskAdminService {

    private final PlanTaskMapper planTaskMapper;

    /**
     * 分页查询方案任务
     *
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @param userPlanId 用户方案ID（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<PlanTask> getTaskPage(Integer pageNo, Integer pageSize, String userPlanId) {
        Page<PlanTask> page = planTaskMapper.selectAdminPage(new Page<>(pageNo, pageSize), userPlanId);
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }
}
