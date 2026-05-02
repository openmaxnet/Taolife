package com.taolife.plan.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.plan.param.PlanTaskQueryParam;
import com.taolife.plan.param.TaskCompleteParam;
import com.taolife.plan.service.IPlanTaskService;
import com.taolife.plan.vo.PlanTaskDetailVO;
import com.taolife.plan.vo.PlanTaskListVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 方案任务控制器
 * 处理方案任务相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@RestController
@RequestMapping("/api/plan/task")
@RequiredArgsConstructor
public class PlanTaskController {

    private final IPlanTaskService planTaskService;

    /**
     * 分页查询任务列表
     * 根据方案ID分页查询任务列表
     *
     * @param userPlanId 用户方案ID
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getTaskPage")
    public ExceptionResult<PageResult<PlanTaskListVO>> getTaskPage(
            @RequestParam("userPlanId") String userPlanId,
            @Valid PlanTaskQueryParam param) {
        log.info("分页查询任务列表，userPlanId: {}, param: {}", userPlanId, param);
        return ExceptionResult.success(planTaskService.getTaskPage(userPlanId, param));
    }

    /**
     * 获取任务详情
     * 根据任务ID获取任务详细信息
     *
     * @param id 任务ID
     * @return 任务详情
     */
    @GetMapping("/getTaskDetail")
    public ExceptionResult<PlanTaskDetailVO> getTaskDetail(@RequestParam("id") String id) {
        log.info("获取任务详情，id: {}", id);
        return ExceptionResult.success(planTaskService.getTaskDetail(id));
    }

    /**
     * 完成任务
     * 标记任务为已完成状态
     *
     * @param param 完成任务参数
     * @return 操作结果
     */
    @PostMapping("/completeTask")
    public ExceptionResult<Void> completeTask(@Valid @RequestBody TaskCompleteParam param) {
        String accountId = UserContext.getAccountId();
        log.info("完成任务，accountId: {}, param: {}", accountId, param);
        planTaskService.completeTask(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 跳过任务
     * 标记任务为已跳过状态
     *
     * @param id 任务ID
     * @return 操作结果
     */
    @PostMapping("/skipTask")
    public ExceptionResult<Void> skipTask(@RequestParam("id") String id) {
        String accountId = UserContext.getAccountId();
        log.info("跳过任务，accountId: {}, taskId: {}", accountId, id);
        planTaskService.skipTask(accountId, id);
        return ExceptionResult.success();
    }
}
