package com.taolife.wisdom.service;

import com.taolife.wisdom.param.*;
import com.taolife.wisdom.vo.FitnessQuestionAdminVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员体质问卷服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IFitnessQuestionAdminService {

    /**
     * 分页查询题目列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<FitnessQuestionAdminVO> getQuestionPage(QuestionPageAdminParam param);

    /**
     * 获取题目详情（含选项）
     *
     * @param param 详情查询参数
     * @return 题目详情
     */
    com.taolife.wisdom.vo.QuestionDetailAdminVO getQuestionDetail(QuestionDetailAdminParam param);

    /**
     * 创建题目
     *
     * @param param 创建参数
     */
    void createQuestion(FitnessQuestionSaveAdminParam param);

    /**
     * 修改题目
     *
     * @param param 修改参数
     */
    void modifyQuestionInfo(FitnessQuestionSaveAdminParam param);

    /**
     * 删除题目
     *
     * @param param 删除参数
     */
    void removeQuestion(RemoveQuestionAdminParam param);

    /**
     * 修改题目状态
     *
     * @param param 状态修改参数
     */
    void modifyQuestionStatus(ModifyQuestionStatusAdminParam param);

    /**
     * 创建选项
     *
     * @param param 创建参数
     */
    void createOption(OptionSaveAdminParam param);

    /**
     * 修改选项
     *
     * @param param 修改参数
     */
    void modifyOptionInfo(OptionSaveAdminParam param);

    /**
     * 删除选项
     *
     * @param param 删除参数
     */
    void removeOption(RemoveOptionAdminParam param);
}
