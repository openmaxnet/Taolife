package com.taolife.identity.service.impl;

import com.taolife.identity.entity.SysPermission;
import com.taolife.identity.entity.SysRole;
import com.taolife.identity.entity.SysRolePermission;
import com.taolife.identity.entity.SysUser;
import com.taolife.identity.mapper.SysPermissionMapper;
import com.taolife.identity.mapper.SysRoleMapper;
import com.taolife.identity.mapper.SysRolePermissionMapper;
import com.taolife.identity.mapper.SysUserMapper;
import com.taolife.identity.service.IAuthAdminService;
import com.taolife.identity.vo.LoginAdminVO;
import com.taolife.identity.vo.RouteAdminVO;
import com.taolife.identity.vo.UserInfoAdminVO;
import com.taolife.identity.vo.PermissionTreeVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.security.JwtManager;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员认证服务实现类
 * 处理管理员登录认证、令牌管理、权限树构建等核心业务逻辑
 *
 * @author 文二
 * @date 2026-04-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAdminServiceImpl implements IAuthAdminService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final JwtManager jwtManager;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    /**
     * 管理员登录
     * 验证用户名密码，生成JWT令牌，更新登录信息
     *
     * @param username 用户名
     * @param password 密码
     * @param clientIp 客户端IP地址
     * @return 登录结果（包含token和用户信息）
     * @throws BusinessException 用户名或密码错误、账号被禁用、角色不存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginAdminVO adminLogin(String username, String password, String clientIp) {
        // 根据用户名查询用户
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ExceptionCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 检查账号是否被禁用
        if (user.getIsDisabled() != null && user.getIsDisabled() == 1) {
            throw new BusinessException(ExceptionCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 验证密码（BCrypt匹配）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ExceptionCode.PASSWORD_ERROR, "用户名或密码错误");
        }

        // 查询用户角色
        SysRole role = sysRoleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "用户角色不存在");
        }

        // 生成双Token
        List<String> roles = List.of(role.getRoleCode());
        String accessToken = jwtManager.generateAccessToken(user.getId(), roles);
        String refreshToken = jwtManager.generateRefreshToken(user.getId(), roles);

        // 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(clientIp);
        sysUserMapper.update(user);

        log.info("管理员登录成功：username={}, role={}", username, role.getRoleCode());
        meterRegistry.counter("biz.user.login", "source", "admin", "status", "success").increment();

        // 构建返回结果
        UserInfoAdminVO userInfoVO = convertToUserInfoVO(user, role);
        return new LoginAdminVO(accessToken, refreshToken, userInfoVO);
    }

    /**
     * 管理员退出登录
     * 移除服务端token记录
     *
     * @param adminId 管理员ID
     */
    @Override
    public void adminLogout(String adminId) {
        // 移除服务端token
        jwtManager.removeToken(adminId);
        log.info("管理员退出登录：adminId={}", adminId);
    }

    /**
     * 刷新管理员令牌
     * 验证refreshToken，签发新的双Token
     */
    @Override
    public LoginAdminVO refreshToken(String refreshToken) {
        if (!jwtManager.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ExceptionCode.REFRESH_TOKEN_INVALID, "刷新令牌无效或已过期");
        }

        String userId = jwtManager.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new BusinessException(ExceptionCode.REFRESH_TOKEN_INVALID, "无法获取用户信息");
        }

        List<String> roles = jwtManager.getRolesFromToken(refreshToken);
        if (roles == null || roles.isEmpty()) {
            roles = List.of("USER");
        }

        // 签发新的双Token
        String newAccessToken = jwtManager.generateAccessToken(userId, roles);
        String newRefreshToken = jwtManager.generateRefreshToken(userId, roles);

        // 查询用户信息
        SysUser user = sysUserMapper.selectById(userId);
        SysRole role = user != null ? sysRoleMapper.selectById(user.getRoleId()) : null;
        UserInfoAdminVO userInfoVO = convertToUserInfoVO(user, role);

        log.info("管理员刷新令牌成功：adminId={}", userId);
        return new LoginAdminVO(newAccessToken, newRefreshToken, userInfoVO);
    }

    /**
     * 获取当前管理员信息
     * 根据管理员ID查询用户信息和角色信息
     *
     * @param adminId 管理员ID
     * @return 管理员用户信息
     * @throws BusinessException 管理员不存在
     */
    @Override
    public UserInfoAdminVO getAdminUserInfo(String adminId) {
        // 查询用户信息
        SysUser user = sysUserMapper.selectById(adminId);
        if (user == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "管理员不存在");
        }

        // 查询角色信息
        SysRole role = sysRoleMapper.selectById(user.getRoleId());
        return convertToUserInfoVO(user, role);
    }

    /**
     * 获取当前角色的权限树
     * 查询角色关联的权限列表，自动补全缺失的父级节点，构建完整树形结构
     *
     * @param adminId 管理员ID
     * @return 权限树列表
     * @throws BusinessException 管理员不存在
     */
    @Override
    public List<PermissionTreeVO> getAdminPermissionTree(String adminId) {
        // 查询用户信息
        SysUser user = sysUserMapper.selectById(adminId);
        if (user == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "管理员不存在");
        }

        // 查询角色关联的权限记录
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectByRoleId(user.getRoleId());
        if (rolePermissions.isEmpty()) {
            return List.of();
        }

        // 提取权限ID列表
        List<String> permissionIds = rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .toList();

        // 批量查询启用的权限
        List<SysPermission> permissions = sysPermissionMapper.selectEnabledByIds(permissionIds);

        // 构建权限树
        return buildPermissionTree(permissions);
    }

    /**
     * 获取当前管理员可访问的路由列表
     * 查询角色关联的菜单权限，返回扁平路由列表
     *
     * @param adminId 管理员ID
     * @return 扁平路由列表
     */
    @Override
    public List<RouteAdminVO> getAdminRoutes(String adminId) {
        // 查询用户信息
        SysUser user = sysUserMapper.selectById(adminId);
        if (user == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "管理员不存在");
        }

        // 查询角色关联的权限记录
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectByRoleId(user.getRoleId());
        if (rolePermissions.isEmpty()) {
            return List.of();
        }

        // 提取权限ID列表
        List<String> permissionIds = rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .toList();

        // 批量查询启用的权限（仅菜单类型）
        List<SysPermission> permissions = sysPermissionMapper.selectEnabledByIds(permissionIds).stream()
            .filter(p -> p.getPermissionType() != null && p.getPermissionType() == 1)
            .toList();

        // 补全缺失的父级目录节点
        List<String> allIds = permissions.stream().map(SysPermission::getId).toList();
        List<String> missingParentIds = permissions.stream()
            .map(SysPermission::getParentId)
            .filter(id -> id != null && !allIds.contains(id))
            .distinct()
            .toList();

        List<SysPermission> allPermissions = new ArrayList<>(permissions);
        if (!missingParentIds.isEmpty()) {
            allPermissions.addAll(sysPermissionMapper.selectEnabledByParentIds(missingParentIds));
        }

        // 转换为路由VO（扁平列表）
        return allPermissions.stream()
            .map(this::convertToRouteVO)
            .toList();
    }

    /**
     * 构建权限树
     * 自动补全缺失的父级节点，构建完整的树形结构
     *
     * @param permissions 权限列表
     * @return 权限树列表（仅顶级节点）
     */
    private List<PermissionTreeVO> buildPermissionTree(List<SysPermission> permissions) {
        // 收集当前权限列表中的所有ID
        List<String> allIds = permissions.stream().map(SysPermission::getId).toList();

        // 查找缺失的父级节点（parentId不在当前列表中的）
        List<String> parentIds = permissions.stream()
            .map(SysPermission::getParentId)
            .filter(id -> id != null && !allIds.contains(id))
            .distinct()
            .toList();

        // 合并当前权限和补全的父级节点
        List<SysPermission> allPermissions = new ArrayList<>(permissions);
        if (!parentIds.isEmpty()) {
            // 批量查询缺失的父级节点
            List<SysPermission> parentNodes = sysPermissionMapper.selectEnabledByParentIds(parentIds);
            allPermissions.addAll(parentNodes);
        }

        // 转换为VO
        List<PermissionTreeVO> allVOs = allPermissions.stream()
            .map(this::convertToPermissionTreeVO)
            .toList();

        // 按父ID分组，构建父子关系
        Map<String, List<PermissionTreeVO>> childrenMap = allVOs.stream()
            .filter(vo -> vo.getParentId() != null)
            .collect(Collectors.groupingBy(PermissionTreeVO::getParentId));

        // 设置每个节点的子节点列表
        allVOs.forEach(vo -> vo.setChildren(childrenMap.get(vo.getId())));

        // 过滤出顶级节点并按排序序号排序
        return allVOs.stream()
            .filter(vo -> vo.getParentId() == null || vo.getParentId().isEmpty())
            .sorted(Comparator.comparingInt(vo -> vo.getSortOrder() != null ? vo.getSortOrder() : 0))
            .toList();
    }

    /**
     * 用户实体转用户信息VO
     *
     * @param user 用户实体
     * @param role 角色实体
     * @return 用户信息VO
     */
    private UserInfoAdminVO convertToUserInfoVO(SysUser user, SysRole role) {
        UserInfoAdminVO vo = new UserInfoAdminVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatarUrl(user.getAvatarUrl());
        // 填充角色信息
        if (role != null) {
            vo.setRoleCodes(List.of(role.getRoleCode()));
            vo.setRoleNames(List.of(role.getRoleName()));
        }
        return vo;
    }

    /**
     * 权限实体转权限树VO
     *
     * @param permission 权限实体
     * @return 权限树VO
     */
    private PermissionTreeVO convertToPermissionTreeVO(SysPermission permission) {
        PermissionTreeVO vo = new PermissionTreeVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setPermissionName(permission.getPermissionName());
        vo.setPermissionCode(permission.getPermissionCode());
        vo.setPermissionType(permission.getPermissionType());
        vo.setPath(permission.getPath());
        vo.setComponent(permission.getComponent());
        vo.setIcon(permission.getIcon());
        vo.setSortOrder(permission.getSortOrder());
        return vo;
    }

    /**
     * 权限实体转路由VO
     *
     * @param permission 权限实体
     * @return 路由VO
     */
    private RouteAdminVO convertToRouteVO(SysPermission permission) {
        RouteAdminVO vo = new RouteAdminVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setPermissionCode(permission.getPermissionCode());
        vo.setPath(permission.getPath());
        vo.setPermissionName(permission.getPermissionName());
        vo.setIcon(permission.getIcon());
        vo.setComponent(permission.getComponent());
        vo.setSortOrder(permission.getSortOrder());
        vo.setPermissionType(permission.getPermissionType());
        return vo;
    }
}
