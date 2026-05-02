import { ref } from 'vue'

export interface InteractionApi {
  toggle: (params: { targetType: number; targetId: string; interactionType: number }) => Promise<{ isLiked: boolean; isCollected: boolean }>
  getStatus: (params: { targetType: number; targetId: string }) => Promise<{ isLiked: boolean; isCollected: boolean }>
}

export function useInteraction(
  api: InteractionApi,
  targetType: number,
  getTargetId: () => string,
) {
  const isLiked = ref(false)
  const isCollected = ref(false)

  const loadStatus = async () => {
    const targetId = getTargetId()
    if (!targetId) return
    try {
      const status = await api.getStatus({ targetType, targetId })
      isLiked.value = status.isLiked
      isCollected.value = status.isCollected
    } catch {
      // 静默失败
    }
  }

  const toggleLike = async () => {
    const targetId = getTargetId()
    if (!targetId) return
    try {
      const status = await api.toggle({ targetType, targetId, interactionType: 1 })
      isLiked.value = status.isLiked
    } catch (e: any) {
      uni.showToast({ title: e.message || '操作失败', icon: 'none' })
    }
  }

  const toggleCollect = async () => {
    const targetId = getTargetId()
    if (!targetId) return
    try {
      const status = await api.toggle({ targetType, targetId, interactionType: 2 })
      isCollected.value = status.isCollected
      uni.showToast({ title: isCollected.value ? '收藏成功' : '取消收藏', icon: 'none' })
    } catch (e: any) {
      uni.showToast({ title: e.message || '操作失败', icon: 'none' })
    }
  }

  return { isLiked, isCollected, loadStatus, toggleLike, toggleCollect }
}
