package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanDetailRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方案详情关联Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface PlanDetailRelationMapper extends BaseMapper<PlanDetailRelation> {

    /**
     * 根据ID查询方案详情关联（带逻辑删除过滤）
     *
     * @param id 关联ID
     * @return 方案详情关联对象
     */
    default PlanDetailRelation selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanDetailRelation::getId).eq(id)
                .and(PlanDetailRelation::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
