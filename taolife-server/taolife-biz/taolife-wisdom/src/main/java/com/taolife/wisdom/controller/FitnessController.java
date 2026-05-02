package com.taolife.wisdom.controller;

import com.taolife.wisdom.param.AssessmentSubmitParam;
import com.taolife.wisdom.service.IFitnessService;
import com.taolife.wisdom.vo.AssessmentStatusVO;
import com.taolife.wisdom.vo.FitnessQuestionVO;
import com.taolife.wisdom.vo.FitnessRecordVO;
import com.taolife.wisdom.vo.FitnessResultVO;
import com.taolife.wisdom.vo.FitnessTypeVO;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 体质辨识控制器
 * 负责处理体质测评相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-22
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/constitution")
@RequiredArgsConstructor
@Validated
public class FitnessController {

    private final IFitnessService constitutionService;

    /**
     * 获取体质类型列表
     * 获取所有可用的体质类型
     *
     * @return 体质类型列表
     */
    @GetMapping("/getConstitutionTypes")
    public ExceptionResult<List<FitnessTypeVO>> getConstitutionTypes() {
        return ExceptionResult.success(constitutionService.getConstitutionTypes());
    }

    /**
     * 获取问卷题目列表
     * 根据模式获取对应的问卷题目（含选项）
     *
     * @param mode 测评模式：1-简易模式，2-精细模式
     * @return 题目列表
     */
    @GetMapping("/getQuestionList")
    public ExceptionResult<List<FitnessQuestionVO>> getQuestionList(
            @RequestParam(value = "mode", defaultValue = "1") @Nullable Integer mode) {
        return ExceptionResult.success(constitutionService.getQuestionList(mode));
    }

    /**
     * 提交测评答案
     * 用户完成问卷后提交答案，计算体质结果
     *
     * @param param 提交参数
     * @return 测评结果
     */
    @PostMapping("/submitAssessment")
    public ExceptionResult<FitnessResultVO> submitAssessment(
            @Valid @RequestBody @NonNull AssessmentSubmitParam param) {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(constitutionService.submitAssessment(accountId, 1, param));
    }

    /**
     * 提交测评答案（指定模式）
     *
     * @param mode  测评模式
     * @param param 提交参数
     * @return 测评结果
     */
    @PostMapping("/submitAssessmentWithMode")
    public ExceptionResult<FitnessResultVO> submitAssessmentWithMode(
            @RequestParam("mode") @NonNull Integer mode,
            @Valid @RequestBody @NonNull AssessmentSubmitParam param) {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(constitutionService.submitAssessment(accountId, mode, param));
    }

    /**
     * 获取测评结果
     * 根据记录ID获取测评结果详情
     *
     * @param recordId 记录ID
     * @return 测评结果
     */
    @GetMapping("/getAssessmentResult")
    public ExceptionResult<FitnessResultVO> getAssessmentResult(
            @RequestParam("recordId") @NonNull String recordId) {
        return ExceptionResult.success(constitutionService.getAssessmentResult(recordId));
    }

    /**
     * 获取最新测评结果
     * 获取用户最近一次测评的结果
     *
     * @return 测评结果
     */
    @GetMapping("/getLatestResult")
    public ExceptionResult<FitnessResultVO> getLatestResult() {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(constitutionService.getLatestResult(accountId));
    }

    /**
     * 获取测评历史记录
     * 获取用户所有测评历史记录
     *
     * @return 历史记录列表
     */
    @GetMapping("/getHistoryList")
    public ExceptionResult<List<FitnessRecordVO>> getHistoryList() {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(constitutionService.getHistoryList(accountId));
    }

    /**
     * 检查用户是否有测评记录
     * 查询用户是否有做过的体质测评
     * 返回：0-没有测评记录，1-有测评记录
     *
     * @return 评估状态VO
     */
    @GetMapping("/checkAssessmentStatus")
    public ExceptionResult<AssessmentStatusVO> checkAssessmentStatus() {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(constitutionService.checkAssessmentStatus(accountId));
    }
}
