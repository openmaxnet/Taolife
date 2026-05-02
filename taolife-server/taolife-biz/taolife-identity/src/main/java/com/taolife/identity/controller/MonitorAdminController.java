package com.taolife.identity.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.monitor.MonitorDataService;
import com.taolife.common.monitor.vo.ClientMetricsAggVO;
import com.taolife.common.monitor.vo.MonitorMetricsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 监控面板 API
 * 提供后端系统指标、客户端性能指标查询
 */
@RestController
@RequestMapping("/api/admin/monitor")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RequiredArgsConstructor
public class MonitorAdminController {

    private final MonitorDataService monitorService;

    /**
     * 获取后端系统指标（JVM/HTTP/DB/业务）
     */
    @GetMapping("/metrics")
    public ExceptionResult<MonitorMetricsVO> getMetrics() {
        return ExceptionResult.success(monitorService.getMetrics());
    }

    /**
     * 获取客户端指标聚合（按来源）
     */
    @GetMapping("/client-metrics")
    public ExceptionResult<ClientMetricsAggVO> getClientMetrics(
            @RequestParam(defaultValue = "admin") String source,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "20") int limit) {
        return ExceptionResult.success(monitorService.getClientMetrics(source, type, limit));
    }
}
