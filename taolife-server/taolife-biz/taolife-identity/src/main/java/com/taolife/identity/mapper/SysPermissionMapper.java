package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统权限Mapper
 * 封装权限相关的数据库查询操作
 *
 * @author 文二
 * @date 2026-04-10
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据ID查询权限（带逻辑删除过滤）
     * 用于权限详情查询、修改、删除前的数据校验
     *
     * @param id 权限ID
     * @return 权限对象，不存在返回null
     */
    default SysPermission selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysPermission::getId).eq(id)
                .and(SysPermission::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID列表批量查询启用的权限
     * 用于根据角色ID查询关联的权限列表
     * 过滤已禁用和已删除的权限，按排序序号升序排列
     *
     * @param ids 权限ID列表
     * @return 权限列表
     */
    default List<SysPermission> selectEnabledByIds(List<String> ids) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SysPermission::getId).in(ids)
                .and(SysPermission::getIsDisabled).eq(0)
                .and(SysPermission::getIsDeleted).eq(0)
                .orderBy(SysPermission::getSortOrder, true)
        );
    }

    /**
     * 根据父ID列表批量查询启用的权限节点
     * 用于补全权限树中缺失的父级节点
     *
     * @param parentIds 父权限ID列表
     * @return 权限列表
     */
    default List<SysPermission> selectEnabledByParentIds(List<String> parentIds) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SysPermission::getId).in(parentIds)
                .and(SysPermission::getIsDisabled).eq(0)
                .and(SysPermission::getIsDeleted).eq(0)
        );
    }

    /**
     * 查询权限树（带逻辑删除和禁用过滤）
     * 用于获取菜单树或按钮权限列表
     *
     * @param type 权限类型（1：菜单，2：按钮，可为null表示全部）
     * @return 权限列表
     */
    default List<SysPermission> selectPermissionTree(Integer type) {
        // 构建查询条件
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SysPermission::getIsDeleted).eq(0)
            .and(SysPermission::getIsDisabled).eq(0);

        // 按类型筛选
        if (type != null) {
            queryWrapper.and(SysPermission::getPermissionType).eq(type);
        }

        // 按排序号升序
        queryWrapper.orderBy(SysPermission::getSortOrder, true);

        return selectListByQuery(queryWrapper);
    }

    /**
     * 分页查询权限列表（带逻辑删除过滤）
     * 支持按类型和权限名称模糊查询，按排序号升序排列
     *
     * @param pageNo          页码
     * @param pageSize        每页大小
     * @param type            权限类型（可为null）
     * @param permissionName  权限名称（模糊查询，可为null）
     * @return 分页结果
     */
    default Page<SysPermission> selectPermissionPage(Integer pageNo, Integer pageSize, Integer type, String permissionName) {
        // 校验分页参数
        if (pageNo == null || pageSize == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }
        if (pageNo < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("每页大小必须大于0");
        }

        // 构建查询条件
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SysPermission::getIsDeleted).eq(0);

        // 按类型筛选
        if (type != null) {
            queryWrapper.and(SysPermission::getPermissionType).eq(type);
        }
        // 权限名称模糊查询
        if (permissionName != null && !permissionName.isEmpty()) {
            queryWrapper.and(SysPermission::getPermissionName).like(permissionName);
        }

        // 按排序号升序
        queryWrapper.orderBy(SysPermission::getSortOrder, true);

        // 执行分页查询
        return paginate(pageNo, pageSize, queryWrapper);
    }
}