package com.taolife.common.monitor.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 客户端（Admin/小程序）性能指标聚合
 */
@Data
public class ClientMetricsAggVO {
    /** 来源：admin / miniapp */
    private String source;
    /** 采样总数 */
    private int sampleCount;
    /** Web Vitals / API 延迟 百分位数 */
    private Map<String, PercentileValue> metrics;
    /** 最近上报的原始记录（最多 20 条） */
    private List<ClientMetricRecord> recentRecords;

    @Data
    public static class PercentileValue {
        private double p50;
        private double p95;
        private double p99;
    }

    @Data
    public static class ClientMetricRecord {
        private String type;
        private String name;
        private double value;
        private String rating;
        private long timestamp;
        private Map<String, String> tags;
    }
}
