package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanSquare;
import com.taolife.plan.param.PlanSquareQueryParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方案广场Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface PlanSquareMapper extends BaseMapper<PlanSquare> {

    /**
     * 根据ID查询方案广场（带逻辑删除过滤）
     *
     * @param id 广场ID
     * @return 方案广场对象
     */
    default PlanSquare selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanSquare::getId).eq(id)
                .and(PlanSquare::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询方案广场列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<PlanSquare> selectSquarePage(PlanSquareQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        // 默认条件：未删除、状态正常
        wrapper.where(PlanSquare::getIsDeleted).eq(0);
        wrapper.and(PlanSquare::getStatus).eq(1);

        // 关键词搜索
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(PlanSquare::getPlanSummary).like(param.getKeyword());
        }

        // 方案类型筛选
        if (param.getPlanType() != null) {
            wrapper.and(PlanSquare::getPlanType).eq(param.getPlanType());
        }

        // 体质筛选
        if (param.getConstitutionName() != null && !param.getConstitutionName().isEmpty()) {
            wrapper.and(PlanSquare::getConstitutionName).eq(param.getConstitutionName());
        }

        // 排序：1=创建时间倒序(默认)，2=点赞数倒序，3=浏览数倒序
        Integer sortBy = param.getSortBy();
        if (sortBy == null || sortBy == 1) {
            wrapper.orderBy(PlanSquare::getCreateTime, false);
        } else if (sortBy == 2) {
            wrapper.orderBy(PlanSquare::getLikeCount, false);
        } else if (sortBy == 3) {
            wrapper.orderBy(PlanSquare::getViewCount, false);
        } else {
            wrapper.orderBy(PlanSquare::getCreateTime, false);
        }

        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 管理后台分页查询方案广场列表
     *
     * @param page   分页参数
     * @param status 状态（可选）
     * @return 分页结果
     */
    default Page<PlanSquare> selectAdminPage(Page<PlanSquare> page, Integer status) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(PlanSquare::getIsDeleted).eq(0);
        if (status != null) {
            wrapper.and(PlanSquare::getStatus).eq(status);
        }
        wrapper.orderBy(PlanSquare::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
