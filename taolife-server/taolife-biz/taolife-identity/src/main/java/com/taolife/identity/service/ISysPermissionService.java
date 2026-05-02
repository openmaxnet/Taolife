package com.taolife.identity.service;

import com.taolife.identity.param.SysPermissionPageParam;
import com.taolife.identity.param.SysPermissionSaveParam;
import com.taolife.identity.vo.SysPermissionDetailVO;
import com.taolife.identity.vo.SysPermissionVO;
import com.taolife.common.utils.PageResult;

import java.util.List;

/**
 * 系统权限服务接口
 *
 * @author 文二
 * @date 2026-04-11
 */
public interface ISysPermissionService {

    /**
     * 获取权限树
     *
     * @param type 权限类型（1：菜单，2：按钮）
     * @return 权限树列表
     */
    List<SysPermissionVO> getPermissionTree(Integer type);

    /**
     * 分页获取权限列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<SysPermissionVO> getPermissionPage(SysPermissionPageParam param);

    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限详细信息
     */
    SysPermissionDetailVO getPermissionDetail(String id);

    /**
     * 创建权限
     *
     * @param param 权限保存参数
     */
    void createPermission(SysPermissionSaveParam param);

    /**
     * 修改权限信息
     *
     * @param id 权限ID
     * @param param 权限保存参数
     */
    void modifyPermissionInfo(String id, SysPermissionSaveParam param);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void removePermission(String id);

    /**
     * 批量删除权限
     *
     * @param ids 权限ID列表
     */
    void removePermissionBatch(List<String> ids);
}