package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.param.PlanSquareActionParam;
import com.taolife.plan.param.PlanSquareQueryParam;
import com.taolife.plan.param.PlanSquareShareParam;
import com.taolife.plan.vo.PlanSquareDetailVO;
import com.taolife.plan.vo.PlanSquareListVO;

/**
 * 方案广场服务接口
 * 定义方案广场相关的业务操作
 *
 * @author 文二
 * @date 2026-04-06
 */
public interface IPlanSquareService {

    /**
     * 分页查询方案广场列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<PlanSquareListVO> getSquarePage(PlanSquareQueryParam param);

    /**
     * 获取方案广场详情
     *
     * @param id 方案广场ID
     * @return 方案广场详情
     */
    PlanSquareDetailVO getSquareDetail(String id);

    /**
     * 分享方案到广场
     *
     * @param accountId 账号ID
     * @param param 分享参数
     */
    void shareToSquare(String accountId, PlanSquareShareParam param);

    /**
     * 方案广场操作（点赞/收藏）
     *
     * @param accountId 账号ID
     * @param param 操作参数
     */
    void planAction(String accountId, PlanSquareActionParam param);
}
