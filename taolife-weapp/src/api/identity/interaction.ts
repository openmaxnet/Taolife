import { get, post } from '@/utils/request'

export interface InteractionStatusVO {
  isLiked: boolean
  isCollected: boolean
}

export const toggleInteraction = (data: { targetType: number; targetId: string; interactionType: number }) =>
  post<InteractionStatusVO>('/api/identity/interaction/toggleInteraction', data)

export const getInteractionStatus = (params: { targetType: number; targetId: string }) =>
  get<InteractionStatusVO>('/api/identity/interaction/getInteractionStatus', params)

export const getInteractionStats = () =>
  get<{ likeCount: number; collectCount: number; followingCount: number }>('/api/identity/interaction/getInteractionStats')
