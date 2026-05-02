package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户偏好学习Mapper接口
 *
 * @author 文二
 * @date 2026-04-05
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {

    /**
     * 根据ID查询用户偏好（带逻辑删除过滤）
     *
     * @param id 偏好ID
     * @return 用户偏好对象
     */
    default UserPreference selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(UserPreference::getId).eq(id)
                .and(UserPreference::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据账号ID查询用户偏好
     *
     * @param accountId 账号ID
     * @return 用户偏好对象
     */
    default UserPreference selectByAccountId(String accountId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(UserPreference::getAccountId).eq(accountId)
                .and(UserPreference::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 创建或更新用户偏好
     *
     * @param preference 用户偏好对象
     * @return 操作结果
     */
    default int saveOrUpdate(UserPreference preference) {
        UserPreference existing = selectByAccountId(preference.getAccountId());
        if (existing != null) {
            // 更新现有记录
            preference.setId(existing.getId());
            return update(preference);
        } else {
            // 创建新记录
            return insert(preference);
        }
    }

    /**
     * 管理后台分页查询用户偏好列表
     *
     * @param page 分页参数
     * @return 分页结果
     */
    default Page<UserPreference> selectAdminPage(Page<UserPreference> page) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(UserPreference::getIsDeleted).eq(0);
        wrapper.orderBy(UserPreference::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
