package com.taolife.aichat.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 会话VO
 * 用于返回会话信息给前端
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class SessionVO {

    /**
     * 会话ID
     * 唯一标识一个会话
     */
    private String sessionId;

    /**
     * 会话标题
     * 根据第一条消息自动生成
     */
    private String sessionTitle;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
