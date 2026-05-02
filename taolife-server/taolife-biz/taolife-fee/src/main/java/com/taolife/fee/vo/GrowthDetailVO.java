package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户成长值详情 VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class GrowthDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前成长值 */
    private Integer growthValue;

    /** 当前等级值 */
    private Integer growthLevel;

    /** 所有等级定义列表 */
    private List<GrowthLevelVO> levels;
}
