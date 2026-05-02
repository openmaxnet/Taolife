package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI模型分页查询参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiModelPageParam {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /**
     * 厂商ID
     */
    private String providerId;

    /**
     * 模型类型：chat/embedding
     */
    private String modelType;

    /**
     * 关键词搜索
     */
    private String keyword;
}
