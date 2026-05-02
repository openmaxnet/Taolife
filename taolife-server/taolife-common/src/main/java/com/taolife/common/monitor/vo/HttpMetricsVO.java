package com.taolife.common.monitor.vo;

import lombok.Data;

/**
 * HTTP 请求指标
 */
@Data
public class HttpMetricsVO {
    /** 最近 1 分钟请求数 */
    private double requestsPerMinute;
    /** 平均响应时间（ms） */
    private double avgResponseTimeMs;
    /** 最大响应时间（ms） */
    private double maxResponseTimeMs;
    /** 总请求数 */
    private double totalRequests;
}
