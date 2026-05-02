package com.taolife.aichat.vo;

import lombok.Data;

/**
 * 消息VO
 * 用于返回消息信息给前端
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class MessageVO {

    /**
     * 消息ID
     * 唯一标识一条消息
     */
    private String messageId;

    /**
     * 会话ID
     * 消息所属的会话
     */
    private String sessionId;

    /**
     * 消息角色：1-用户问题，2-AI回答，3-系统消息
     * 与智谱API保持一致的角色定义
     */
    private String role;

    /**
     * 消息内容
     * 用户提出的问题或AI的回答内容
     */
    private String content;

    /**
     * 思考过程内容
     * AI在生成回答前的推理过程（仅assistant消息）
     */
    private String reasoningContent;

    /**
     * 创建时间
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private String createTime;

    /**
     * 消息状态
     * 1-完成，2-LLM调用失败，3-安全检测拒绝，4-用户中断
     */
    private Integer status;
}
