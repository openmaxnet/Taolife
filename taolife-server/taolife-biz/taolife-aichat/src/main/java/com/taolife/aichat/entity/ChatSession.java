package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问答会话实体
 * 对应数据库表 tf_chat_session
 * 支持多会话隔离：每个会话独立存储上下文，用户可创建多个会话且不串上下文
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
@Table("tl_ai_chat_session")
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     * 唯一标识一个会话
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     * 会话所属的用户账号
     */
    private String accountId;

    /**
     * 会话标题
     * 根据第一条消息自动生成
     */
    private String sessionTitle;

    /**
     * 关联的体质记录ID
     * 用于关联用户的体质评估结果，提供个性化建议
     */
    private String constitutionId;

    /**
     * 消息数量
     * 会话中的消息总数（用户消息+AI回答）
     */
    private Integer messageCount;

    /**
     * 最后一条消息
     * 用于在会话列表中预览最新对话内容
     */
    private String lastMessage;

    /**
     * 最后一条消息时间
     * 记录最后一次对话的时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 聊天模型
     * 支持的模型：glm-flash、glm-4
     */
    private String chatModel;

    /**
     * 向量模型
     * 支持的模型：qwen-embedding、bge-m3
     */
    private String embeddingModel;

    /**
     * 状态
     * 0-已结束，1-进行中
     */
    private Integer status;

    /**
     * 最后AI回复状态
     * 1-完成，2-LLM调用失败，3-安全检测拒绝，4-用户中断
     */
    private Integer lastMessageStatus = 1;

    /**
     * 是否删除
     * 0-否，1-是（逻辑删除）
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
