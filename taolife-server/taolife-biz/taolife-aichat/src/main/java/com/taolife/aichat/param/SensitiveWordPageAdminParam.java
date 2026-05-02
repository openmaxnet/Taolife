package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员敏感词分页查询参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SensitiveWordPageAdminParam {

    private Integer pageNo;

    private Integer pageSize;

    private Integer wordType;

    private String keyword;
}
