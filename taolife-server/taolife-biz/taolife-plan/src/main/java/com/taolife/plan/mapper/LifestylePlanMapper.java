package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.LifestylePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生活方案Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface LifestylePlanMapper extends BaseMapper<LifestylePlan> {

    /**
     * 根据ID查询生活方案（带逻辑删除和禁用过滤）
     *
     * @param id 方案ID
     * @return 生活方案对象
     */
    default LifestylePlan selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(LifestylePlan::getId).eq(id)
                .and(LifestylePlan::getIsDeleted).eq(0)
                .and(LifestylePlan::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询生活方案标签
     *
     * @param id 方案ID
     * @return 标签字符串
     */
    default String selectTagsById(String id) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select("tags");
        queryWrapper.where(LifestylePlan::getId).eq(id)
         .and(LifestylePlan::getIsDeleted).eq(0)
         .and(LifestylePlan::getIsDisabled).eq(0);
        LifestylePlan plan = selectOneByQuery(queryWrapper);
        return plan != null ? plan.getTags() : null;
    }

    /**
     * 管理后台：根据ID查询（仅过滤已删除）
     */
    default LifestylePlan selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(LifestylePlan::getId).eq(id)
                .and(LifestylePlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
