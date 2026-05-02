package com.taolife.identity.service.impl;

import com.taolife.identity.entity.SysRole;
import com.taolife.identity.entity.SysRolePermission;
import com.taolife.identity.mapper.SysRoleMapper;
import com.taolife.identity.mapper.SysRolePermissionMapper;
import com.taolife.identity.param.SysRoleSaveParam;
import com.taolife.identity.service.ISysRoleService;
import com.taolife.identity.vo.SysRoleDetailVO;
import com.taolife.identity.vo.SysRoleVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统角色服务实现类
 * 实现系统角色管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-04-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 获取角色列表
     * 查询所有角色信息
     *
     * @return 角色VO列表
     */
    @Override
    public List<SysRoleVO> getSysRoleList() {
        return sysRoleMapper.selectRoleList().stream().map(this::convertToVO).toList();
    }

    /**
     * 获取角色详情
     * 根据ID查询角色信息及关联的权限ID列表
     *
     * @param id 角色ID
     * @return 角色详情VO
     */
    @Override
    public SysRoleDetailVO getSysRoleDetail(String id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "角色不存在");
        }
        SysRoleDetailVO vo = convertToDetailVO(role);
        // 查询角色关联的权限ID列表
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectByRoleId(id);
        vo.setPermissionIds(rolePermissions.stream().map(SysRolePermission::getPermissionId).toList());
        return vo;
    }

    /**
     * 创建角色
     * 新增角色并保存角色权限关联
     *
     * @param param 角色创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSysRole(SysRoleSaveParam param) {
        if (param.getRoleName() == null || param.getRoleName().isEmpty()) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "角色名称不能为空");
        }
        if (param.getRoleCode() == null || param.getRoleCode().isEmpty()) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "角色编码不能为空");
        }

        // 检查角色编码是否已存在
        SysRole existing = sysRoleMapper.selectByRoleCode(param.getRoleCode());
        if (existing != null) {
            throw new BusinessException(ExceptionCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleName(param.getRoleName());
        role.setRoleCode(param.getRoleCode());
        role.setDescription(param.getDescription());
        role.setIsDisabled(0);
        role.setIsDeleted(0);
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);
        log.info("创建角色成功：roleCode={}", param.getRoleCode());

        // 保存角色权限关联
        saveRolePermissions(role.getId(), param.getPermissionIds());
    }

    /**
     * 修改角色信息
     * 根据ID修改角色名称、编码、描述等信息
     *
     * @param id    角色ID
     * @param param 角色修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysRoleInfo(String id, SysRoleSaveParam param) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 检查角色编码是否被其他角色占用
        if (param.getRoleCode() != null && !param.getRoleCode().equals(role.getRoleCode())) {
            SysRole existing = sysRoleMapper.selectByRoleCode(param.getRoleCode());
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException(ExceptionCode.DATA_ALREADY_EXISTS, "角色编码已存在");
            }
        }

        if (param.getRoleName() != null && !param.getRoleName().isEmpty()) {
            role.setRoleName(param.getRoleName());
        }
        if (param.getRoleCode() != null && !param.getRoleCode().isEmpty()) {
            role.setRoleCode(param.getRoleCode());
        }
        if (param.getDescription() != null) {
            role.setDescription(param.getDescription());
        }
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.update(role);
        log.info("修改角色信息成功：id={}", id);
    }

    /**
     * 修改角色权限
     * 重新设置角色关联的权限列表（先删后插）
     *
     * @param id            角色ID
     * @param permissionIds 权限ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysRolePermissions(String id, List<String> permissionIds) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "角色不存在");
        }
        saveRolePermissions(id, permissionIds);
        log.info("修改角色权限成功：id={}", id);
    }

    /**
     * 删除角色
     * 根据ID删除角色（逻辑删除）
     *
     * @param id 角色ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSysRole(String id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "角色不存在");
        }

        role.setIsDeleted(1);
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.update(role);
        log.info("删除角色成功：id={}", id);
    }

    /**
     * 修改角色状态
     * 启用或禁用指定角色
     *
     * @param id         角色ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysRoleStatus(String id, Integer isDisabled) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "角色不存在");
        }

        role.setIsDisabled(isDisabled);
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.update(role);
        log.info("修改角色状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    /**
     * 保存角色权限关联（先删后插）
     */
    private void saveRolePermissions(String roleId, List<String> permissionIds) {
        sysRolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (String permissionId : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rp.setCreateTime(LocalDateTime.now());
                sysRolePermissionMapper.insert(rp);
            }
        }
    }

    private SysRoleVO convertToVO(SysRole role) {
        SysRoleVO vo = new SysRoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        vo.setIsDisabled(role.getIsDisabled());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }

    private SysRoleDetailVO convertToDetailVO(SysRole role) {
        SysRoleDetailVO vo = new SysRoleDetailVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        vo.setIsDisabled(role.getIsDisabled());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());
        return vo;
    }
}
