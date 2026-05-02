package com.taolife.wisdom.service;

import com.taolife.wisdom.param.AssessmentSubmitParam;
import com.taolife.wisdom.vo.AssessmentStatusVO;
import com.taolife.wisdom.vo.FitnessQuestionVO;
import com.taolife.wisdom.vo.FitnessRecordVO;
import com.taolife.wisdom.vo.FitnessResultVO;
import com.taolife.wisdom.vo.FitnessTypeVO;

import java.util.List;

/**
 * 体质辨识服务接口
 * 定义体质测评相关的业务操作
 *
 * @author 文二
 * @date 2026-03-22
 */
public interface IFitnessService {

    /**
     * 获取体质类型列表
     * 查询所有可用的体质类型
     *
     * @return 体质类型列表
     */
    List<FitnessTypeVO> getConstitutionTypes();

    /**
     * 获取问卷题目列表
     * 根据指定模式获取对应的问卷题目（含选项）
     *
     * @param mode 测评模式：1-简易模式，2-精细模式
     * @return 题目列表
     */
    List<FitnessQuestionVO> getQuestionList(Integer mode);

    /**
     * 提交测评答案
     * 用户完成问卷后提交答案，系统计算体质结果并保存记录
     *
     * @param accountId 账号ID
     * @param mode      测评模式
     * @param param     提交参数
     * @return 测评结果
     */
    FitnessResultVO submitAssessment(String accountId, Integer mode, AssessmentSubmitParam param);

    /**
     * 获取测评结果
     * 根据记录ID查询测评结果详情
     *
     * @param recordId 记录ID
     * @return 测评结果
     */
    FitnessResultVO getAssessmentResult(String recordId);

    /**
     * 获取最新测评结果
     * 查询用户最近一次测评的结果
     *
     * @param accountId 账号ID
     * @return 测评结果
     */
    FitnessResultVO getLatestResult(String accountId);

    /**
     * 获取测评历史记录
     * 查询用户所有测评历史记录
     *
     * @param accountId 账号ID
     * @return 历史记录列表
     */
    List<FitnessRecordVO> getHistoryList(String accountId);

    /**
     * 检查用户是否有测评记录
     * 查询用户是否有做过的体质测评
     *
     * @param accountId 账号ID
     * @return 评估状态VO
     */
    AssessmentStatusVO checkAssessmentStatus(String accountId);
}
