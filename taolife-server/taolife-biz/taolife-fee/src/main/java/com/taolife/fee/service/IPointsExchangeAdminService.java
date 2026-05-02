package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.PointsExchangePageParam;
import com.taolife.fee.vo.PointsExchangeAdminVO;

/**
 * 积分兑换记录管理端服务
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IPointsExchangeAdminService {

    /**
     * 分页获取积分兑换记录列表
     * 根据条件分页获取积分兑换记录信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<PointsExchangeAdminVO> getExchangeRecordPage(PointsExchangePageParam param);
}
