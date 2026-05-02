package com.taolife.aichat.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 删除消息请求参数
 * 用于删除指定会话中的单条消息
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class DeleteMessageParam {

    /**
     * 会话ID
     * 消息所属的会话
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 消息ID
     * 要删除的消息的唯一标识
     */
    @NotBlank(message = "消息ID不能为空")
    private String messageId;
}
