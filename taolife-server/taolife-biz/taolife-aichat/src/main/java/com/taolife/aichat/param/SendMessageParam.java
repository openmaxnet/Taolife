package com.taolife.aichat.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送消息请求参数
 * 用于向AI问答会话发送用户问题
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class SendMessageParam {

    /**
     * 会话ID
     * 指定要发送消息的会话
     */
    @NotNull(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 消息内容
     * 用户提出的问题或对话内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 是否开启流式输出
     * true-流式输出，false-一次性返回完整回答
     */
    private Boolean stream = false;

    /**
     * 是否启用思考模式
     * true-启用，AI会在回答前进行推理思考
     */
    private Boolean enableThinking = false;
}
