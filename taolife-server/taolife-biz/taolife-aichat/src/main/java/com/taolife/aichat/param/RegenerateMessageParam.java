package com.taolife.aichat.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 重新生成消息请求参数
 * 用于重新生成指定AI回答
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class RegenerateMessageParam {

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 要重新生成的AI消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private String messageId;

    /**
     * 是否启用思考模式
     */
    private Boolean enableThinking = false;
}
