package com.taolife.identity.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 互动切换参数
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class InteractionToggleParam {

    /** 目标类型：1-方案广场 2-文章 3-食物 4-运动 5-方案详情 */
    @NotNull(message = "目标类型不能为空")
    private Integer targetType;

    @NotNull(message = "目标ID不能为空")
    private String targetId;

    /** 互动类型：1-点赞 2-收藏 */
    @NotNull(message = "互动类型不能为空")
    private Integer interactionType;
}
