/**
 * SSE 流式请求工具
 * 基于 uni.request enableChunked 实现 SSE 逐 chunk 接收
 */

import { getToken } from '@/utils/storage'
import { BASE_URL } from '@/utils/config'

/** SSE 流式回调 */
export interface SSECallbacks {
  onMessage: (content: string) => void
  onError: (error: string) => void
  onCompleted: () => void
  onThinking?: (content: string) => void
}

/**
 * 创建 SSE 流式请求
 *
 * @param path 接口路径（会拼接 BASE_URL）
 * @param data 请求参数
 * @param callbacks 流式回调
 * @returns 取消请求的函数
 */
export const createSseRequest = (
  path: string,
  data: any,
  callbacks: SSECallbacks,
): (() => void) => {
  const { onMessage, onError, onCompleted, onThinking } = callbacks
  const token = getToken()
  let buffer = ''

  const task = uni.request({
    url: `${BASE_URL}${path}`,
    method: 'POST',
    header: {
      'Content-Type': 'application/json',
      Authorization: token ? `Bearer ${token}` : '',
    },
    data,
    enableChunked: true,
    responseType: 'text',
    success: () => {
      if (buffer.trim()) {
        // 非 SSE 响应：后端直接返回 JSON 错误（如配额超限）
        const trimmed = buffer.trim()
        if (trimmed.startsWith('{') && !trimmed.includes('event:')) {
          try {
            const errorData = JSON.parse(trimmed)
            if (errorData.code) {
              onError(errorData.code)
              return
            }
          } catch { /* 不是合法 JSON，走 SSE 解析 */ }
        }
        buffer = parseSSEChunk(buffer, callbacks)
      }
    },
    fail: (err) => {
      console.error('[SSE] 请求失败:', err)
      onError('网络错误，请检查网络连接')
    },
  })

  ;(task as any).onChunkReceived((response: { data: ArrayBuffer }) => {
    try {
      const uint8 = response.data instanceof ArrayBuffer
        ? new Uint8Array(response.data)
        : new Uint8Array(response.data as any)
      const text = new TextDecoder('utf-8').decode(uint8)
      buffer += text
      buffer = parseSSEChunk(buffer, callbacks)
    } catch (e) {
      console.error('[SSE] chunk 解码失败:', e)
    }
  })

  return () => { task.abort() }
}

/**
 * 增量解析 SSE chunk
 * SSE 事件格式: event:xxx\ndata:yyy\n\n
 */
const parseSSEChunk = (buffer: string, callbacks: SSECallbacks): string => {
  const { onMessage, onError, onCompleted, onThinking } = callbacks
  const parts = buffer.split('\n\n')
  const remaining = parts.pop() || ''

  for (const part of parts) {
    if (!part.trim()) continue

    let eventName = 'message'
    let dataLine = ''

    for (const line of part.split('\n')) {
      if (line.startsWith('event:')) {
        eventName = line.substring(6).trim()
      } else if (line.startsWith('data:')) {
        dataLine = line.substring(5).trim()
      }
    }

    if (!dataLine) continue

    try {
      const parsed = JSON.parse(dataLine)
      if (eventName === 'done') {
        onCompleted()
      } else if (eventName === 'error') {
        onError(parsed.error || '未知错误')
      } else if (eventName === 'thinking') {
        if (parsed.content && onThinking) onThinking(parsed.content)
      } else {
        if (parsed.content) onMessage(parsed.content)
      }
    } catch {
      return part + '\n\n' + remaining
    }
  }

  return remaining
}
