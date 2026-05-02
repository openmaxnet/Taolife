/**
 * AI聊天接口
 */

import { get, post } from '@/utils/request'
import { createSseRequest } from '@/utils/sse'
import type {
  Session,
  Message,
  CreateSessionParam,
  SendMessageParam,
  RegenerateMessageParam,
  SessionPageParam,
  PageResult,
} from '@/types/biz/ai/chat'
import type { SSECallbacks } from '@/utils/sse'

/** 创建会话 */
export const createSession = async (param: CreateSessionParam) => {
  return post<Session>('/api/ai/chat/createSession', param)
}

/** 获取会话列表 */
export const getSessionPage = async (param?: SessionPageParam) => {
  return get<PageResult<Session>>('/api/ai/chat/getSessionPage', param)
}

/** 获取会话详情（消息列表） */
export const getSessionDetail = async (param: SessionPageParam) => {
  return get<PageResult<Message>>('/api/ai/chat/getSessionDetail', param)
}

/** 删除会话 */
export const removeSession = async (sessionId: string) => {
  return post<void>(`/api/ai/chat/removeSession?sessionId=${sessionId}`, {})
}

/** 删除消息 */
export const deleteMessage = async (sessionId: string, messageId: string) => {
  return post<void>('/api/ai/chat/deleteMessage', { sessionId, messageId })
}

/** 流式发送消息 */
export const sendMessage = (
  param: SendMessageParam,
  onMessage: SSECallbacks['onMessage'],
  onError: SSECallbacks['onError'],
  onCompleted: SSECallbacks['onCompleted'],
  onThinking?: SSECallbacks['onThinking'],
) => {
  return createSseRequest('/api/ai/chat/sendMessage', param, { onMessage, onError, onCompleted, onThinking })
}

/** 重新生成AI回答 */
export const regenerateMessage = (
  param: RegenerateMessageParam,
  onMessage: SSECallbacks['onMessage'],
  onError: SSECallbacks['onError'],
  onCompleted: SSECallbacks['onCompleted'],
  onThinking?: SSECallbacks['onThinking'],
) => {
  return createSseRequest('/api/ai/chat/regenerateMessage', param, { onMessage, onError, onCompleted, onThinking })
}
