package com.taolife.identity.service;

import com.taolife.identity.vo.UserInteractionStatusVO;
import com.taolife.identity.vo.UserStatsVO;

/**
 * 用户互动服务接口
 *
 * @author 文二
 * @date 2026-04-28
 */
public interface IUserInteractionService {

    /**
     * 切换互动状态
     * 对目标进行点赞/收藏切换，重复操作将取消
     *
     * @param accountId       账号ID
     * @param targetType      目标类型
     * @param targetId        目标ID
     * @param interactionType 互动类型（1-点赞 2-收藏）
     * @return 当前互动状态
     */
    UserInteractionStatusVO toggleInteraction(String accountId, Integer targetType, String targetId, Integer interactionType);

    /**
     * 查询互动状态
     * 查询当前用户对指定目标的点赞和收藏状态
     *
     * @param accountId  账号ID
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @return 互动状态
     */
    UserInteractionStatusVO getInteractionStatus(String accountId, Integer targetType, String targetId);

    /**
     * 获取互动统计
     * 查询当前用户的点赞数、收藏数和关注数
     *
     * @param accountId 账号ID
     * @return 互动统计数据
     */
    UserStatsVO getInteractionStats(String accountId);
}
