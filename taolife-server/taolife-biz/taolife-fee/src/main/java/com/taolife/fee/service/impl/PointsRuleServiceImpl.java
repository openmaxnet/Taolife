package com.taolife.fee.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.PointsRule;
import com.taolife.fee.mapper.PointsRuleMapper;
import com.taolife.fee.param.PointsRuleSaveParam;
import com.taolife.fee.service.IPointsRuleService;
import com.taolife.fee.vo.PointsRuleAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分规则服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsRuleServiceImpl implements IPointsRuleService {

    private final PointsRuleMapper pointsRuleMapper;

    /**
     * 查询所有已启用的积分规则
     *
     * @return 积分规则VO列表
     */
    @Override
    public List<PointsRuleAdminVO> getEnabledRules() {
        return pointsRuleMapper.selectEnabledRules().stream().map(this::toVO).toList();
    }

    /**
     * 按规则编码查询积分规则实体
     *
     * @param ruleCode 规则编码
     * @return 积分规则实体
     */
    @Override
    public PointsRule getRuleByCode(String ruleCode) {
        return pointsRuleMapper.selectByRuleCode(ruleCode);
    }

    /**
     * 按规则编码获取积分数值
     *
     * @param ruleCode 规则编码
     * @return 积分数值（未找到时返回 0）
     */
    @Override
    public int getPointsByCode(String ruleCode) {
        PointsRule rule = pointsRuleMapper.selectByRuleCode(ruleCode);
        return rule != null ? rule.getPoints() : 0;
    }

    /**
     * 按条件类型查询积分规则列表
     *
     * @param conditionType 条件类型（如连续签到天数条件）
     * @return 积分规则实体列表
     */
    @Override
    public List<PointsRule> getRulesByConditionType(int conditionType) {
        return pointsRuleMapper.selectByConditionType(conditionType);
    }

    /**
     * 分页查询积分规则
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 积分规则分页结果
     */
    @Override
    public PageResult<PointsRuleAdminVO> getRulePage(Integer pageNo, Integer pageSize) {
        QueryWrapper query = QueryWrapper.create()
            .orderBy(PointsRule::getSortOrder, true)
            .orderBy(PointsRule::getCreateTime, false);
        com.mybatisflex.core.paginate.Page<PointsRule> page = pointsRuleMapper.paginate(pageNo, pageSize, query);
        List<PointsRuleAdminVO> voList = page.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(voList, pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 按 ID 查询积分规则详情
     *
     * @param id 规则ID
     * @return 积分规则VO
     */
    @Override
    public PointsRuleAdminVO getRuleForAdmin(String id) {
        PointsRule rule = pointsRuleMapper.selectByIdForAdmin(id);
        return rule != null ? toVO(rule) : null;
    }

    /**
     * 创建积分规则
     *
     * @param param 积分规则保存参数
     * @return 新规则ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createRule(PointsRuleSaveParam param) {
        PointsRule rule = new PointsRule();
        rule.setRuleCode(param.getRuleCode());
        rule.setRuleName(param.getRuleName());
        rule.setPointsType(param.getPointsType());
        rule.setPoints(param.getPoints());
        rule.setConditionType(param.getConditionType());
        rule.setConditionValue(param.getConditionValue());
        rule.setDailyLimit(param.getDailyLimit());
        rule.setIsEnabled(param.getIsEnabled());
        rule.setSortOrder(param.getSortOrder());
        rule.setRemark(param.getRemark());
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        pointsRuleMapper.insert(rule);
        log.info("创建积分规则：ruleCode={}, ruleName={}", rule.getRuleCode(), rule.getRuleName());
        return rule.getId();
    }

    /**
     * 修改积分规则
     *
     * @param id    规则ID
     * @param param 积分规则保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyRule(String id, PointsRuleSaveParam param) {
        PointsRule existing = pointsRuleMapper.selectByIdForAdmin(id);
        if (existing == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "积分规则不存在");
        }
        existing.setRuleCode(param.getRuleCode());
        existing.setRuleName(param.getRuleName());
        existing.setPointsType(param.getPointsType());
        existing.setPoints(param.getPoints());
        existing.setConditionType(param.getConditionType());
        existing.setConditionValue(param.getConditionValue());
        existing.setDailyLimit(param.getDailyLimit());
        existing.setIsEnabled(param.getIsEnabled());
        existing.setSortOrder(param.getSortOrder());
        existing.setRemark(param.getRemark());
        existing.setUpdateTime(LocalDateTime.now());
        pointsRuleMapper.update(existing);
        log.info("修改积分规则：id={}", id);
    }

    /**
     * 删除积分规则
     *
     * @param id 规则ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRule(String id) {
        PointsRule existing = pointsRuleMapper.selectByIdForAdmin(id);
        if (existing == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "积分规则不存在");
        }
        pointsRuleMapper.deleteById(id);
        log.info("删除积分规则：id={}", id);
    }

    /**
     * 修改积分规则启用状态
     *
     * @param id        规则ID
     * @param isEnabled 是否启用：0-否，1-是
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyRuleStatus(String id, Integer isEnabled) {
        PointsRule existing = pointsRuleMapper.selectByIdForAdmin(id);
        if (existing == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "积分规则不存在");
        }
        existing.setIsEnabled(isEnabled);
        existing.setUpdateTime(LocalDateTime.now());
        pointsRuleMapper.update(existing);
        log.info("修改积分规则状态：id={}, isEnabled={}", id, isEnabled);
    }

    private PointsRuleAdminVO toVO(PointsRule rule) {
        PointsRuleAdminVO vo = new PointsRuleAdminVO();
        vo.setId(rule.getId());
        vo.setRuleCode(rule.getRuleCode());
        vo.setRuleName(rule.getRuleName());
        vo.setPointsType(rule.getPointsType());
        vo.setPoints(rule.getPoints());
        vo.setConditionType(rule.getConditionType());
        vo.setConditionValue(rule.getConditionValue());
        vo.setDailyLimit(rule.getDailyLimit());
        vo.setIsEnabled(rule.getIsEnabled());
        vo.setSortOrder(rule.getSortOrder());
        vo.setRemark(rule.getRemark());
        vo.setCreateTime(rule.getCreateTime());
        vo.setUpdateTime(rule.getUpdateTime());
        return vo;
    }
}
