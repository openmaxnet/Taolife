package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanSquare;

/**
 * 方案广场管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface ISquareAdminService {

    /**
     * 分页查询方案广场列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param status   状态（可选）
     * @return 分页结果
     */
    PageResult<PlanSquare> getSquarePage(Integer pageNo, Integer pageSize, Integer status);

    /**
     * 修改方案广场状态
     *
     * @param id     方案广场ID
     * @param status 状态
     */
    void modifySquareStatus(String id, Integer status);
}
