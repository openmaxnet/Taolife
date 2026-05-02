package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员消息VO
 * 用于返回消息信息给管理端
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class MessageAdminVO {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户账号ID
     */
    private String accountId;

    /**
     * 角色：1-user，2-assistant，3-system
     */
    private Integer role;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 问题类型：1-体质类，2-食疗类，3-穴位类，4-养生类，5-其他
     */
    private Integer questionType;

    /**
     * 问题类型名称
     */
    private String questionTypeName;

    /**
     * 是否安全：0-否，1-是
     */
    private Integer isSafe;

    /**
     * 风险原因
     */
    private String riskReason;

    /**
     * 引用的知识库文档ID（JSON数组）
     */
    private String knowledgeRefs;

    /**
     * 使用的模型
     */
    private String modelUsed;

    /**
     * 消耗的Token数
     */
    private Integer tokensUsed;

    /**
     * 响应时间（毫秒）
     */
    private Integer responseTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
