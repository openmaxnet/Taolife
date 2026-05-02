package com.taolife.aicore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenAI API 兼容的消息模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * 角色：system、user、assistant、tool
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    public static Message system(String content) {
        return new Message("system", content);
    }

    public static Message user(String content) {
        return new Message("user", content);
    }

    public static Message assistant(String content) {
        return new Message("assistant", content);
    }
}
