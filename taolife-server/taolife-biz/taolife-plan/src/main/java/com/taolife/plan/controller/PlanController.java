package com.taolife.plan.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.plan.param.GeneratePlanParam;
import com.taolife.plan.param.PlanQueryParam;
import com.taolife.plan.param.PlanFeedbackParam;
import com.taolife.plan.service.IPlanService;
import com.taolife.plan.service.IPlanGenerationService;
import com.taolife.plan.vo.GeneratePlanVO;
import com.taolife.plan.vo.GenerationStatusVO;
import com.taolife.plan.vo.PlanDetailVO;
import com.taolife.plan.vo.PlanHistoryVO;
import com.taolife.plan.vo.PlanSummaryVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 健康方案控制器
 * 处理用户健康方案相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final IPlanService planService;
    private final IPlanGenerationService planGenerationService;

    /**
     * 获取养生方案
     * 根据当前登录用户查询最新的养生方案
     *
     * @return 养生方案摘要
     */
    @GetMapping("/getHealthPlan")
    public ExceptionResult<PlanSummaryVO> getHealthPlan() {
        String accountId = UserContext.getAccountId();
        log.info("获取养生方案，accountId: {}", accountId);
        return ExceptionResult.success(planService.getHealthPlan(accountId));
    }

    /**
     * 获取方案详情
     * 根据方案ID获取方案详细信息
     *
     * @param id 方案ID
     * @return 方案详情
     */
    @GetMapping("/getHealthPlanDetail")
    public ExceptionResult<PlanDetailVO> getHealthPlanDetail(@RequestParam("id") String id) {
        log.info("获取方案详情，id: {}", id);
        return ExceptionResult.success(planService.getHealthPlanDetail(id));
    }

    /**
     * 分页查询方案历史
     * 分页查询当前登录用户的方案历史记录
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getPlanHistoryPage")
    public ExceptionResult<PageResult<PlanHistoryVO>> getPlanHistoryPage(@Valid PlanQueryParam param) {
        String accountId = UserContext.getAccountId();
        log.info("分页查询方案历史，accountId: {}, param: {}", accountId, param);
        return ExceptionResult.success(planService.getPlanHistoryPage(accountId, param));
    }

    /**
     * 提交方案生成请求（异步）
     * 根据用户体质、季节、偏好等信息生成个性化养生方案
     *
     * @return 生成结果信息
     */
    @PostMapping("/generate")
    public ExceptionResult<GeneratePlanVO> generatePlan(@RequestBody GeneratePlanParam param) {
        String accountId = UserContext.getAccountId();
        log.info("提交方案生成请求，accountId: {}, cycleDays: {}", accountId, param.getCycleDays());
        GeneratePlanVO vo = planGenerationService.submitGenerationTask(accountId, param.getCycleDays());
        return ExceptionResult.success(vo);
    }

    /**
     * 查询生成任务状态
     * 根据任务ID查询方案生成的进度和状态
     *
     * @param taskId 任务ID
     * @return 生成状态
     */
    @GetMapping("/getGenerationStatus")
    public ExceptionResult<GenerationStatusVO> getGenerationStatus(@RequestParam("taskId") String taskId) {
        log.info("查询生成任务状态，taskId: {}", taskId);
        GenerationStatusVO vo = planGenerationService.getGenerationStatus(taskId);
        return ExceptionResult.success(vo);
    }

    /**
     * 提交反馈
     * 统一处理方案反馈和调整效果反馈
     *
     * @param param 反馈参数（通过feedbackType区分反馈类型：1-方案反馈，2-调整效果反馈）
     * @return 操作结果
     */
    @PostMapping("/submitFeedback")
    public ExceptionResult<Void> submitFeedback(@Valid @RequestBody PlanFeedbackParam param) {
        String accountId = UserContext.getAccountId();
        log.info("提交反馈，accountId: {}, param: {}", accountId, param);
        planService.submitFeedback(accountId, param);
        return ExceptionResult.success();
    }
}
