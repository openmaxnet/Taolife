package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI模型厂商分页查询参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiProviderPageParam {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /**
     * 厂商类型：chat/embedding/image
     */
    private String providerType;

    /**
     * 关键词搜索
     */
    private String keyword;
}
