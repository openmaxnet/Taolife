package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanShareRecord;
import com.taolife.plan.mapper.PlanShareRecordMapper;
import com.taolife.plan.service.IShareRecordAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 方案分享记录管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShareRecordAdminServiceImpl implements IShareRecordAdminService {

    private final PlanShareRecordMapper planShareRecordMapper;

    /**
     * 分页查询方案分享记录
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public PageResult<PlanShareRecord> getShareRecordPage(Integer pageNo, Integer pageSize) {
        Page<PlanShareRecord> page = planShareRecordMapper.selectAdminPage(new Page<>(pageNo, pageSize));
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }
}
