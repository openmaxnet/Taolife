package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员敏感词状态修改参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ModifySensitiveWordStatusAdminParam {

    private String id;

    private Integer isEnabled;
}
