<template>
  <view class="page-container">
    <TLTopBar :title="pageTitle" />

    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <TLReload v-if="loadFailed" @reload="loadDocument" />
      <TLLoading v-else-if="loading" />
      <view v-else-if="doc" class="document-body">
        <rich-text :nodes="doc.content" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { getAgreementByCode } from '@/api/identity/agreement'
import { usePageLayout } from '@/composables/usePageLayout'
import { useDetailLoader } from '@/composables/useDetailLoader'

const { contentPaddingTop } = usePageLayout()

const pageTitle = ref('详情')
let docCode = ''

const { data: doc, isLoading: loading, loadFailed, load: loadDocument } = useDetailLoader<{ title: string; content: string; version: number }>(
  () => getAgreementByCode(docCode)
)

onLoad((options) => {
  docCode = options?.code || ''
  pageTitle.value = decodeURIComponent(options?.title || '详情')
  loadDocument()
})
</script>

<style lang="scss">
// .page-container {
//   // background: $tf-surface;
//   min-height: 100vh;
// }

.document-body {
  background: $tf-surface;
  margin: $tf-space-6;
  padding: $tf-space-6;
  border-radius: $tf-radius-md;
  font-size: $tf-text-md;
  color: $tf-gray-700;
  line-height: 1.8;
}
</style>
