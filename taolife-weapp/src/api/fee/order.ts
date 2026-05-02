import { get, post } from '@/utils/request'
import type { CreateOrderVO, PayOrderVO } from '@/types/biz/fee/member'
import type { PageResult } from '@/types/common/request'

export const createOrder = (data: { planCode: string; wxOpenid: string }) =>
  post<CreateOrderVO>('/api/fee/order/createOrder', data)

export const getOrderStatus = (orderNo: string) =>
  get<PayOrderVO>('/api/fee/order/getOrderStatus', { orderNo })

export const getOrderById = (id: string) =>
  get<PayOrderVO>('/api/fee/order/getOrderById', { id })

export const getOrderPage = (params: { pageNo?: number; pageSize?: number }) =>
  get<PageResult<PayOrderVO>>('/api/fee/order/getOrderPage', params)

export const cancelOrder = (orderNo: string) =>
  post<void>('/api/fee/order/cancelOrder', { orderNo })
