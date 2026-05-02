package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问答消息实体
 * 对应数据库表 tf_chat_message
 * 存储用户问题和AI回答的完整记录
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
@Table("tl_ai_chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     * 唯一标识一条消息
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 会话ID
     * 消息所属的会话
     */
    private String sessionId;

    /**
     * 账号ID
     * 消息所属的用户账号
     */
    private String accountId;

    /**
     * 角色
     * 1-user，2-assistant，3-system
     * 与智谱API保持一致的角色定义
     */
    private Integer role;

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
     * 问题类型
     * 1-体质类，2-食疗类，3-穴位类，4-养生类，5-其他
     */
    private Integer questionType;

    /**
     * 是否安全
     * 0-否，1-是（通过敏感词检测和问题分类）
     */
    private Integer isSafe;

    /**
     * 风险原因
     * 当消息不安全时，记录具体的风险原因
     */
    private String riskReason;

    /**
     * 引用的知识库文档ID
     * JSON数组格式，记录AI回答时参考的知识库文档
     */
    private String knowledgeRefs;

    /**
     * 使用的模型
     * AI回答时使用的模型名称
     */
    private String modelUsed;

    /**
     * 消耗的Token数
     * AI回答时消耗的Token数量
     */
    private Integer tokensUsed;

    /**
     * 响应时间（毫秒）
     * AI生成回答所花费的时间
     */
    private Integer responseTime;

    /**
     * 是否删除
     * 0-否，1-是（逻辑删除）
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 消息状态
     * 0-生成中，1-完成，2-LLM调用失败，3-安全检测拒绝，4-用户中断
     */
    private Integer status = 1;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
