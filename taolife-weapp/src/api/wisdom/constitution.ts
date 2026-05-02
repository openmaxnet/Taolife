/**
 * 体质辨识接口
 * 封装体质辨识相关的API调用
 */

import { get, post } from '@/utils/request';
import type {
  ConstitutionType,
  ConstitutionQuestion,
  ConstitutionResult,
  ConstitutionRecord,
  AssessmentSubmitParam,
  AssessmentStatus
} from '@/types/biz/ai/constitution';

/**
 * 获取体质类型列表
 * 查询所有可用的体质类型
 *
 * @returns 体质类型列表
 */
export const getConstitutionTypes = async () => {
  return get<ConstitutionType[]>('/api/wisdom/constitution/getConstitutionTypes');
};

/**
 * 获取问卷题目列表
 * 根据指定模式获取对应的问卷题目（含选项）
 *
 * @param mode 测评模式：1-简易模式，2-精细模式
 * @returns 题目列表
 */
export const getQuestionList = async (mode: number) => {
  return get<ConstitutionQuestion[]>('/api/wisdom/constitution/getQuestionList', { mode });
};

/**
 * 提交测评答案
 * 用户完成问卷后提交答案，系统计算体质结果并保存记录
 *
 * @param mode 测评模式
 * @param param 提交参数
 * @returns 测评结果
 */
export const submitAssessment = async (mode: number, param: AssessmentSubmitParam) => {
  return post<ConstitutionResult>('/api/wisdom/constitution/submitAssessment', { mode, ...param });
};

/**
 * 获取测评结果
 * 根据记录ID查询测评结果详情
 *
 * @param recordId 记录ID
 * @returns 测评结果
 */
export const getAssessmentResult = async (recordId: string) => {
  return get<ConstitutionResult>('/api/wisdom/constitution/getAssessmentResult', { recordId });
};

/**
 * 获取最新测评结果
 * 查询用户最近一次测评的结果
 *
 * @returns 测评结果
 */
export const getLatestResult = async () => {
  return get<ConstitutionResult>('/api/wisdom/constitution/getLatestResult');
};

/**
 * 获取测评历史记录
 * 查询用户所有测评历史记录
 *
 * @returns 历史记录列表
 */
export const getHistoryList = async () => {
  return get<ConstitutionRecord[]>('/api/wisdom/constitution/getHistoryList');
};

/**
 * 检查用户是否有测评记录
 * 查询用户是否有做过的体质测评，返回状态：0-没有记录，1-有记录
 *
 * @returns 评估状态
 */
export const checkAssessmentStatus = async () => {
  return get<AssessmentStatus>('/api/wisdom/constitution/checkAssessmentStatus');
};
