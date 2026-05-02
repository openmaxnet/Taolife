package com.taolife.common.monitor;

import com.taolife.common.monitor.vo.ClientMetricsAggVO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * 客户端指标环形缓冲区
 * 存储 Admin 前端 Web Vitals 和小程序性能数据
 */
@Component
public class ClientMetricsBuffer {

    private static final int MAX_RECORDS = 1000;

    private final ConcurrentLinkedDeque<ClientMetricEntry> buffer = new ConcurrentLinkedDeque<>();

    /**
     * 写入一条客户端指标
     */
    public void add(String source, String type, String name, double value,
                    String rating, Map<String, String> tags) {
        buffer.addLast(new ClientMetricEntry(source, type, name, value, rating, tags, Instant.now().toEpochMilli()));
        while (buffer.size() > MAX_RECORDS) {
            buffer.removeFirst();
        }
    }

    /**
     * 按 source 聚合指标，计算百分位数
     */
    public ClientMetricsAggVO aggregate(String source, String type, int limit) {
        List<ClientMetricEntry> entries = buffer.stream()
                .filter(e -> e.source.equals(source))
                .filter(e -> type == null || e.type.equals(type))
                .collect(Collectors.toList());

        ClientMetricsAggVO vo = new ClientMetricsAggVO();
        vo.setSource(source);
        vo.setSampleCount(entries.size());

        // 按 name 分组计算百分位数
        Map<String, List<ClientMetricEntry>> byName = entries.stream()
                .collect(Collectors.groupingBy(e -> e.name));

        Map<String, ClientMetricsAggVO.PercentileValue> metrics = new LinkedHashMap<>();
        for (var entry : byName.entrySet()) {
            List<Double> values = entry.getValue().stream()
                    .map(e -> e.value)
                    .sorted()
                    .collect(Collectors.toList());
            if (!values.isEmpty()) {
                ClientMetricsAggVO.PercentileValue pv = new ClientMetricsAggVO.PercentileValue();
                pv.setP50(percentile(values, 50));
                pv.setP95(percentile(values, 95));
                pv.setP99(percentile(values, 99));
                metrics.put(entry.getKey(), pv);
            }
        }
        vo.setMetrics(metrics);

        // 最近 N 条记录
        List<ClientMetricsAggVO.ClientMetricRecord> recent = entries.stream()
                .skip(Math.max(0, entries.size() - limit))
                .map(e -> {
                    ClientMetricsAggVO.ClientMetricRecord r = new ClientMetricsAggVO.ClientMetricRecord();
                    r.setType(e.type);
                    r.setName(e.name);
                    r.setValue(e.value);
                    r.setRating(e.rating);
                    r.setTimestamp(e.timestamp);
                    r.setTags(e.tags);
                    return r;
                })
                .collect(Collectors.toList());
        vo.setRecentRecords(recent);

        return vo;
    }

    private double percentile(List<Double> sorted, int p) {
        if (sorted.isEmpty()) return 0;
        int idx = (int) Math.ceil(p / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(idx, sorted.size() - 1)));
    }

    public static class ClientMetricEntry {
        public final String source;
        public final String type;
        public final String name;
        public final double value;
        public final String rating;
        public final Map<String, String> tags;
        public final long timestamp;

        public ClientMetricEntry(String source, String type, String name, double value,
                                 String rating, Map<String, String> tags, long timestamp) {
            this.source = source;
            this.type = type;
            this.name = name;
            this.value = value;
            this.rating = rating;
            this.tags = tags != null ? tags : Map.of();
            this.timestamp = timestamp;
        }
    }
}
