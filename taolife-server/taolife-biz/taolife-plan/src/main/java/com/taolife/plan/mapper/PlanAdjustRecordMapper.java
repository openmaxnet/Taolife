package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanAdjustRecord;
import com.taolife.plan.param.PlanAdjustRecordQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

/**
 * 方案调整记录Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface PlanAdjustRecordMapper extends BaseMapper<PlanAdjustRecord> {

    /**
     * 根据ID查询方案调整记录
     *
     * @param id 记录ID
     * @return 方案调整记录对象
     */
    default PlanAdjustRecord selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanAdjustRecord::getId).eq(id)
                .limit(1)
        );
    }

    /**
     * 根据用户方案ID分页查询调整记录
     *
     * @param userPlanId 用户方案ID
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<PlanAdjustRecord> selectByUserPlanId(String userPlanId, PlanAdjustRecordQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        wrapper.where(PlanAdjustRecord::getUserPlanId).eq(userPlanId);

        if (param.getAdjustmentType() != null) {
            wrapper.and(PlanAdjustRecord::getAdjustmentType).eq(param.getAdjustmentType());
        }

        if (param.getStartDate() != null) {
            wrapper.and(PlanAdjustRecord::getCreateTime).ge(LocalDate.parse(param.getStartDate()).atStartOfDay());
        }

        if (param.getEndDate() != null) {
            wrapper.and(PlanAdjustRecord::getCreateTime).le(LocalDate.parse(param.getEndDate()).atTime(23, 59, 59));
        }

        wrapper.orderBy(PlanAdjustRecord::getCreateTime, false);

        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 管理后台分页查询方案调整记录
     *
     * @param page       分页参数
     * @param userPlanId 用户方案ID（可选）
     * @return 分页结果
     */
    default Page<PlanAdjustRecord> selectAdminPage(Page<PlanAdjustRecord> page, String userPlanId) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (userPlanId != null && !userPlanId.isEmpty()) {
            wrapper.where(PlanAdjustRecord::getUserPlanId).eq(userPlanId);
        }
        wrapper.orderBy(PlanAdjustRecord::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
