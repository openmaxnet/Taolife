package com.taolife.aichat.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 会话列表查询参数
 * 用于分页查询会话列表和消息列表
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class SessionPageParam {

    /**
     * 会话ID（查询会话详情时需要）
     * 要查询的会话的唯一标识
     */
    private String sessionId;

    /**
     * 页码
     * 从1开始计数
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNo;

    /**
     * 每页数量
     * 单页最大返回记录数
     */
    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量必须大于0")
    private Integer pageSize;
}
