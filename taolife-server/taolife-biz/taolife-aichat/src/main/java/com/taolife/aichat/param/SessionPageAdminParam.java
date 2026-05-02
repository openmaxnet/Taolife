package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员会话分页查询参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SessionPageAdminParam {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 用户账号ID（精确搜索）
     */
    private String accountId;

    /**
     * 关键词搜索（标题或最后消息）
     */
    private String keyword;
}
