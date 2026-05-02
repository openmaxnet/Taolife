package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.User;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper接口
 *
 * @author 文二
 * @date 2026-03-16
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据账号ID查询User记录
     *
     * @param accountId 账号ID
     * @return User对象
     */
    default User selectByAccountId(String accountId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(User::getAccountId).eq(accountId)
                .and(User::getIsDeleted).eq(0);
        return selectOneByQuery(queryWrapper);
    }
}
