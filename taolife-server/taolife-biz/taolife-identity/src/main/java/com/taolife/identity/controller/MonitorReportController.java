package com.taolife.identity.controller;

import com.taolife.common.annotation.AuthSkip;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.monitor.MonitorDataService;
import com.taolife.common.monitor.vo.ClientMetricReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户端指标上报 API（公开接口，无需认证）
 */
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorReportController {

    private final MonitorDataService monitorService;

    /**
     * 接收客户端（Admin/小程序）指标上报
     */
    @PostMapping("/report")
    @AuthSkip(reason = "客户端指标上报接口，无需登录")
    public ExceptionResult<Void> reportMetrics(@RequestBody ClientMetricReportVO report) {
        if (report.getMetrics() != null) {
            for (ClientMetricReportVO.MetricItem item : report.getMetrics()) {
                monitorService.reportClientMetric(
                        report.getSource() != null ? report.getSource() : "admin",
                        item.getType(),
                        item.getName(),
                        item.getValue(),
                        item.getRating(),
                        item.getTags()
                );
            }
        }
        return ExceptionResult.success();
    }
}
