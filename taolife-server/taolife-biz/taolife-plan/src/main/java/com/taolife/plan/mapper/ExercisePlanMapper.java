package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.ExercisePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运动方案Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface ExercisePlanMapper extends BaseMapper<ExercisePlan> {

    /**
     * 根据ID查询运动方案（带逻辑删除和禁用过滤）
     *
     * @param id 方案ID
     * @return 运动方案对象
     */
    default ExercisePlan selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(ExercisePlan::getId).eq(id)
                .and(ExercisePlan::getIsDeleted).eq(0)
                .and(ExercisePlan::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询运动方案标签
     *
     * @param id 方案ID
     * @return 标签字符串
     */
    default String selectTagsById(String id) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select("tags");
        queryWrapper.where(ExercisePlan::getId).eq(id)
         .and(ExercisePlan::getIsDeleted).eq(0)
         .and(ExercisePlan::getIsDisabled).eq(0);
        ExercisePlan plan = selectOneByQuery(queryWrapper);
        return plan != null ? plan.getTags() : null;
    }

    /**
     * 管理后台：根据ID查询（仅过滤已删除）
     */
    default ExercisePlan selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(ExercisePlan::getId).eq(id)
                .and(ExercisePlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
