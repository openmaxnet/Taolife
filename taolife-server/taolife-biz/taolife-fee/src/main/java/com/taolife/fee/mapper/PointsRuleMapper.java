package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.PointsRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 积分规则 Mapper 接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface PointsRuleMapper extends BaseMapper<PointsRule> {

    /**
     * 按规则编码查询
     *
     * @param ruleCode 规则编码
     * @return 积分规则
     */
    default PointsRule selectByRuleCode(String ruleCode) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PointsRule::getRuleCode).eq(ruleCode)
                .and(PointsRule::getIsEnabled).eq(1)
                .limit(1)
        );
    }

    /**
     * 查询所有已启用的规则（按排序号升序）
     *
     * @return 已启用的积分规则列表
     */
    default List<PointsRule> selectEnabledRules() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(PointsRule::getIsEnabled).eq(1)
                .orderBy(PointsRule::getSortOrder, true)
        );
    }

    /**
     * 按条件类型查询已启用的规则
     *
     * @param conditionType 条件类型
     * @return 积分规则列表
     */
    default List<PointsRule> selectByConditionType(Integer conditionType) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(PointsRule::getConditionType).eq(conditionType)
                .and(PointsRule::getIsEnabled).eq(1)
                .orderBy(PointsRule::getConditionValue, true)
        );
    }

    /**
     * 根据 ID 查询（管理员用，不过滤状态）
     *
     * @param id 规则ID
     * @return 积分规则
     */
    default PointsRule selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(PointsRule::getId).eq(id)
                .limit(1)
        );
    }
}
