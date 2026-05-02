package com.taolife.aichat.controller;

import com.taolife.aichat.param.ImportAdminParam;
import com.taolife.aichat.param.ModifyQuestionRuleStatusAdminParam;
import com.taolife.aichat.param.QuestionRuleDetailAdminParam;
import com.taolife.aichat.param.QuestionRulePageAdminParam;
import com.taolife.aichat.param.QuestionRuleSaveAdminParam;
import com.taolife.aichat.param.RemoveQuestionRuleAdminParam;
import com.taolife.aichat.service.IQuestionRuleAdminService;
import com.taolife.aichat.vo.QuestionRuleAdminVO;
import com.taolife.aichat.vo.ImportResultVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员分类规则管理控制器
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/rule")
@RequiredArgsConstructor
public class QuestionRuleAdminController {

    private final IQuestionRuleAdminService adminQuestionRuleService;

    /**
     * 分页查询分类规则列表
     *
     * @param param 分页查询参数
     * @return 分类规则分页结果
     */
    @GetMapping("/getQuestionRulePage")
    public ExceptionResult<PageResult<QuestionRuleAdminVO>> getQuestionRulePage(QuestionRulePageAdminParam param) {
        return ExceptionResult.success(adminQuestionRuleService.getQuestionRulePage(param));
    }

    /**
     * 获取分类规则详情
     *
     * @param param 详情查询参数
     * @return 分类规则详情
     */
    @GetMapping("/getQuestionRuleDetail")
    public ExceptionResult<QuestionRuleAdminVO> getQuestionRuleDetail(QuestionRuleDetailAdminParam param) {
        return ExceptionResult.success(adminQuestionRuleService.getQuestionRuleDetail(param));
    }

    /**
     * 创建分类规则
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createQuestionRule")
    public ExceptionResult<Void> createQuestionRule(QuestionRuleSaveAdminParam param) {
        adminQuestionRuleService.createQuestionRule(param);
        return ExceptionResult.success();
    }

    /**
     * 修改分类规则信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyQuestionRuleInfo")
    public ExceptionResult<Void> modifyQuestionRuleInfo(QuestionRuleSaveAdminParam param) {
        adminQuestionRuleService.modifyQuestionRuleInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除分类规则
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeQuestionRule")
    public ExceptionResult<Void> removeQuestionRule(RemoveQuestionRuleAdminParam param) {
        adminQuestionRuleService.removeQuestionRule(param);
        return ExceptionResult.success();
    }

    /**
     * 修改分类规则状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyQuestionRuleStatus")
    public ExceptionResult<Void> modifyQuestionRuleStatus(ModifyQuestionRuleStatusAdminParam param) {
        adminQuestionRuleService.modifyQuestionRuleStatus(param);
        return ExceptionResult.success();
    }

    /**
     * 批量导入分类规则（Excel）
     *
     * @param param 导入参数（含Excel文件）
     * @return 导入结果
     */
    @PostMapping("/importQuestionRules")
    public ExceptionResult<ImportResultVO> importQuestionRules(ImportAdminParam param) {
        return ExceptionResult.success(adminQuestionRuleService.importQuestionRules(param));
    }
}