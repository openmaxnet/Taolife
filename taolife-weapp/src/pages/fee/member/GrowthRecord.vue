<template>
  <view class="page-container">
    <TLTopBar title="成长记录" />

    <view class="content" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <view class="record-list" v-if="records.length > 0">
        <view v-for="item in records" :key="item.id" class="record-item">
          <view class="record-left">
            <text class="record-source">{{ growthSourceMap[item.growthSource] ?? '其他' }}</text>
            <text class="record-remark">{{ item.remark }}</text>
          </view>
          <view class="record-right">
            <text class="record-change" :class="item.growthChange > 0 ? 'positive' : 'negative'">
              {{ item.growthChange > 0 ? '+' : '' }}{{ item.growthChange }}
            </text>
            <text class="record-time">{{ item.createTime?.substring(5, 16) }}</text>
          </view>
        </view>
      </view>
      <TLEmpty v-else-if="loaded" title="暂无成长记录" />
      <view v-if="hasMore && records.length > 0" class="load-more" @tap="loadMore">
        <text>加载更多</text>
      </view>
    </view>

    <view class="bottom-space" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLEmpty from '@/components/TLEmpty/index.vue'
import { getGrowthRecords } from '@/api/fee/member'
import type { MemberGrowthRecordVO } from '@/types/biz/fee/member'
import { usePageLayout } from '@/composables/usePageLayout'

const { contentPaddingTop } = usePageLayout()

const records = ref<MemberGrowthRecordVO[]>([])
const pageNo = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const loaded = ref(false)

const growthSourceMap: Record<number, string> = {
  1: '每日登录',
  2: '签到',
  3: '完成任务',
  4: '开通续费',
  5: '手动调整',
}

const loadRecords = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getGrowthRecords(pageNo.value, 20)
    const list = res?.list ?? []
    if (pageNo.value === 1) {
      records.value = list
    } else {
      records.value.push(...list)
    }
    hasMore.value = records.value.length < (res?.total ?? 0)
  } catch (error) {
    console.error('获取成长记录失败:', error)
  } finally {
    loading.value = false
    loaded.value = true
  }
}

const loadMore = () => {
  pageNo.value++
  loadRecords()
}

onMounted(() => {
  loadRecords()
})
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: $tf-page-bg-color;
}

.content {
  padding: $tf-space-6;
}

.record-list {
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  overflow: hidden;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $tf-space-6 $tf-space-8;
  border-bottom: $tf-border-light;

  &:last-child {
    border-bottom: none;
  }
}

.record-left {
  display: flex;
  flex-direction: column;
  gap: $tf-space-1;
}

.record-source {
  font-size: $tf-text-lg;
  color: $tf-gray-800;
}

.record-remark {
  font-size: $tf-text-base;
  color: $tf-gray-600;
}

.record-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: $tf-space-1;
}

.record-change {
  font-size: $tf-text-lg;
  font-weight: 600;

  &.positive {
    color: #67C23A;
  }

  &.negative {
    color: #F56C6C;
  }
}

.record-time {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

.load-more {
  text-align: center;
  padding: $tf-space-6;
  font-size: $tf-text-md;
  color: $tf-gray-600;
  background: $tf-surface;
  border-radius: $tf-radius-lg;
  margin-top: $tf-space-4;
}

.bottom-space {
  height: $tf-space-12;
}
</style>
