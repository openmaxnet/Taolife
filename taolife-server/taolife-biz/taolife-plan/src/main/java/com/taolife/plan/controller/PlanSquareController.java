package com.taolife.plan.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.plan.param.PlanSquareActionParam;
import com.taolife.plan.param.PlanSquareQueryParam;
import com.taolife.plan.param.PlanSquareShareParam;
import com.taolife.plan.service.IPlanSquareService;
import com.taolife.plan.vo.PlanSquareDetailVO;
import com.taolife.plan.vo.PlanSquareListVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 方案广场控制器
 * 处理方案广场相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@RestController
@RequestMapping("/api/plan/square")
@RequiredArgsConstructor
public class PlanSquareController {

    private final IPlanSquareService planSquareService;

    /**
     * 分页查询方案广场列表
     * 根据条件分页查询方案广场中的方案
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getSquarePage")
    public ExceptionResult<PageResult<PlanSquareListVO>> getSquarePage(@Valid PlanSquareQueryParam param) {
        log.info("分页查询方案广场列表，param: {}", param);
        return ExceptionResult.success(planSquareService.getSquarePage(param));
    }

    /**
     * 获取方案广场详情
     * 根据方案广场ID获取详细信息
     *
     * @param id 方案广场ID
     * @return 方案广场详情
     */
    @GetMapping("/getSquareDetail")
    public ExceptionResult<PlanSquareDetailVO> getSquareDetail(@RequestParam("id") String id) {
        log.info("获取方案广场详情，id: {}", id);
        return ExceptionResult.success(planSquareService.getSquareDetail(id));
    }

    /**
     * 分享方案到广场
     * 将用户的方案分享到方案广场
     *
     * @param param 分享参数
     * @return 操作结果
     */
    @PostMapping("/shareToSquare")
    public ExceptionResult<Void> shareToSquare(@Valid @RequestBody PlanSquareShareParam param) {
        String accountId = UserContext.getAccountId();
        log.info("分享方案到广场，accountId: {}, param: {}", accountId, param);
        planSquareService.shareToSquare(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 方案广场操作（点赞/收藏）
     * 统一处理点赞、取消点赞、收藏、取消收藏操作
     *
     * @param param 操作参数（actionType：1-点赞，2-取消点赞，3-收藏，4-取消收藏）
     * @return 操作结果
     */
    @PostMapping("/planAction")
    public ExceptionResult<Void> planAction(@Valid @RequestBody PlanSquareActionParam param) {
        String accountId = UserContext.getAccountId();
        log.info("方案广场操作，accountId: {}, param: {}", accountId, param);
        planSquareService.planAction(accountId, param);
        return ExceptionResult.success();
    }
}
