import { ref, type Ref } from 'vue'

interface PageData<T> {
  list: T[]
  total: number
}

export function useListLoader<T>(
  fetchFn: (pageNo: number, pageSize: number) => Promise<PageData<T>>,
  pageSize = 20,
) {
  const list = ref<T[]>([]) as Ref<T[]>
  const pageNo = ref(1)
  const total = ref(0)
  const loading = ref(false)
  const noMore = ref(false)
  const loadFailed = ref(false)

  const fetchList = async (reset = false) => {
    if (loading.value) return
    loading.value = true
    loadFailed.value = false
    try {
      if (reset) {
        pageNo.value = 1
        noMore.value = false
      }
      const result = await fetchFn(pageNo.value, pageSize)
      const items = result?.list ?? []
      if (reset) {
        list.value = items
      } else {
        list.value = [...list.value, ...items]
      }
      total.value = result?.total ?? 0
      noMore.value = list.value.length >= total.value
    } catch (error) {
      console.error('获取列表失败:', error)
      loadFailed.value = true
    } finally {
      loading.value = false
    }
  }

  const handleReload = () => fetchList(true)

  const loadMore = () => {
    if (!noMore.value && !loading.value) {
      pageNo.value++
      fetchList()
    }
  }

  return { list, pageNo, total, loading, noMore, loadFailed, fetchList, handleReload, loadMore }
}
