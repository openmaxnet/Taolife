package com.taolife.aichat.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 删除会话请求参数
 * 用于删除指定的会话
 *
 * @author 文二
 * @date 2026-04-01
 */
@Data
public class RemoveSessionParam {

    /**
     * 会话ID
     * 要删除的会话的唯一标识
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
}
