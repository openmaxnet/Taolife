/**
 * 账号接口
 * 封装账号相关的 API 调用
 */

import { get, post } from '@/utils/request';
import type { UserInfoVO, UserInfoParam } from '@/types';

export const getUserInfo = () =>
  get<UserInfoVO>('/api/identity/account/getUserInfo');

export const modifyUserInfo = (param: UserInfoParam) =>
  post('/api/identity/account/modifyUserInfo', param);

export interface CancelCheckVO {
  cancellationStatus: number
  cancellationTime: string | null
  cancellationSchedule: string | null
  canCancel: boolean
  blockReason: string | null
}

export const getCancelStatus = () =>
  get<CancelCheckVO>('/api/identity/account/getCancelStatus');

export const requestCancel = (reason?: string) =>
  post('/api/identity/account/requestCancel', reason ? { reason } : {});

export const revokeCancel = () =>
  post('/api/identity/account/revokeCancel');
