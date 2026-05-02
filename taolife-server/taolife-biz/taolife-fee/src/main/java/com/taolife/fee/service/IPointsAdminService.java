package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.vo.PointsRecordAdminVO;

/**
 * 积分管理服务接口（管理员）
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IPointsAdminService {

    /**
     * 分页查询积分记录
     *
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param accountId 账号ID（可选）
     * @return 积分记录分页结果
     */
    PageResult<PointsRecordAdminVO> getPointsRecordPage(Integer pageNo, Integer pageSize, String accountId);
}
