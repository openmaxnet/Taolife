package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI场景配置分页查询参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiSceneConfigPageParam {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /**
     * 关键词搜索
     */
    private String keyword;
}
