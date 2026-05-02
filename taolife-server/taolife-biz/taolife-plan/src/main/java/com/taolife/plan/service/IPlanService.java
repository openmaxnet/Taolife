package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.param.PlanQueryParam;
import com.taolife.plan.param.PlanFeedbackParam;
import com.taolife.plan.vo.PlanDetailVO;
import com.taolife.plan.vo.PlanHistoryVO;
import com.taolife.plan.vo.PlanSummaryVO;

/**
 * 健康方案服务接口
 * 定义健康方案相关的业务操作
 *
 * @author 文二
 * @date 2026-04-06
 */
public interface IPlanService {

    /**
     * 获取最新的养生方案摘要
     *
     * @param accountId 用户ID
     * @return 方案摘要
     */
    PlanSummaryVO getHealthPlan(String accountId);

    /**
     * 获取方案详情
     *
     * @param id 方案ID
     * @return 方案详情
     */
    PlanDetailVO getHealthPlanDetail(String id);

    /**
     * 分页查询方案历史记录
     *
     * @param accountId 用户ID
     * @param param     查询参数
     * @return 分页结果
     */
    PageResult<PlanHistoryVO> getPlanHistoryPage(String accountId, PlanQueryParam param);

    /**
     * 提交反馈
     *
     * @param accountId 用户ID
     * @param param     反馈参数
     */
    void submitFeedback(String accountId, PlanFeedbackParam param);
}
