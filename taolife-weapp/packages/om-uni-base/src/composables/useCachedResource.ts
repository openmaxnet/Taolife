import { ref } from 'vue'

interface CachedData<T> {
  data: T
  timestamp: number
}

export function useCachedResource<T>(options: {
  storageKey: string
  cacheTtl: number
  fetchFn: () => Promise<T | null>
}) {
  const { storageKey, cacheTtl, fetchFn } = options
  const data = ref<T | null>(null)
  const loading = ref(false)

  const readCache = (): T | null => {
    try {
      const raw = uni.getStorageSync(storageKey)
      if (!raw) return null
      const cached: CachedData<T> = JSON.parse(raw)
      if (Date.now() - cached.timestamp > cacheTtl) {
        uni.removeStorageSync(storageKey)
        return null
      }
      return cached.data
    } catch {
      return null
    }
  }

  const writeCache = (value: T) => {
    const cached: CachedData<T> = { data: value, timestamp: Date.now() }
    uni.setStorageSync(storageKey, JSON.stringify(cached))
  }

  const fetch = async (forceRefresh = false): Promise<T | null> => {
    if (!forceRefresh && data.value) return data.value

    if (!forceRefresh) {
      const cached = readCache()
      if (cached) {
        data.value = cached
        return cached
      }
    }

    loading.value = true
    try {
      const result = await fetchFn()
      if (result) {
        data.value = result
        writeCache(result)
      }
      return result
    } catch {
      return null
    } finally {
      loading.value = false
    }
  }

  const clearCache = () => {
    data.value = null
    uni.removeStorageSync(storageKey)
  }

  return { data, loading, fetch, clearCache }
}
