package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI提示词模板分页查询参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiPromptTemplatePageParam {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /**
     * 模板类型：system_prompt/user_prompt/assistant_prompt
     */
    private String templateType;

    /**
     * 关键词搜索
     */
    private String keyword;
}
