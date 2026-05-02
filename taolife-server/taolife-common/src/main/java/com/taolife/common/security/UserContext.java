package com.taolife.common.security;

import java.util.List;

/**
 * 用户上下文
 * 用于在业务层获取当前登录用户的信息
 * 使用ThreadLocal存储，确保线程安全
 *
 * @author 文二
 * @date 2026-03-16
 */
public class UserContext {

    /**
     * 用户上下文ThreadLocal变量
     */
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * 用户ID（账号ID）
     */
    private String accountId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 会员过期时间
     */
    private String memberExpireTime;

    /**
     * 登录Token
     */
    private String token;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 默认构造函数
     */
    public UserContext() {
    }

    /**
     * 全参数构造函数
     *
     * @param accountId 账号ID
     * @param nickname  昵称
     */
    public UserContext(String accountId, String nickname) {
        this.accountId = accountId;
        this.nickname = nickname;
    }

    /**
     * 获取当前用户上下文
     *
     * @return 当前线程的用户上下文，如果未设置则返回null
     */
    public static UserContext get() {
        return CONTEXT.get();
    }

    /**
     * 设置当前用户上下文
     *
     * @param context 用户上下文
     */
    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    /**
     * 清除当前用户上下文
     * 在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 判断是否已登录
     *
     * @return 是否已设置用户上下文
     */
    public static boolean isLoggedIn() {
        return CONTEXT.get() != null && CONTEXT.get().accountId != null;
    }

    /**
     * 获取当前用户ID（账号ID）
     *
     * @return 账号ID，如果未登录返回null
     */
    public static String getAccountId() {
        UserContext context = CONTEXT.get();
        return context != null ? context.accountId : null;
    }

    /**
     * 设置当前用户ID
     *
     * @param accountId 账号ID
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    // Getters and Setters

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(Integer memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getMemberExpireTime() {
        return memberExpireTime;
    }

    public void setMemberExpireTime(String memberExpireTime) {
        this.memberExpireTime = memberExpireTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * 检查用户是否拥有指定角色
     */
    public boolean hasRole(String roleCode) {
        return roles != null && roles.contains(roleCode);
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "accountId='" + accountId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", memberLevel=" + memberLevel +
                '}';
    }
}
