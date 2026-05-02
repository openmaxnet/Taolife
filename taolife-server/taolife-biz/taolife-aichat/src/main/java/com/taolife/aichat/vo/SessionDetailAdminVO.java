package com.taolife.aichat.vo;

import lombok.Data;

import java.util.List;

/**
 * 管理员会话详情VO
 * 包含会话信息和消息列表
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SessionDetailAdminVO {

    /**
     * 会话基本信息
     */
    private SessionAdminVO session;

    /**
     * 消息列表
     */
    private List<MessageAdminVO> messages;
}
