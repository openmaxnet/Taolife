namespace Entity {
  /** 监控面板汇总指标 */
  interface MonitorMetrics {
    jvm: JvmMetrics
    dbPool: DbPoolMetrics
    http: HttpMetrics
    business: BusinessMetrics
    timestamp: string
  }

  /** JVM 运行时指标 */
  interface JvmMetrics {
    heapUsed: number
    heapMax: number
    heapUsage: number
    nonHeapUsed: number
    activeThreads: number
    peakThreads: number
    systemCpuUsage: number
    processCpuUsage: number
    uptimeSeconds: number
  }

  /** 数据库连接池指标 */
  interface DbPoolMetrics {
    activeConnections: number
    idleConnections: number
    maxConnections: number
    pendingThreads: number
    usage: number
  }

  /** HTTP 请求指标 */
  interface HttpMetrics {
    requestsPerMinute: number
    avgResponseTimeMs: number
    maxResponseTimeMs: number
    totalRequests: number
  }

  /** 业务指标 */
  interface BusinessMetrics {
    userRegisterTotal: number
    userLoginTotal: number
    orderCreateTotal: number
    aiRequestTotal: number
    aiRequestAvgTimeMs: number
    customCounters: Record<string, number>
  }

  /** 客户端指标聚合 */
  interface ClientMetricsAgg {
    source: string
    sampleCount: number
    metrics: Record<string, ClientPercentileValue>
    recentRecords: ClientMetricRecord[]
  }

  /** 百分位数值 */
  interface ClientPercentileValue {
    p50: number
    p95: number
    p99: number
  }

  /** 客户端指标记录 */
  interface ClientMetricRecord {
    type: string
    name: string
    value: number
    rating?: string
    timestamp: number
    tags?: Record<string, string>
  }
}
