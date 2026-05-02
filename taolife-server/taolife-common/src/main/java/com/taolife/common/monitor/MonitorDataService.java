package com.taolife.common.monitor;

import com.taolife.common.monitor.vo.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 聚合系统指标：JVM、HTTP、DB、业务计数器
 */
@Service
@RequiredArgsConstructor
public class MonitorDataService {

    private final MeterRegistry registry;
    private final ClientMetricsBuffer clientBuffer;

    /**
     * 获取完整监控指标
     */
    public MonitorMetricsVO getMetrics() {
        MonitorMetricsVO vo = new MonitorMetricsVO();
        vo.setJvm(getJvmMetrics());
        vo.setDbPool(getDbPoolMetrics());
        vo.setHttp(getHttpMetrics());
        vo.setBusiness(getBusinessMetrics());
        vo.setTimestamp(Instant.now().toString());
        return vo;
    }

    /**
     * JVM 运行时指标
     */
    public JvmMetricsVO getJvmMetrics() {
        JvmMetricsVO vo = new JvmMetricsVO();
        MemoryMXBean memoryMx = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMx.getHeapMemoryUsage();
        RuntimeMXBean runtimeMx = ManagementFactory.getRuntimeMXBean();
        ThreadMXBean threadMx = ManagementFactory.getThreadMXBean();
        OperatingSystemMXBean osMx = ManagementFactory.getOperatingSystemMXBean();

        double mb = 1024.0 * 1024.0;
        vo.setHeapUsed(heap.getUsed() / mb);
        vo.setHeapMax(heap.getMax() / mb);
        vo.setHeapUsage(heap.getMax() > 0 ? (double) heap.getUsed() / heap.getMax() : 0);
        vo.setNonHeapUsed(memoryMx.getNonHeapMemoryUsage().getUsed() / mb);
        vo.setActiveThreads(threadMx.getThreadCount());
        vo.setPeakThreads(threadMx.getPeakThreadCount());
        vo.setUptimeSeconds(runtimeMx.getUptime() / 1000);

        // com.sun.management 扩展接口
        if (osMx instanceof com.sun.management.OperatingSystemMXBean sunOsMx) {
            vo.setSystemCpuUsage(sunOsMx.getCpuLoad());
            vo.setProcessCpuUsage(sunOsMx.getProcessCpuLoad());
        }
        return vo;
    }

    /**
     * 数据库连接池指标
     */
    public DbPoolMetricsVO getDbPoolMetrics() {
        DbPoolMetricsVO vo = new DbPoolMetricsVO();
        // 从 Micrometer 的 HikariPool 指标读取
        Double active = getGauge("hikaricp.connections.active");
        Double idle = getGauge("hikaricp.connections.idle");
        Double max = getGauge("hikaricp.connections.max");
        Double pending = getGauge("hikaricp.connections.pending");

        vo.setActiveConnections(active != null ? active.intValue() : 0);
        vo.setIdleConnections(idle != null ? idle.intValue() : 0);
        vo.setMaxConnections(max != null ? max.intValue() : 0);
        vo.setPendingThreads(pending != null ? pending.intValue() : 0);
        vo.setUsage(max != null && max > 0 && active != null ? active / max : 0);
        return vo;
    }

    /**
     * HTTP 请求指标
     */
    public HttpMetricsVO getHttpMetrics() {
        HttpMetricsVO vo = new HttpMetricsVO();

        // Spring Boot 自动注册的 HTTP Server 请求指标
        Double total = getCounter("http.server.requests");
        vo.setTotalRequests(total != null ? total : 0);

        // 从 Timer 获取响应时间
        Timer timer = registry.find("http.server.requests").timer();
        if (timer != null && timer.count() > 0) {
            vo.setAvgResponseTimeMs(timer.mean(TimeUnit.MILLISECONDS));
            vo.setMaxResponseTimeMs(timer.max(TimeUnit.MILLISECONDS));
            // 近 1 分钟速率
            double perSec = timer.count() > 0
                    ? (double) timer.count() / Math.max(1, timer.totalTime(TimeUnit.SECONDS)) * 60
                    : 0;
            // 取合理值，避免启动初期比率过高
            vo.setRequestsPerMinute(Math.min(perSec, timer.count()));
        }
        return vo;
    }

    /**
     * 业务计数器指标
     */
    public BusinessMetricsVO getBusinessMetrics() {
        BusinessMetricsVO vo = new BusinessMetricsVO();
        vo.setUserRegisterTotal(getBizCounter("biz.user.register"));
        vo.setUserLoginTotal(getBizCounter("biz.user.login"));
        vo.setOrderCreateTotal(getBizCounter("biz.order.create"));
        vo.setAiRequestTotal(getBizCounter("biz.ai.request"));

        // AI 调用平均耗时
        Timer aiTimer = registry.find("biz.ai.request").timer();
        if (aiTimer != null && aiTimer.count() > 0) {
            vo.setAiRequestAvgTimeMs(aiTimer.mean(TimeUnit.MILLISECONDS));
        }

        // 收集所有 biz. 开头的计数器
        Map<String, Double> custom = new LinkedHashMap<>();
        registry.getMeters().stream()
                .filter(m -> m.getId().getName().startsWith("biz."))
                .forEach(m -> {
                    String name = m.getId().getName();
                    double val = getBizCounter(name);
                    if (val > 0) {
                        custom.put(name, val);
                    }
                });
        vo.setCustomCounters(custom);
        return vo;
    }

    /**
     * 获取客户端（Admin/小程序）指标聚合
     */
    public ClientMetricsAggVO getClientMetrics(String source, String type, int limit) {
        return clientBuffer.aggregate(source, type, limit);
    }

    /**
     * 写入客户端指标
     */
    public void reportClientMetric(String source, String type, String name, double value,
                                   String rating, Map<String, String> tags) {
        clientBuffer.add(source, type, name, value, rating, tags);
    }

    // ---- helpers ----

    private Double getGauge(String name) {
        return registry.find(name).gauge() != null
                ? registry.find(name).gauge().value()
                : null;
    }

    private Double getCounter(String name) {
        return registry.find(name).counter() != null
                ? registry.find(name).counter().count()
                : null;
    }

    private double getBizCounter(String name) {
        var counters = registry.find(name).counters();
        if (counters == null || counters.isEmpty()) return 0;
        return counters.stream().mapToDouble(c -> c.count()).sum();
    }
}
