package com.taolife.plan.task;

import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 方案到期定时任务
 * 每日凌晨扫描到期方案，根据任务完成情况更新状态
 *
 * @author 文二
 * @date 2026-04-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlanExpireTask {

    private final UserPlanMapper userPlanMapper;
    private final PlanTaskMapper planTaskMapper;

    /**
     * 每天凌晨2点扫描到期方案
     * status=1(进行中) 且 endDate < 今天 的方案，检查是否还有未完成任务：
     * - 无未完成任务 → status=2(已完成)
     * - 有未完成任务 → status=3(已过期)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void expireCompletedPlans() {
        log.info("开始执行方案到期检查...");

        LocalDate today = LocalDate.now();

        // 查询所有到期且进行中的方案
        List<UserPlan> expiredPlans = userPlanMapper.selectExpiredActivePlans(today);

        if (expiredPlans.isEmpty()) {
            log.info("无到期方案");
            return;
        }

        int completedCount = 0;
        int expiredCount = 0;

        for (UserPlan plan : expiredPlans) {
            // 检查是否还有未完成的任务（status=1）
            long pendingCount = planTaskMapper.countPendingByUserPlanId(plan.getId());

            // 统计任务完成情况
            long total = planTaskMapper.countByUserPlanId(plan.getId());
            long completed = planTaskMapper.countCompletedByUserPlanId(plan.getId());

            int totalTasks = (int) total;
            int completedTasks = (int) completed;
            int completionRate = totalTasks > 0 ? (int) (completedTasks * 100 / totalTasks) : 0;

            if (pendingCount == 0) {
                plan.setStatus(2); // COMPLETED
                completedCount++;
                log.info("方案已完成, planId: {}", plan.getId());
            } else {
                plan.setStatus(3); // EXPIRED
                expiredCount++;
                log.info("方案已过期, planId: {}, 未完成任务数: {}", plan.getId(), pendingCount);
            }

            plan.setTotalTasks(totalTasks);
            plan.setCompletedTasks(completedTasks);
            plan.setCompletionRate(BigDecimal.valueOf(completionRate));
            plan.setUpdateTime(LocalDateTime.now());
            userPlanMapper.update(plan);
        }

        log.info("方案到期检查完成, 总计: {}, 已完成: {}, 已过期: {}",
                expiredPlans.size(), completedCount, expiredCount);
    }
}
