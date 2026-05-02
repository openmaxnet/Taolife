import { ref } from 'vue'
import { getAiQuotaStatus } from '@/api/fee/points'
import type { QuotaStatusVO } from '@/types/biz/fee/points'

const quotaStatus = ref<QuotaStatusVO>({
  used: 0,
  total: 5,
  remaining: 5,
  hasRemaining: true,
})
const loaded = ref(false)

// Composable 入口使用 function 声明，符合 Vue 社区命名约定
export function useQuota() {
  const loadQuota = async () => {
    try {
      const res = await getAiQuotaStatus()
      if (res) {
        quotaStatus.value = res
      }
      loaded.value = true
    } catch {
      // 使用默认值
    }
  }

  const decrementLocal = () => {
    if (quotaStatus.value.total !== -1) {
      quotaStatus.value.used++
      quotaStatus.value.remaining = Math.max(0, quotaStatus.value.total - quotaStatus.value.used)
      quotaStatus.value.hasRemaining = quotaStatus.value.remaining > 0
    }
  }

  const incrementLocal = (count: number) => {
    if (quotaStatus.value.total !== -1) {
      quotaStatus.value.used = Math.max(0, quotaStatus.value.used - count)
      quotaStatus.value.remaining = quotaStatus.value.total - quotaStatus.value.used
      quotaStatus.value.hasRemaining = true
    }
  }

  return {
    quotaStatus,
    loaded,
    loadQuota,
    decrementLocal,
    incrementLocal,
  }
}
