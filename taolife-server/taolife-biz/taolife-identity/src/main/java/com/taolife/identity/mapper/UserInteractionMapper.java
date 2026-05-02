package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.UserInteraction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户互动记录Mapper
 *
 * @author 文二
 * @date 2026-04-28
 */
@Mapper
public interface UserInteractionMapper extends BaseMapper<UserInteraction> {

    /**
     * 根据用户和目标查询互动记录
     *
     * @param accountId       账号ID
     * @param targetType      目标类型
     * @param targetId        目标ID
     * @param interactionType 互动类型（1：点赞，2：收藏）
     * @return 互动记录
     */
    default UserInteraction selectByUserTarget(String accountId, Integer targetType, String targetId, Integer interactionType) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(UserInteraction::getAccountId).eq(accountId)
                .and(UserInteraction::getTargetType).eq(targetType)
                .and(UserInteraction::getTargetId).eq(targetId)
                .and(UserInteraction::getInteractionType).eq(interactionType)
                .limit(1)
        );
    }

    /**
     * 统计指定账号的互动数量
     *
     * @param accountId       账号ID
     * @param interactionType 互动类型（1：点赞，2：收藏）
     * @return 互动数量
     */
    default long countByAccountAndType(String accountId, Integer interactionType) {
        return selectCountByQuery(
            QueryWrapper.create()
                .where(UserInteraction::getAccountId).eq(accountId)
                .and(UserInteraction::getInteractionType).eq(interactionType)
                .and(UserInteraction::getIsDeleted).eq(0)
        );
    }
}
