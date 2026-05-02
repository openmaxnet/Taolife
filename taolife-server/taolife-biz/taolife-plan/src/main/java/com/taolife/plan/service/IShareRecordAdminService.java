package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanShareRecord;

/**
 * 方案分享记录管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IShareRecordAdminService {

    /**
     * 分页查询方案分享记录列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PlanShareRecord> getShareRecordPage(Integer pageNo, Integer pageSize);
}
