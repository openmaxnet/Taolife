package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员用户信息视图对象
 * 用于返回管理员的用户信息和角色信息给前端
 *
 * @author 文二
 * @date 2026-04-10
 */
@Data
@NoArgsConstructor
public class UserInfoAdminVO {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 角色编码列表（支持多角色）
     */
    private List<String> roleCodes;

    /**
     * 角色名称列表
     */
    private List<String> roleNames;
}
