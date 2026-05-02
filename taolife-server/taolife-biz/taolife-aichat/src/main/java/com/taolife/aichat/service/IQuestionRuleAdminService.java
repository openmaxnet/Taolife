package com.taolife.aichat.service;

import com.taolife.aichat.param.*;
import com.taolife.aichat.param.ImportAdminParam;
import com.taolife.aichat.vo.QuestionRuleAdminVO;
import com.taolife.aichat.vo.ImportResultVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员分类规则服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IQuestionRuleAdminService {

    /**
     * 分页查询分类规则列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<QuestionRuleAdminVO> getQuestionRulePage(QuestionRulePageAdminParam param);

    /**
     * 获取分类规则详情
     *
     * @param param 详情查询参数
     * @return 分类规则详情
     */
    QuestionRuleAdminVO getQuestionRuleDetail(QuestionRuleDetailAdminParam param);

    /**
     * 创建分类规则
     *
     * @param param 创建参数
     */
    void createQuestionRule(QuestionRuleSaveAdminParam param);

    /**
     * 修改分类规则
     *
     * @param param 修改参数
     */
    void modifyQuestionRuleInfo(QuestionRuleSaveAdminParam param);

    /**
     * 删除分类规则
     *
     * @param param 删除参数
     */
    void removeQuestionRule(RemoveQuestionRuleAdminParam param);

    /**
     * 修改分类规则状态
     *
     * @param param 状态修改参数
     */
    void modifyQuestionRuleStatus(ModifyQuestionRuleStatusAdminParam param);

    /**
     * 批量导入分类规则
     *
     * @param param 导入参数
     * @return 导入结果
     */
    ImportResultVO importQuestionRules(ImportAdminParam param);
}