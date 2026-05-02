package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员会话VO
 * 用于返回会话信息给管理端
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SessionAdminVO {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户账号ID
     */
    private String accountId;

    /**
     * 会话标题
     */
    private String sessionTitle;

    /**
     * 关联的体质记录ID
     */
    private String constitutionId;

    /**
     * 关联的体质名称
     */
    private String constitutionName;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 最后一条消息
     */
    private String lastMessage;

    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 聊天模型
     */
    private String chatModel;

    /**
     * 向量模型
     */
    private String embeddingModel;

    /**
     * 状态：0-已结束，1-进行中
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
