package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI状态修改参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiStatusParam {

    /**
     * ID
     */
    private String id;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
