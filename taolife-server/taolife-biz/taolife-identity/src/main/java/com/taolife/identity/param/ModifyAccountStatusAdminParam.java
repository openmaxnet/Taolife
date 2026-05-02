package com.taolife.identity.param;

import lombok.Data;

/**
 * 管理员修改账号状态参数
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
public class ModifyAccountStatusAdminParam {

    /**
     * 账号ID
     */
    private String id;

    /**
     * 禁用标记（0：启用，1：禁用）
     */
    private Integer isDisabled;
}
