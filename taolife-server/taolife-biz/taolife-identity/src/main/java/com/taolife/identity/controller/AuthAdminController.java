package com.taolife.identity.controller;

import com.taolife.identity.param.LoginAdminParam;
import com.taolife.identity.service.IAuthAdminService;
import com.taolife.identity.vo.LoginAdminVO;
import com.taolife.identity.vo.RouteAdminVO;
import com.taolife.identity.vo.UserInfoAdminVO;
import com.taolife.identity.vo.PermissionTreeVO;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.annotation.AuthSkip;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理员认证控制器
 * 负责处理管理员登录、登出、用户信息查询、权限树获取等HTTP请求
 *
 * @author 文二
 * @date 2026-04-10
 */
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {

    private final IAuthAdminService adminAuthService;

    /**
     * 管理员登录
     * 验证用户名密码，返回JWT令牌和用户信息
     *
     * @param param 登录参数（用户名+密码）
     * @param request HTTP请求（用于获取客户端IP）
     * @return 登录结果（包含token和用户信息）
     */
    @PostMapping("/adminLogin")
    @AuthSkip(reason = "管理员登录接口无需认证", loginEndpoint = true)
    @PreAuthorize("permitAll()")
    public ExceptionResult<LoginAdminVO> adminLogin(@Valid @RequestBody LoginAdminParam param,
                                                          HttpServletRequest request) {
        // 获取客户端真实IP
        String clientIp = getClientIp(request);
        // 调用登录服务
        LoginAdminVO result = adminAuthService.adminLogin(param.getUsername(), param.getPassword(), clientIp);
        return ExceptionResult.success(result);
    }

    /**
     * 管理员退出登录
     * 移除服务端token记录
     *
     * @return 操作结果
     */
    @PostMapping("/adminLogout")
    public ExceptionResult<Void> adminLogout() {
        // 从上下文中获取当前管理员ID
        String adminId = UserContext.getAccountId();
        if (adminId != null) {
            adminAuthService.adminLogout(adminId);
        }
        return ExceptionResult.success();
    }

    /**
     * 刷新管理员令牌
     * 使用refreshToken换取新的双Token
     *
     * @param body 请求体，包含refreshToken
     * @return 新的登录结果（包含新的双token和用户信息）
     */
    @PostMapping("/refreshToken")
    @AuthSkip(reason = "刷新令牌接口无需accessToken认证")
    @PreAuthorize("permitAll()")
    public ExceptionResult<LoginAdminVO> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ExceptionResult.failed(ExceptionCode.REFRESH_TOKEN_REQUIRED);
        }
        LoginAdminVO result = adminAuthService.refreshToken(refreshToken);
        return ExceptionResult.success(result);
    }

    /**
     * 获取当前管理员信息
     * 返回当前登录管理员的用户信息和角色信息
     *
     * @return 管理员用户信息
     */
    @GetMapping("/getAdminUserInfo")
    public ExceptionResult<UserInfoAdminVO> getAdminUserInfo() {
        // 从上下文中获取当前管理员ID
        String adminId = UserContext.getAccountId();
        if (adminId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        // 查询管理员信息
        UserInfoAdminVO userInfo = adminAuthService.getAdminUserInfo(adminId);
        return ExceptionResult.success(userInfo);
    }

    /**
     * 获取当前角色的权限树
     * 返回当前管理员角色关联的菜单和按钮权限树形结构
     *
     * @return 权限树列表
     */
    @GetMapping("/getAdminPermissionTree")
    public ExceptionResult<List<PermissionTreeVO>> getAdminPermissionTree() {
        // 从上下文中获取当前管理员ID
        String adminId = UserContext.getAccountId();
        if (adminId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        // 查询权限树
        List<PermissionTreeVO> tree = adminAuthService.getAdminPermissionTree(adminId);
        return ExceptionResult.success(tree);
    }

    /**
     * 获取当前管理员可访问的路由列表
     * 返回扁平路由列表，用于前端动态路由注册
     *
     * @return 扁平路由列表
     */
    @GetMapping("/getAdminRoutes")
    public ExceptionResult<List<RouteAdminVO>> getAdminRoutes() {
        String adminId = UserContext.getAccountId();
        if (adminId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        List<RouteAdminVO> routes = adminAuthService.getAdminRoutes(adminId);
        return ExceptionResult.success(routes);
    }

    /**
     * 获取客户端真实IP地址
     * 支持多级代理场景，依次从X-Forwarded-For、X-Real-IP、RemoteAddr获取
     *
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        // 优先从代理头获取真实IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        // 最后回退到RemoteAddr
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
