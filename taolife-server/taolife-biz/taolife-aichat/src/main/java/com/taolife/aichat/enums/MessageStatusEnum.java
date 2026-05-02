package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举
 * 标记AI回复消息的生成状态，用于精准排查问题
 *
 * @author 文二
 * @date 2026-04-16
 */
@Getter
@AllArgsConstructor
public enum MessageStatusEnum {

    /**
     * 生成中（不落库，仅前端使用）
     */
    STREAMING(0, "生成中"),

    /**
     * 正常完成
     */
    COMPLETE(1, "完成"),

    /**
     * LLM调用失败（API超时、网络错误、模型返回异常等）
     */
    ERROR_LLM(2, "LLM调用失败"),

    /**
     * 安全检测拒绝（敏感词命中、问题分类拒绝等）
     */
    ERROR_SAFETY(3, "安全检测拒绝"),

    /**
     * 用户中断（停止按钮、前端abort等）
     */
    INTERRUPTED(4, "用户中断");

    private final Integer value;
    private final String desc;
}
