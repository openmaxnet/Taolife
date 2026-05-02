package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.AdLogPageParam;
import com.taolife.fee.vo.AdLogAdminVO;

/**
 * 广告展示日志管理端服务
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IAdDisplayLogAdminService {

    /**
     * 分页获取广告日志列表
     * 根据条件分页获取广告日志信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AdLogAdminVO> getAdLogPage(AdLogPageParam param);
}
