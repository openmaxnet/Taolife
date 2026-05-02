package com.taolife.identity.vo;

import lombok.Data;

/**
 * 用户互动统计VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class UserStatsVO {

    /** 点赞数 */
    private Long likeCount;

    /** 收藏数 */
    private Long collectCount;

    /** 关注数 */
    private Long followingCount;
}
