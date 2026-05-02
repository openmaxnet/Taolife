package com.taolife.wisdom.controller;

import com.taolife.wisdom.param.ModifyQuestionStatusAdminParam;
import com.taolife.wisdom.param.OptionSaveAdminParam;
import com.taolife.wisdom.param.QuestionDetailAdminParam;
import com.taolife.wisdom.param.QuestionPageAdminParam;
import com.taolife.wisdom.param.FitnessQuestionSaveAdminParam;
import com.taolife.wisdom.param.RemoveOptionAdminParam;
import com.taolife.wisdom.param.RemoveQuestionAdminParam;
import com.taolife.wisdom.service.IFitnessQuestionAdminService;
import com.taolife.wisdom.vo.FitnessQuestionAdminVO;
import com.taolife.wisdom.vo.QuestionDetailAdminVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员体质问卷管理控制器
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/constitution/question")
@RequiredArgsConstructor
public class FitnessQuestionAdminController {

    private final IFitnessQuestionAdminService adminConstitutionQuestionService;

    /**
     * 分页查询题目列表
     *
     * @param param 分页查询参数
     * @return 题目分页结果
     */
    @GetMapping("/getQuestionPage")
    public ExceptionResult<PageResult<FitnessQuestionAdminVO>> getQuestionPage(QuestionPageAdminParam param) {
        return ExceptionResult.success(adminConstitutionQuestionService.getQuestionPage(param));
    }

    /**
     * 获取题目详情（含选项）
     *
     * @param param 详情查询参数（含ID）
     * @return 题目详情VO
     */
    @GetMapping("/getQuestionDetail")
    public ExceptionResult<QuestionDetailAdminVO> getQuestionDetail(QuestionDetailAdminParam param) {
        return ExceptionResult.success(adminConstitutionQuestionService.getQuestionDetail(param));
    }

    /**
     * 创建题目
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createQuestion")
    public ExceptionResult<Void> createQuestion(FitnessQuestionSaveAdminParam param) {
        adminConstitutionQuestionService.createQuestion(param);
        return ExceptionResult.success();
    }

    /**
     * 修改题目信息
     *
     * @param param 修改参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/modifyQuestionInfo")
    public ExceptionResult<Void> modifyQuestionInfo(FitnessQuestionSaveAdminParam param) {
        adminConstitutionQuestionService.modifyQuestionInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除题目（逻辑删除）
     *
     * @param param 删除参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/removeQuestion")
    public ExceptionResult<Void> removeQuestion(RemoveQuestionAdminParam param) {
        adminConstitutionQuestionService.removeQuestion(param);
        return ExceptionResult.success();
    }

    /**
     * 修改题目状态
     *
     * @param param 状态修改参数（含ID和禁用标记）
     * @return 操作结果
     */
    @PostMapping("/modifyQuestionStatus")
    public ExceptionResult<Void> modifyQuestionStatus(ModifyQuestionStatusAdminParam param) {
        adminConstitutionQuestionService.modifyQuestionStatus(param);
        return ExceptionResult.success();
    }

    // ==================== 选项管理 ====================

    /**
     * 创建选项
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createOption")
    public ExceptionResult<Void> createOption(OptionSaveAdminParam param) {
        adminConstitutionQuestionService.createOption(param);
        return ExceptionResult.success();
    }

    /**
     * 修改选项信息
     *
     * @param param 修改参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/modifyOptionInfo")
    public ExceptionResult<Void> modifyOptionInfo(OptionSaveAdminParam param) {
        adminConstitutionQuestionService.modifyOptionInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除选项
     *
     * @param param 删除参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/removeOption")
    public ExceptionResult<Void> removeOption(RemoveOptionAdminParam param) {
        adminConstitutionQuestionService.removeOption(param);
        return ExceptionResult.success();
    }
}
