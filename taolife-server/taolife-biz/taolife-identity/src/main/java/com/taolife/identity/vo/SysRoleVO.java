package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统角色视图对象
 * 用于返回角色列表信息
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
@NoArgsConstructor
public class SysRoleVO {

    /**
     * 角色ID
     */
    private String id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 禁用标记（0：启用，1：禁用）
     */
    private Integer isDisabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}