package com.taolife.plan.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.plan.param.AiAdjustParam;
import com.taolife.plan.param.BatchAiAdjustParam;
import com.taolife.plan.param.BatchConfirmAdjustParam;
import com.taolife.plan.param.ConfirmAdjustParam;
import com.taolife.plan.param.PlanAdjustRecordQueryParam;
import com.taolife.plan.param.ManualAdjustmentParam;
import com.taolife.plan.service.IPlanAdjustService;
import com.taolife.plan.vo.AdjustmentRecordVO;
import com.taolife.plan.vo.AiAdjustResultVO;
import com.taolife.plan.vo.BatchAiAdjustResultVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * 方案调整控制器
 * 处理方案调整相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@RestController
@RequestMapping("/api/plan/adjustment")
@RequiredArgsConstructor
public class PlanAdjustController {

    private final IPlanAdjustService planAdjustService;

    /**
     * 用户手动调整方案
     *
     * @param param 手动调整参数
     * @return 操作结果
     */
    @PostMapping("/manualAdjust")
    public ExceptionResult<Void> manualAdjust(@Valid @RequestBody ManualAdjustmentParam param) {
        String accountId = UserContext.getAccountId();
        log.info("用户手动调整方案，accountId: {}, userPlanId: {}", accountId, param.getUserPlanId());
        planAdjustService.manualAdjust(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 获取智能调整推荐标签
     *
     * @param userPlanId 用户方案ID
     * @param planType   方案类型
     * @return 推荐标签列表
     */
    @GetMapping("/suggestions")
    public ExceptionResult<List<String>> getSuggestions(
            @RequestParam("userPlanId") String userPlanId,
            @RequestParam("planType") Integer planType) {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(planAdjustService.getAdjustmentSuggestions(accountId, userPlanId, planType));
    }

    /**
     * AI调整方案（单条）
     *
     * @param param AI调整参数
     * @return AI调整结果
     */
    @PostMapping("/aiAdjust")
    public ExceptionResult<AiAdjustResultVO> aiAdjust(@Valid @RequestBody AiAdjustParam param) {
        String accountId = UserContext.getAccountId();
        log.info("AI单条调整方案，accountId: {}, userPlanId: {}, planType: {}", accountId, param.getUserPlanId(), param.getPlanType());
        return ExceptionResult.success(planAdjustService.aiAdjust(accountId, param));
    }

    /**
     * AI批量调整方案
     *
     * @param param 批量AI调整参数
     * @return 批量调整结果
     */
    @PostMapping("/batchAiAdjust")
    public ExceptionResult<BatchAiAdjustResultVO> batchAiAdjust(@Valid @RequestBody BatchAiAdjustParam param) {
        String accountId = UserContext.getAccountId();
        log.info("AI批量调整方案，accountId: {}, userPlanId: {}", accountId, param.getUserPlanId());
        return ExceptionResult.success(planAdjustService.batchAiAdjust(accountId, param));
    }

    /**
     * 确认AI调整（单条）
     *
     * @param param 确认调整参数
     * @return 操作结果
     */
    @PostMapping("/confirmAiAdjust")
    public ExceptionResult<Void> confirmAiAdjust(@Valid @RequestBody ConfirmAdjustParam param) {
        String accountId = UserContext.getAccountId();
        log.info("确认AI调整，accountId: {}, adjustmentId: {}", accountId, param.getAdjustmentId());
        planAdjustService.confirmAiAdjust(accountId, param.getAdjustmentId());
        return ExceptionResult.success();
    }

    /**
     * 批量确认AI调整
     *
     * @param param 批量确认调整参数
     * @return 操作结果
     */
    @PostMapping("/batchConfirmAiAdjust")
    public ExceptionResult<Void> batchConfirmAiAdjust(@Valid @RequestBody BatchConfirmAdjustParam param) {
        String accountId = UserContext.getAccountId();
        log.info("批量确认AI调整，accountId: {}, count: {}", accountId, param.getAdjustmentIds().size());
        planAdjustService.batchConfirmAiAdjust(accountId, param.getAdjustmentIds());
        return ExceptionResult.success();
    }

    /**
     * 分页查询调整记录
     *
     * @param userPlanId 用户方案ID
     * @param param      查询参数
     * @return 调整记录分页结果
     */
    @GetMapping("/getAdjustmentRecordPage")
    public ExceptionResult<PageResult<AdjustmentRecordVO>> getAdjustmentRecordPage(
            @RequestParam("userPlanId") String userPlanId,
            @Valid PlanAdjustRecordQueryParam param) {
        log.info("分页查询调整记录，userPlanId: {}", userPlanId);
        return ExceptionResult.success(planAdjustService.getAdjustmentRecordPage(userPlanId, param));
    }
}
