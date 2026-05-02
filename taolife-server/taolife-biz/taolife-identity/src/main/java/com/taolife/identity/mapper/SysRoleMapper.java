package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统角色Mapper
 * 封装角色相关的数据库查询操作
 *
 * @author 文二
 * @date 2026-04-10
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据ID查询角色（带逻辑删除过滤）
     * 用于角色详情查询、修改前的数据校验
     *
     * @param id 角色ID
     * @return 角色对象，不存在返回null
     */
    default SysRole selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysRole::getId).eq(id)
                .and(SysRole::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 查询角色列表（带逻辑删除过滤）
     * 查询所有未删除的角色，按创建时间倒序排列
     *
     * @return 角色列表
     */
    default List<SysRole> selectRoleList() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SysRole::getIsDeleted).eq(0)
                .orderBy(SysRole::getCreateTime, false)
        );
    }

    /**
     * 根据角色编码查询角色（带逻辑删除过滤）
     *
     * @param roleCode 角色编码
     * @return 角色对象，不存在返回null
     */
    default SysRole selectByRoleCode(String roleCode) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysRole::getRoleCode).eq(roleCode)
                .and(SysRole::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}