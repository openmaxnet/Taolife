package com.taolife.identity.vo;

import lombok.Data;

/**
 * 用户互动状态VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class UserInteractionStatusVO {

    /** 是否已点赞 */
    private Boolean isLiked;

    /** 是否已收藏 */
    private Boolean isCollected;
}
