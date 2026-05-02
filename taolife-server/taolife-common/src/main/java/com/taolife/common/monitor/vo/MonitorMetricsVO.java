package com.taolife.common.monitor.vo;

import lombok.Data;

/**
 * 监控面板汇总指标
 */
@Data
public class MonitorMetricsVO {
    private JvmMetricsVO jvm;
    private DbPoolMetricsVO dbPool;
    private HttpMetricsVO http;
    private BusinessMetricsVO business;
    private String timestamp;
}
