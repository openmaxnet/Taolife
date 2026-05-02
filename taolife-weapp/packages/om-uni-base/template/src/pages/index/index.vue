<template>
  <view class="page">
    <text class="title">{{ title }}</text>
    <view class="info">
      <text>认证状态: {{ authed ? '已登录' : '未登录' }}</text>
    </view>
    <view class="actions">
      <button @tap="handleLogin">登录</button>
      <button @tap="handleFetch">获取数据</button>
    </view>
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-if="data" class="result">
      <text>{{ JSON.stringify(data) }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { isAuth, enAuth, launchSilentLogin, checkLocalAuthState } from '@/utils/authManager'
import { useListLoader } from '@om/uni-base'
import { getArticleList } from '@/api/example'

const title = ref('OM Uni-Base Template')
const authed = ref(false)
const loading = ref(false)
const data = ref<any>(null)

// 使用 useListLoader 示例（需要时取消注释）
// const { list, loading, refresh } = useListLoader({
//   fetchFn: (page) => getArticleList(page, 10),
// })

const handleLogin = async () => {
  try {
    await enAuth()
    authed.value = true
  } catch (e) {
    uni.showToast({ title: '登录失败', icon: 'none' })
  }
}

const handleFetch = async () => {
  loading.value = true
  try {
    // 示例请求 - 替换为实际 API
    data.value = { message: '替换为你的 API 调用' }
  } finally {
    loading.value = false
  }
}

// 启动时静默登录
launchSilentLogin()
checkLocalAuthState().then((v) => { authed.value = v })
</script>

<style scoped lang="scss">
.page {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 40rpx;
}

.info {
  margin-bottom: 40rpx;
}

.actions {
  display: flex;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.loading, .result {
  margin-top: 20rpx;
  padding: 20rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  width: 100%;
}
</style>
