/**
 * 经络穴位接口
 * 封装经络和穴位相关的API调用
 */

import { get } from '@/utils/request';
import type {
  MeridianQueryParam,
  MeridianPageResult,
  MeridianDetail,
  AcupointQueryParam,
  AcupointPageResult,
  AcupointDetail
} from '@/types/biz/wisdom/meridian';

/**
 * 获取经络列表
 * 根据条件分页获取经络信息列表
 *
 * @param param 查询参数
 * @returns 分页结果
 */
export const getMeridianPage = async (param: MeridianQueryParam) => {
  return get<MeridianPageResult>('/api/wisdom/meridian/getMeridianPage', param);
};

/**
 * 获取经络详情
 * 根据经络ID获取经络的详细信息
 *
 * @param id 经络ID
 * @returns 经络详情
 */
export const getMeridianDetail = async (id: string) => {
  return get<MeridianDetail>('/api/wisdom/meridian/getMeridianDetail', { id });
};

/**
 * 获取穴位列表
 * 根据条件分页获取穴位信息列表
 *
 * @param param 查询参数
 * @returns 分页结果
 */
export const getAcupointPage = async (param: AcupointQueryParam) => {
  return get<AcupointPageResult>('/api/wisdom/acupoint/getAcupointPage', param);
};

/**
 * 获取穴位详情
 * 根据穴位ID获取穴位的详细信息
 *
 * @param id 穴位ID
 * @returns 穴位详情
 */
export const getAcupointDetail = async (id: string) => {
  return get<AcupointDetail>('/api/wisdom/acupoint/getAcupointDetail', { id });
};