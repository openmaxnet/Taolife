package com.taolife.identity.service;

import com.taolife.identity.vo.LoginAdminVO;
import com.taolife.identity.vo.RouteAdminVO;
import com.taolife.identity.vo.UserInfoAdminVO;
import com.taolife.identity.vo.PermissionTreeVO;

import java.util.List;

/**
 * 管理员认证服务接口
 * 提供管理员登录、登出、用户信息查询、权限树获取等能力
 *
 * @author 文二
 * @date 2026-04-10
 */
public interface IAuthAdminService {

    /**
     * 管理员登录
     * 验证用户名密码，生成JWT令牌，返回用户信息和权限数据
     *
     * @param username 用户名
     * @param password 密码
     * @param clientIp 客户端IP地址
     * @return 登录结果（包含token和用户信息）
     * @throws com.taolife.common.exception.BusinessException 用户名或密码错误、账号被禁用、角色不存在
     */
    LoginAdminVO adminLogin(String username, String password, String clientIp);

    /**
     * 管理员退出登录
     * 移除服务端token记录
     *
     * @param adminId 管理员ID
     */
    void adminLogout(String adminId);

    /**
     * 获取当前管理员信息
     * 根据管理员ID查询用户信息和角色信息
     *
     * @param adminId 管理员ID
     * @return 管理员用户信息
     * @throws com.taolife.common.exception.BusinessException 管理员不存在
     */
    UserInfoAdminVO getAdminUserInfo(String adminId);

    /**
     * 获取当前角色的权限树
     * 根据管理员角色查询关联的菜单和按钮权限，构建树形结构
     *
     * @param adminId 管理员ID
     * @return 权限树列表
     * @throws com.taolife.common.exception.BusinessException 管理员不存在
     */
    List<PermissionTreeVO> getAdminPermissionTree(String adminId);

    /**
     * 获取当前管理员可访问的路由列表
     * 根据管理员角色查询关联的菜单权限，返回扁平路由列表
     *
     * @param adminId 管理员ID
     * @return 扁平路由列表
     */
    List<RouteAdminVO> getAdminRoutes(String adminId);

    /**
     * 刷新管理员令牌
     * 使用refreshToken换取新的accessToken和refreshToken
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录结果（包含新的双token和用户信息）
     * @throws com.taolife.common.exception.BusinessException refreshToken无效或已过期
     */
    LoginAdminVO refreshToken(String refreshToken);
}
