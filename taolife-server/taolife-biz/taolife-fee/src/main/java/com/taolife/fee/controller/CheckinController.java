package com.taolife.fee.controller;

import com.taolife.fee.param.CheckinCalendarParam;
import com.taolife.fee.service.ICheckinService;
import com.taolife.fee.vo.CheckinCalendarVO;
import com.taolife.fee.vo.CheckinResultVO;
import com.taolife.fee.vo.CheckinDailyStatusVO;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 每日签到控制器
 * 处理每日签到相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-04
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final ICheckinService dailyCheckinService;

    /**
     * 获取今日签到状态
     * 查询用户今日是否可以签到
     *
     * @return 今日签到状态
     */
    @GetMapping("/getTodayCheckinStatus")
    public ExceptionResult<CheckinDailyStatusVO> getTodayCheckinStatus() {
        String accountId = UserContext.getAccountId();
        CheckinDailyStatusVO result = dailyCheckinService.getTodayCheckinStatus(accountId);
        return ExceptionResult.success(result);
    }

    /**
     * 执行每日签到
     * 用户进行每日签到操作
     *
     * @return 签到结果
     */
    @PostMapping("/dailyCheckin")
    public ExceptionResult<CheckinResultVO> dailyCheckin() {
        String accountId = UserContext.getAccountId();
        CheckinResultVO result = dailyCheckinService.dailyCheckin(accountId);
        return ExceptionResult.success(result);
    }

    /**
     * 获取签到日历
     * 查询指定月份的签到记录
     *
     * @param param 查询参数
     * @return 签到日历
     */
    @GetMapping("/getCheckinCalendar")
    public ExceptionResult<CheckinCalendarVO> getCheckinCalendar(@Valid CheckinCalendarParam param) {
        String accountId = UserContext.getAccountId();
        CheckinCalendarVO result = dailyCheckinService.getCheckinCalendar(
            accountId, param.getYear(), param.getMonth()
        );
        return ExceptionResult.success(result);
    }
}
