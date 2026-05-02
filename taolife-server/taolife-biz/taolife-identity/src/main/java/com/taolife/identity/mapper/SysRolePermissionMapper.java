package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色权限关联Mapper
 * 封装角色权限关联的数据库查询操作
 *
 * @author 文二
 * @date 2026-04-10
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 根据角色ID查询该角色关联的所有权限记录
     *
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    default List<SysRolePermission> selectByRoleId(String roleId) {
        // 构建查询条件：根据角色ID查询关联记录
        return selectListByQuery(
            QueryWrapper.create()
                .where(SysRolePermission::getRoleId).eq(roleId)
        );
    }

    /**
     * 根据角色ID删除所有权限关联记录
     *
     * @param roleId 角色ID
     */
    default void deleteByRoleId(String roleId) {
        deleteByQuery(
            QueryWrapper.create()
                .where(SysRolePermission::getRoleId).eq(roleId)
        );
    }
}
