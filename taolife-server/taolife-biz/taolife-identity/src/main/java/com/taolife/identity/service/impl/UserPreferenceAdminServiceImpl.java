package com.taolife.identity.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.identity.entity.UserPreference;
import com.taolife.identity.mapper.UserPreferenceMapper;
import com.taolife.identity.service.IUserPreferenceAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户偏好管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceAdminServiceImpl implements IUserPreferenceAdminService {

    private final UserPreferenceMapper userPreferenceMapper;

    /**
     * 分页查询用户偏好
     * 分页获取所有用户的偏好设置
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public PageResult<UserPreference> getUserPreferencePage(Integer pageNo, Integer pageSize) {
        Page<UserPreference> page = userPreferenceMapper.selectAdminPage(new Page<>(pageNo, pageSize));
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }
}
