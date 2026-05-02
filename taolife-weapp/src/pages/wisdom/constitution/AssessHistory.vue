<template>
  <view class="history-page">

    <!-- 顶部导航栏 -->
    <TLTopBar title="测评历史" />

    <!-- 加载失败组件 -->
    <TLReload v-if="loadFailed" title="加载失败" description="无法获取测评历史，请稍后重试" @reload="loadHistory" />

    <!-- 加载中 -->
    <view v-else-if="isLoading" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-else class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <view v-if="list.length > 0" class="history-list">
        <view v-for="item in list" :key="item.recordId" class="history-item" @click="goToDetail(item.recordId)">
          <view class="history-info">
            <text class="history-type">{{ item.constitutionName }}</text>
            <text class="history-date">{{ item.createTime }}</text>
          </view>
          <view class="history-score">
            <text class="score-value">{{ item.score }}</text>
            <text class="score-label">分</text>
          </view>
        </view>
      </view>

      <!-- 空状态使用组件 -->
      <TLEmpty v-else title="暂无测评记录" description="完成测评后可查看历史记录" button-text="开始测评" @click="goToQuestionnaire" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { enAuth } from '@/utils/authManager'
import type { ConstitutionRecord } from '@/types/biz/ai/constitution'
import { getHistoryList } from '@/api/wisdom/constitution'


// 页面布局
const { contentPaddingTop } = usePageLayout()

// 加载状态
const isLoading = ref(true)
// 加载失败状态
const loadFailed = ref(false)
// 历史记录列表
const list = ref<ConstitutionRecord[]>([])

// 页面加载时检查并加载数据
onMounted(async () => {
  await enAuth()
  loadHistory()
})

// 加载历史记录
const loadHistory = async () => {
  isLoading.value = true
  loadFailed.value = false

  try {
    const data = await getHistoryList()
    list.value = data || []
  } catch (e) {
    console.error('加载历史记录失败', e)
    loadFailed.value = true
  } finally {
    isLoading.value = false
  }
}

// 跳转测评详情
const goToDetail = (recordId: string) => {
  uni.navigateTo({
    url: `/pages/wisdom/constitution/AssessQuestionnaire?recordId=${recordId}`
  })
}

// 跳转测评问卷
const goToQuestionnaire = () => {
  uni.navigateTo({
    url: '/pages/wisdom/constitution/AssessQuestionnaire'
  })
}
</script>

<style lang="scss">
.history-page {
  height: 100vh;
  box-sizing: border-box;
}

.content-area {
  padding: $tf-space-6;
}

.history-list {
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
  overflow: hidden;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $tf-space-5;
  border-bottom: $tf-border-light;
}

.history-item:last-child {
  border-bottom: none;
}

.history-info {
  flex: 1;
}

.history-type {
  display: block;
  font-size: $tf-text-lg;
  color: $tf-gray-900;
  font-weight: 500;
  margin-bottom: $tf-space-2;
}

.history-date {
  display: block;
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

.history-score {
  display: flex;
  align-items: baseline;
}

.score-value {
  font-size: $tf-text-3xl;
  color: $tf-brand;
  font-weight: 600;
}

.score-label {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-left: $tf-space-1;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400rpx;
}

</style>
