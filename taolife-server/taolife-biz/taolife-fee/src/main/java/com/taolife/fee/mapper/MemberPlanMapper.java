package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.MemberPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员套餐Mapper接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface MemberPlanMapper extends BaseMapper<MemberPlan> {

    /**
     * 查询已启用的会员套餐列表
     *
     * @return 会员套餐列表
     */
    default List<MemberPlan> selectEnabledList() {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(MemberPlan::getIsEnabled).eq(1)
                .orderBy(MemberPlan::getSortOrder, true);
        return selectListByQuery(wrapper);
    }

    /**
     * 根据套餐代码查询（仅已启用）
     *
     * @param planCode 套餐代码
     * @return 会员套餐
     */
    default MemberPlan selectByPlanCode(String planCode) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(MemberPlan::getPlanCode).eq(planCode)
                .and(MemberPlan::getIsEnabled).eq(1)
                .limit(1);
        return selectOneByQuery(wrapper);
    }

    /**
     * 根据套餐代码查询（不限启用状态，用于唯一性校验）
     *
     * @param planCode 套餐代码
     * @return 会员套餐
     */
    default MemberPlan selectByPlanCodeIgnoreEnabled(String planCode) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(MemberPlan::getPlanCode).eq(planCode)
                .limit(1);
        return selectOneByQuery(wrapper);
    }

    /**
     * 分页查询会员套餐
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param planCode 套餐代码（精确查询，可为null）
     * @param isEnabled 启用状态（可为null）
     * @return 分页结果
     */
    default Page<MemberPlan> selectMemberPlanPage(Integer pageNo, Integer pageSize, String planCode, Integer isEnabled) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(MemberPlan::getSortOrder, true)
                .orderBy(MemberPlan::getCreateTime, false);
        if (planCode != null) {
            wrapper.and(MemberPlan::getPlanCode).eq(planCode);
        }
        if (isEnabled != null) {
            wrapper.and(MemberPlan::getIsEnabled).eq(isEnabled);
        }
        return paginate(pageNo, pageSize, wrapper);
    }
}
