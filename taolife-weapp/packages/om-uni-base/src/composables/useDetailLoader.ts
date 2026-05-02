import { ref } from 'vue'

export function useDetailLoader<T>(fetchFn: () => Promise<T>, options?: { ensureAuth?: () => Promise<any> }) {
  const data = ref<T | null>(null)
  const isLoading = ref(true)
  const loadFailed = ref(false)

  const load = async () => {
    isLoading.value = true
    loadFailed.value = false
    try {
      if (options?.ensureAuth) await options.ensureAuth()
      data.value = await fetchFn()
    } catch (e) {
      console.error('加载失败:', e)
      loadFailed.value = true
    } finally {
      isLoading.value = false
    }
  }

  return { data, isLoading, loadFailed, load }
}
