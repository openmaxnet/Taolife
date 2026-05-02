package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI厂商端点配置分页查询参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiProviderEndpointPageParam {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /**
     * 厂商ID
     */
    private String providerId;

    /**
     * 端点类型
     */
    private String endpointType;

    /**
     * 关键词搜索
     */
    private String keyword;
}