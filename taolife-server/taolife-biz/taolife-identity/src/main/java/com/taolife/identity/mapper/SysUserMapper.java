package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Mapper
 * 封装用户相关的数据库查询操作
 *
 * @author 文二
 * @date 2026-04-10
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（带逻辑删除过滤）
     * 用于登录验证等场景
     *
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    default SysUser selectByUsername(String username) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysUser::getUsername).eq(username)
                .and(SysUser::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询用户（带逻辑删除过滤）
     * 用于详情查询、修改、删除前的数据校验
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    default SysUser selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysUser::getId).eq(id)
                .and(SysUser::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询用户列表（带逻辑删除过滤）
     * 支持按真实姓名模糊查询，按创建时间倒序排列
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param realName 真实姓名（模糊查询，可为null）
     * @return 分页结果
     */
    default Page<SysUser> selectUserPage(Integer pageNo, Integer pageSize, String realName) {
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

        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SysUser::getIsDeleted).eq(0);

        if (realName != null && !realName.isEmpty()) {
            queryWrapper.and(SysUser::getRealName).like(realName);
        }

        queryWrapper.orderBy(SysUser::getCreateTime, false);

        return paginate(pageNo, pageSize, queryWrapper);
    }
}