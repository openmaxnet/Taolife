package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.PointsRule;
import com.taolife.fee.param.PointsRuleSaveParam;
import com.taolife.fee.vo.PointsRuleAdminVO;

import java.util.List;

/**
 * 积分规则服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IPointsRuleService {

    /**
     * 查询所有已启用的规则
     *
     * @return 积分规则VO列表
     */
    List<PointsRuleAdminVO> getEnabledRules();

    /**
     * 按规则编码查询积分规则
     *
     * @param ruleCode 规则编码
     * @return 积分规则实体
     */
    PointsRule getRuleByCode(String ruleCode);

    /**
     * 按规则编码获取积分数
     *
     * @param ruleCode 规则编码
     * @return 积分数（未找到返回0）
     */
    int getPointsByCode(String ruleCode);

    /**
     * 按条件类型查询已启用的规则
     *
     * @param conditionType 条件类型
     * @return 积分规则列表
     */
    List<PointsRule> getRulesByConditionType(int conditionType);

    /**
     * 分页查询积分规则
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<PointsRuleAdminVO> getRulePage(Integer pageNo, Integer pageSize);

    /**
     * 根据ID查询规则（管理员用）
     *
     * @param id 规则ID
     * @return 积分规则VO
     */
    PointsRuleAdminVO getRuleForAdmin(String id);

    /**
     * 创建积分规则
     *
     * @param param 保存参数
     * @return 新规则ID
     */
    String createRule(PointsRuleSaveParam param);

    /**
     * 修改积分规则
     *
     * @param id    规则ID
     * @param param 保存参数
     */
    void modifyRule(String id, PointsRuleSaveParam param);

    /**
     * 删除积分规则
     *
     * @param id 规则ID
     */
    void removeRule(String id);

    /**
     * 修改积分规则状态
     *
     * @param id        规则ID
     * @param isEnabled 是否启用：0-否，1-是
     */
    void modifyRuleStatus(String id, Integer isEnabled);
}
