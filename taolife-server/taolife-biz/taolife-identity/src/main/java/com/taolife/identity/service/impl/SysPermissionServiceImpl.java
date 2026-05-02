package com.taolife.identity.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.identity.entity.SysPermission;
import com.taolife.identity.mapper.SysPermissionMapper;
import com.taolife.identity.param.SysPermissionPageParam;
import com.taolife.identity.param.SysPermissionSaveParam;
import com.taolife.identity.service.ISysPermissionService;
import com.taolife.identity.vo.SysPermissionDetailVO;
import com.taolife.identity.vo.SysPermissionVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统权限服务实现类
 * 实现系统权限（菜单/按钮）管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-04-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements ISysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 获取权限树
     * 查询权限列表并构建树形结构
     *
     * @param type 权限类型（1：菜单，2：按钮，可为空）
     * @return 权限树列表
     */
    @Override
    public List<SysPermissionVO> getPermissionTree(Integer type) {
        // 调用Mapper查询权限列表
        List<SysPermission> permissions = sysPermissionMapper.selectPermissionTree(type);
        // 构建树形结构
        return buildTree(permissions);
    }

    /**
     * 分页获取权限列表
     * 根据条件分页查询权限信息
     *
     * @param param 查询参数
     * @return 分页后的权限列表
     */
    @Override
    public PageResult<SysPermissionVO> getPermissionPage(SysPermissionPageParam param) {
        // 调用Mapper分页查询权限
        Page<SysPermission> page = sysPermissionMapper.selectPermissionPage(
            param.getPageNo(), param.getPageSize(), param.getType(), param.getPermissionName());

        // 转换实体为VO
        List<SysPermissionVO> list = page.getRecords().stream()
            .map(this::convertToVO)
            .toList();

        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取权限详情
     * 根据权限ID查询权限详细信息
     *
     * @param id 权限ID
     * @return 权限详细信息
     */
    @Override
    public SysPermissionDetailVO getPermissionDetail(String id) {
        // 根据ID查询权限
        SysPermission permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "权限不存在");
        }
        return convertToDetailVO(permission);
    }

    /**
     * 创建权限
     * 新增一个权限记录
     *
     * @param param 权限保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPermission(SysPermissionSaveParam param) {
        // 构建权限实体
        SysPermission permission = new SysPermission();
        permission.setParentId(param.getParentId());
        permission.setPermissionName(param.getPermissionName());
        permission.setPermissionCode(param.getPermissionCode());
        permission.setPermissionType(param.getPermissionType());
        permission.setPath(param.getPath());
        permission.setComponent(param.getComponent());
        permission.setIcon(param.getIcon());
        permission.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        permission.setIsDisabled(0);
        permission.setIsDeleted(0);
        permission.setCreateTime(LocalDateTime.now());

        // 插入数据库
        sysPermissionMapper.insert(permission);
        log.info("创建权限成功：permissionName={}", param.getPermissionName());
    }

    /**
     * 修改权限信息
     * 根据权限ID修改权限信息
     *
     * @param id   权限ID
     * @param param 权限修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyPermissionInfo(String id, SysPermissionSaveParam param) {
        // 查询权限是否存在
        SysPermission permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "权限不存在");
        }

        // 更新权限信息
        if (param.getParentId() != null) {
            permission.setParentId(param.getParentId());
        }
        if (param.getPermissionName() != null) {
            permission.setPermissionName(param.getPermissionName());
        }
        if (param.getPermissionCode() != null) {
            permission.setPermissionCode(param.getPermissionCode());
        }
        if (param.getPermissionType() != null) {
            permission.setPermissionType(param.getPermissionType());
        }
        if (param.getPath() != null) {
            permission.setPath(param.getPath());
        }
        if (param.getComponent() != null) {
            permission.setComponent(param.getComponent());
        }
        if (param.getIcon() != null) {
            permission.setIcon(param.getIcon());
        }
        if (param.getSortOrder() != null) {
            permission.setSortOrder(param.getSortOrder());
        }
        permission.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        sysPermissionMapper.update(permission);
        log.info("修改权限信息成功：id={}", id);
    }

    /**
     * 删除权限
     * 根据权限ID删除权限（逻辑删除）
     *
     * @param id 权限ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermission(String id) {
        // 查询权限是否存在
        SysPermission permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "权限不存在");
        }

        // 执行逻辑删除
        permission.setIsDeleted(1);
        permission.setUpdateTime(LocalDateTime.now());
        sysPermissionMapper.update(permission);
        log.info("删除权限成功：id={}", id);
    }

    /**
     * 批量删除权限
     * 根据权限ID列表批量删除权限
     *
     * @param ids 权限ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermissionBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 逐个删除
        for (String id : ids) {
            removePermission(id);
        }
        log.info("批量删除权限成功：ids={}", ids);
    }

    /**
     * 构建权限树
     * 将权限列表转换为树形结构
     *
     * @param permissions 权限列表
     * @return 权限树列表
     */
    private List<SysPermissionVO> buildTree(List<SysPermission> permissions) {
        // 转换为VO
        List<SysPermissionVO> allVOs = permissions.stream()
            .map(this::convertToVO)
            .toList();

        // 按父ID分组
        Map<String, List<SysPermissionVO>> childrenMap = allVOs.stream()
            .filter(vo -> vo.getParentId() != null && !vo.getParentId().isEmpty())
            .collect(Collectors.groupingBy(SysPermissionVO::getParentId));

        // 设置每个节点的子节点
        allVOs.forEach(vo -> vo.setChildren(childrenMap.get(vo.getId())));

        // 过滤出顶级节点并按排序号排序
        return allVOs.stream()
            .filter(vo -> vo.getParentId() == null || vo.getParentId().isEmpty())
            .sorted(Comparator.comparingInt(vo -> vo.getSortOrder() != null ? vo.getSortOrder() : 0))
            .toList();
    }

    /**
     * 权限实体转换为VO
     *
     * @param permission 权限实体
     * @return 权限VO
     */
    private SysPermissionVO convertToVO(SysPermission permission) {
        SysPermissionVO vo = new SysPermissionVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setPermissionName(permission.getPermissionName());
        vo.setPermissionCode(permission.getPermissionCode());
        vo.setPermissionType(permission.getPermissionType());
        vo.setPath(permission.getPath());
        vo.setComponent(permission.getComponent());
        vo.setIcon(permission.getIcon());
        vo.setSortOrder(permission.getSortOrder());
        vo.setIsDisabled(permission.getIsDisabled());
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }

    /**
     * 权限实体转换为详情VO
     *
     * @param permission 权限实体
     * @return 权限详情VO
     */
    private SysPermissionDetailVO convertToDetailVO(SysPermission permission) {
        SysPermissionDetailVO vo = new SysPermissionDetailVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setPermissionName(permission.getPermissionName());
        vo.setPermissionCode(permission.getPermissionCode());
        vo.setPermissionType(permission.getPermissionType());
        vo.setPath(permission.getPath());
        vo.setComponent(permission.getComponent());
        vo.setIcon(permission.getIcon());
        vo.setSortOrder(permission.getSortOrder());
        vo.setIsDisabled(permission.getIsDisabled());
        vo.setCreateTime(permission.getCreateTime());
        vo.setUpdateTime(permission.getUpdateTime());
        return vo;
    }
}