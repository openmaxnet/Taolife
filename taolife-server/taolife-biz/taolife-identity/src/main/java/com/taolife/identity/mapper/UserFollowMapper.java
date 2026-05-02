package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注Mapper
 *
 * @author 文二
 * @date 2026-04-28
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    /**
     * 统计关注数
     * 查询指定账号的关注数量
     *
     * @param accountId 账号ID
     * @return 关注数
     */
    default long countFollowing(String accountId) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(UserFollow::getFollowerId).eq(accountId)
                .and(UserFollow::getIsDeleted).eq(0)
        );
    }
}
