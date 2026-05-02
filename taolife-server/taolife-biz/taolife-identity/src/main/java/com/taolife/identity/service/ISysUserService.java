package com.taolife.identity.service;

import com.taolife.identity.param.SysUserPageParam;
import com.taolife.identity.param.SysUserSaveParam;
import com.taolife.identity.vo.SysUserDetailVO;
import com.taolife.identity.vo.SysUserVO;
import com.taolife.common.utils.PageResult;

/**
 * 系统用户服务接口
 *
 * @author 文二
 * @date 2026-04-11
 */
public interface ISysUserService {

    /**
     * 分页获取用户列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<SysUserVO> getSysUserPage(SysUserPageParam param);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详细信息
     */
    SysUserDetailVO getSysUserDetail(String id);

    /**
     * 创建用户
     *
     * @param param 用户保存参数
     */
    void createSysUser(SysUserSaveParam param);

    /**
     * 修改用户信息
     *
     * @param id 用户ID
     * @param param 用户保存参数
     */
    void modifySysUserInfo(String id, SysUserSaveParam param);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void removeSysUser(String id);

    /**
     * 修改用户状态
     *
     * @param id 用户ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifySysUserStatus(String id, Integer isDisabled);
}