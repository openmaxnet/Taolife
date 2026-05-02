package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.AcupointPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 穴位方案Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface AcupointPlanMapper extends BaseMapper<AcupointPlan> {

    /**
     * 根据ID查询穴位方案（带逻辑删除和禁用过滤）
     *
     * @param id 方案ID
     * @return 穴位方案对象
     */
    default AcupointPlan selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AcupointPlan::getId).eq(id)
                .and(AcupointPlan::getIsDeleted).eq(0)
                .and(AcupointPlan::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询穴位方案标签
     *
     * @param id 方案ID
     * @return 标签字符串
     */
    default String selectTagsById(String id) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select("tags");
        queryWrapper.where(AcupointPlan::getId).eq(id)
         .and(AcupointPlan::getIsDeleted).eq(0)
         .and(AcupointPlan::getIsDisabled).eq(0);
        AcupointPlan plan = selectOneByQuery(queryWrapper);
        return plan != null ? plan.getTags() : null;
    }

    /**
     * 管理后台：根据ID查询（仅过滤已删除）
     */
    default AcupointPlan selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AcupointPlan::getId).eq(id)
                .and(AcupointPlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
