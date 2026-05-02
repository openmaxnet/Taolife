package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.CheckinRecord;

/**
 * 签到管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface ICheckinAdminService {

    /**
     * 分页查询签到记录
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<CheckinRecord> getCheckinRecordPage(Integer pageNo, Integer pageSize);
}
