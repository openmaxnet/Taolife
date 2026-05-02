/**
 * 食材接口
 * 封装食材相关的API调用
 */

import { get } from '@/utils/request';
import type { FoodQueryParam, FoodPageResult, FoodDetail } from '@/types/biz/wisdom/food';

/**
 * 获取食材列表
 * 根据条件分页获取食材信息列表
 *
 * @param param 查询参数
 * @returns 分页结果
 */
export const getFoodPage = async (param: FoodQueryParam) => {
  return get<FoodPageResult>('/api/wisdom/food/getFoodPage', param);
};

/**
 * 获取食材详情
 * 根据食材ID获取食材的详细信息
 *
 * @param id 食材ID
 * @returns 食材详情
 */
export const getFoodDetail = async (id: string) => {
  return get<FoodDetail>('/api/wisdom/food/getFoodDetail', { id });
};
