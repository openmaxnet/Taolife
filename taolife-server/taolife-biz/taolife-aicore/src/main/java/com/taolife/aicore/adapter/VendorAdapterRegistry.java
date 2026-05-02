package com.taolife.aicore.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 厂商适配器注册中心
 * 自动收集所有 VendorAdapter 实现，按 vendorCode 索引
 */
@Slf4j
@Component
public class VendorAdapterRegistry {

    private final Map<String, VendorAdapter> adapters;
    private final VendorAdapter defaultAdapter;

    /**
     * 构造适配器注册中心，自动收集所有VendorAdapter实现
     *
     * @param adapterList 所有VendorAdapter实例列表
     */
    public VendorAdapterRegistry(List<VendorAdapter> adapterList) {
        this.adapters = adapterList.stream()
                .collect(Collectors.toMap(adapter -> adapter.getVendorCode().toLowerCase(), Function.identity()));
        this.defaultAdapter = adapters.get("openai");
        log.info("已注册 {} 个厂商适配器: {}", adapters.size(), adapters.keySet());
    }

    /**
     * 根据厂商编码获取适配器（不区分大小写）
     * 未找到时回退到 OpenAI 标准适配器
     */
    public VendorAdapter getAdapter(String vendorCode) {
        if (vendorCode == null || vendorCode.isEmpty()) {
            return defaultAdapter;
        }
        VendorAdapter adapter = adapters.get(vendorCode.toLowerCase());
        if (adapter == null) {
            log.warn("未找到厂商适配器: {}，回退到OpenAI标准适配器", vendorCode);
            return defaultAdapter;
        }
        return adapter;
    }

    /**
     * 判断是否存在指定厂商的适配器（不区分大小写）
     *
     * @param vendorCode 厂商编码
     * @return 是否存在
     */
    public boolean hasAdapter(String vendorCode) {
        return adapters.containsKey(vendorCode.toLowerCase());
    }
}
