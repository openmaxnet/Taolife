package com.taolife.identity.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.identity.entity.SysRole;
import com.taolife.identity.entity.SysUser;
import com.taolife.identity.mapper.SysRoleMapper;
import com.taolife.identity.mapper.SysUserMapper;
import com.taolife.identity.param.SysUserPageParam;
import com.taolife.identity.param.SysUserSaveParam;
import com.taolife.identity.service.ISysUserService;
import com.taolife.identity.vo.SysUserDetailVO;
import com.taolife.identity.vo.SysUserVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统用户服务实现类
 * 实现系统用户管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-04-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页获取用户列表
     * 根据条件分页查询用户信息，并填充角色名称
     *
     * @param param 查询参数（页码、每页大小、真实姓名）
     * @return 分页后的用户列表
     */
    @Override
    public PageResult<SysUserVO> getSysUserPage(SysUserPageParam param) {
        // 调用Mapper分页查询用户
        Page<SysUser> page = sysUserMapper.selectUserPage(param.getPageNo(), param.getPageSize(), param.getRealName());

        // 转换实体为VO，并填充角色名称
        List<SysUserVO> list = new ArrayList<>();
        for (SysUser user : page.getRecords()) {
            SysUserVO vo = convertToVO(user);
            // 根据角色ID查询角色名称
            if (user.getRoleId() != null) {
                SysRole role = sysRoleMapper.selectById(user.getRoleId());
                if (role != null) {
                    vo.setRoleName(role.getRoleName());
                }
            }
            list.add(vo);
        }

        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取用户详情
     * 根据用户ID查询用户详细信息
     *
     * @param id 用户ID
     * @return 用户详细信息
     */
    @Override
    public SysUserDetailVO getSysUserDetail(String id) {
        // 根据ID查询用户
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 转换为详情VO
        SysUserDetailVO vo = convertToDetailVO(user);

        // 填充角色名称
        if (user.getRoleId() != null) {
            SysRole role = sysRoleMapper.selectById(user.getRoleId());
            if (role != null) {
                vo.setRoleName(role.getRoleName());
            }
        }

        return vo;
    }

    /**
     * 创建用户
     * 新增一个用户记录
     *
     * @param param 用户创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSysUser(SysUserSaveParam param) {
        // 检查用户名是否已存在
        SysUser existingUser = sysUserMapper.selectByUsername(param.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ExceptionCode.DATA_ALREADY_EXISTS, "用户名已存在");
        }

        // 构建用户实体
        SysUser user = new SysUser();
        user.setUsername(param.getUsername());
        // 密码加密存储
        if (param.getPassword() != null && !param.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(param.getPassword()));
        }
        user.setRealName(param.getRealName());
        user.setPhone(param.getPhone());
        user.setEmail(param.getEmail());
        user.setAvatarUrl(param.getAvatarUrl());
        user.setRoleId(param.getRoleId());
        user.setIsDisabled(0);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());

        // 插入数据库
        sysUserMapper.insert(user);
        log.info("创建用户成功：username={}", param.getUsername());
    }

    /**
     * 修改用户信息
     * 根据用户ID修改用户信息
     *
     * @param id   用户ID
     * @param param 用户修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysUserInfo(String id, SysUserSaveParam param) {
        // 查询用户是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 检查用户名是否被其他用户占用
        if (param.getUsername() != null && !param.getUsername().equals(user.getUsername())) {
            SysUser existingUser = sysUserMapper.selectByUsername(param.getUsername());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new BusinessException(ExceptionCode.DATA_ALREADY_EXISTS, "用户名已存在");
            }
        }

        // 更新用户信息
        if (param.getUsername() != null) {
            user.setUsername(param.getUsername());
        }
        if (param.getPassword() != null && !param.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(param.getPassword()));
        }
        if (param.getRealName() != null) {
            user.setRealName(param.getRealName());
        }
        if (param.getPhone() != null) {
            user.setPhone(param.getPhone());
        }
        if (param.getEmail() != null) {
            user.setEmail(param.getEmail());
        }
        if (param.getAvatarUrl() != null) {
            user.setAvatarUrl(param.getAvatarUrl());
        }
        if (param.getRoleId() != null) {
            user.setRoleId(param.getRoleId());
        }
        user.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        sysUserMapper.update(user);
        log.info("修改用户信息成功：id={}", id);
    }

    /**
     * 删除用户
     * 根据用户ID删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSysUser(String id) {
        // 查询用户是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 执行逻辑删除
        user.setIsDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.update(user);
        log.info("删除用户成功：id={}", id);
    }

    /**
     * 修改用户状态
     * 启用或禁用用户账号
     *
     * @param id         用户ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysUserStatus(String id, Integer isDisabled) {
        // 查询用户是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 更新禁用状态
        user.setIsDisabled(isDisabled);
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.update(user);
        log.info("修改用户状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    /**
     * 用户实体转换为VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private SysUserVO convertToVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRoleId(user.getRoleId());
        vo.setIsDisabled(user.getIsDisabled());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    /**
     * 用户实体转换为详情VO
     *
     * @param user 用户实体
     * @return 用户详情VO
     */
    private SysUserDetailVO convertToDetailVO(SysUser user) {
        SysUserDetailVO vo = new SysUserDetailVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRoleId(user.getRoleId());
        vo.setIsDisabled(user.getIsDisabled());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}