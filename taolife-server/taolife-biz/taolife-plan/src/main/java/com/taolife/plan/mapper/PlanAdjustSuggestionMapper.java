package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.taolife.plan.entity.PlanAdjustSuggestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方案调整推荐建议缓存Mapper
 *
 * @author 文二
 * @date 2026-04-16
 */
@Mapper
public interface PlanAdjustSuggestionMapper extends BaseMapper<PlanAdjustSuggestion> {

    /**
     * 根据子方案ID查询
     */
    default PlanAdjustSuggestion selectBySubPlanId(String subPlanId) {
        return selectOneByQuery(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(PlanAdjustSuggestion::getSubPlanId).eq(subPlanId)
        );
    }
}
