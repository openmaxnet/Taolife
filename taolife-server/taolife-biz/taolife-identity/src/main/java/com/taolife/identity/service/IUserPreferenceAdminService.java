package com.taolife.identity.service;

import com.taolife.common.utils.PageResult;
import com.taolife.identity.entity.UserPreference;

/**
 * 用户偏好管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IUserPreferenceAdminService {

    /**
     * 分页查询用户偏好列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<UserPreference> getUserPreferencePage(Integer pageNo, Integer pageSize);
}
