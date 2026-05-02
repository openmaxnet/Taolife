package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.MeridianPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经络方案Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface MeridianPlanMapper extends BaseMapper<MeridianPlan> {

    /**
     * 根据ID查询经络方案（带逻辑删除和禁用过滤）
     *
     * @param id 方案ID
     * @return 经络方案对象
     */
    default MeridianPlan selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(MeridianPlan::getId).eq(id)
                .and(MeridianPlan::getIsDeleted).eq(0)
                .and(MeridianPlan::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询经络方案标签
     *
     * @param id 方案ID
     * @return 标签字符串
     */
    default String selectTagsById(String id) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select("tags");
        queryWrapper.where(MeridianPlan::getId).eq(id)
         .and(MeridianPlan::getIsDeleted).eq(0)
         .and(MeridianPlan::getIsDisabled).eq(0);
        MeridianPlan plan = selectOneByQuery(queryWrapper);
        return plan != null ? plan.getTags() : null;
    }

    /**
     * 管理后台：根据ID查询（仅过滤已删除）
     */
    default MeridianPlan selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(MeridianPlan::getId).eq(id)
                .and(MeridianPlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
