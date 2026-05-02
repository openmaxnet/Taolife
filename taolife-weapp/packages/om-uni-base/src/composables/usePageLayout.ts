import { ref, computed } from 'vue'
import { onPageScroll } from '@dcloudio/uni-app'
import { getCapsulePosition, getContentPaddingTop } from '../platform/capsule'

export function usePageLayout() {
  const contentPaddingTop = computed(() => getContentPaddingTop())
  const capsulePosition = ref(getCapsulePosition())
  const navBarRef = ref<any>(null)

  onPageScroll((e) => {
    navBarRef.value?.updateScrollTop(e.scrollTop)
  })

  return { contentPaddingTop, capsulePosition, navBarRef }
}
