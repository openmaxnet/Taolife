package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.PlanShareRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方案分享记录Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface PlanShareRecordMapper extends BaseMapper<PlanShareRecord> {

    /**
     * 根据ID查询方案分享记录（带逻辑删除过滤）
     *
     * @param id 分享记录ID
     * @return 方案分享记录对象
     */
    default PlanShareRecord selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PlanShareRecord::getId).eq(id)
                .and(PlanShareRecord::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 管理后台分页查询方案分享记录列表
     *
     * @param page 分页参数
     * @return 分页结果
     */
    default Page<PlanShareRecord> selectAdminPage(Page<PlanShareRecord> page) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(PlanShareRecord::getIsDeleted).eq(0);
        wrapper.orderBy(PlanShareRecord::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
