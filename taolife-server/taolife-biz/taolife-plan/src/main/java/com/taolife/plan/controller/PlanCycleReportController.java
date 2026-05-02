package com.taolife.plan.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.plan.entity.PlanCycleReport;
import com.taolife.plan.service.IPlanCycleReportService;
import com.taolife.plan.vo.PlanCycleReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 方案周期报告接口
 *
 * @author 文二
 * @date 2026-04-08
 */
@Slf4j
@RestController
@RequestMapping("/api/plan/cycleReport")
@RequiredArgsConstructor
public class PlanCycleReportController {

    private final IPlanCycleReportService planCycleReportService;

    /**
     * 获取未读的周期报告
     *
     * @return 未读报告VO，无未读时返回null
     */
    @GetMapping("/getUnread")
    public ExceptionResult<PlanCycleReportVO> getUnread() {
        String accountId = UserContext.getAccountId();
        PlanCycleReport report = planCycleReportService.getUnreadReport(accountId);
        if (report == null) {
            return ExceptionResult.success(null);
        }
        PlanCycleReportVO vo = new PlanCycleReportVO();
        vo.setId(report.getId());
        vo.setPlanId(report.getPlanId());
        vo.setNewPlanId(report.getNewPlanId());
        vo.setReportContent(report.getReportContent());
        return ExceptionResult.success(vo);
    }

    /**
     * 根据方案ID获取周期报告（用于历史方案查看自身执行总结）
     *
     * @param planId 方案ID
     * @return 周期报告VO，无报告时返回null
     */
    @GetMapping("/getByPlanId")
    public ExceptionResult<PlanCycleReportVO> getByPlanId(@RequestParam("planId") String planId) {
        String accountId = UserContext.getAccountId();
        PlanCycleReport report = planCycleReportService.getReportByPlanId(planId, accountId);
        if (report == null) {
            return ExceptionResult.success(null);
        }
        PlanCycleReportVO vo = new PlanCycleReportVO();
        vo.setId(report.getId());
        vo.setPlanId(report.getPlanId());
        vo.setNewPlanId(report.getNewPlanId());
        vo.setReportContent(report.getReportContent());
        return ExceptionResult.success(vo);
    }

    /**
     * 标记报告为已读
     *
     * @param reportId 报告ID
     * @return 操作结果
     */
    @PostMapping("/markRead")
    public ExceptionResult<Void> markRead(@RequestParam("reportId") String reportId) {
        String accountId = UserContext.getAccountId();
        log.info("标记周期报告已读, reportId: {}, accountId: {}", reportId, accountId);
        planCycleReportService.markRead(reportId, accountId);
        return ExceptionResult.success(null);
    }
}
