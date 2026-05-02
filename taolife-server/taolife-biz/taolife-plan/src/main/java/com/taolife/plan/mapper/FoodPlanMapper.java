package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.FoodPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食材方案Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface FoodPlanMapper extends BaseMapper<FoodPlan> {

    /**
     * 根据ID查询食材方案（带逻辑删除和禁用过滤）
     *
     * @param id 方案ID
     * @return 食材方案对象
     */
    default FoodPlan selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(FoodPlan::getId).eq(id)
                .and(FoodPlan::getIsDeleted).eq(0)
                .and(FoodPlan::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询食材方案标签
     *
     * @param id 方案ID
     * @return 标签字符串
     */
    default String selectTagsById(String id) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select("tags");
        queryWrapper.where(FoodPlan::getId).eq(id)
         .and(FoodPlan::getIsDeleted).eq(0)
         .and(FoodPlan::getIsDisabled).eq(0);
        FoodPlan plan = selectOneByQuery(queryWrapper);
        return plan != null ? plan.getTags() : null;
    }

    /**
     * 管理后台：根据ID查询食材方案（仅过滤已删除）
     *
     * @param id 方案ID
     * @return 食材方案对象
     */
    default FoodPlan selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(FoodPlan::getId).eq(id)
                .and(FoodPlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
