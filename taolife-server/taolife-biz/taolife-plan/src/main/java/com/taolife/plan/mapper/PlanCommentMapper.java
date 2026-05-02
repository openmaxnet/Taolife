package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方案评论Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface PlanCommentMapper extends BaseMapper<PlanComment> {

    /**
     * 根据ID查询方案评论（带逻辑删除过滤）
     *
     * @param id 评论ID
     * @return 方案评论对象
     */
    default PlanComment selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanComment::getId).eq(id)
                .and(PlanComment::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 管理后台分页查询方案评论列表
     *
     * @param page         分页参数
     * @param planSquareId 方案广场ID（可选，不传则查全部）
     * @return 分页结果
     */
    default Page<PlanComment> selectAdminPage(Page<PlanComment> page, String planSquareId) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(PlanComment::getIsDeleted).eq(0);
        if (planSquareId != null && !planSquareId.isEmpty()) {
            wrapper.and(PlanComment::getPlanSquareId).eq(planSquareId);
        }
        wrapper.orderBy(PlanComment::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
