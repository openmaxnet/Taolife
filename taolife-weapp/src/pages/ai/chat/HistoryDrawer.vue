<template>
  <t-drawer
    :visible="visible"
    placement="left"
    :show-overlay="true"
    :close-on-overlay-click="true"
    @close="handleClose"
    class="history-drawer"
  >
    <view class="drawer-content">
      <!-- 标题区域 -->
      <view
        class="custom-header"
        :style="{
          marginTop: capsulePosition.top + 'px',
          height: (capsulePosition.height + 4) + 'px',
        }"
      >
        <view class="header-left">
          <view class="header-icon i-solar:chat-round-dots-outline" />
          <text class="header-title">最近对话</text>
        </view>
        <text v-if="sessionList.length > 0" class="header-count">{{ sessionList.length }} 条记录</text>
      </view>

      <scroll-view class="history-list" scroll-y>
        <!-- 新建对话按钮 -->
        <view class="new-session-wrapper">
          <view class="new-session-btn" @click="handleCreate">
            <view class="new-session-icon i-solar:add-circle-outline" />
            <text class="new-session-text">新建对话</text>
          </view>
        </view>
        <!-- 加载状态 -->
        <view v-if="loading" class="loading-container">
          <t-loading theme="circular" size="40px" />
        </view>

        <!-- 空状态 -->
        <TLEmpty
          v-else-if="!loading && sessionList.length === 0"
          title="暂无历史记录"
          description="开始新对话后将在这里记录"
          icon-size="100rpx"
          padding="120rpx 64rpx"
          min-height="300rpx"
        />

        <!-- 会话列表 -->
        <view v-else class="session-list">
          <t-swipe-cell
            v-for="session in sessionList"
            :key="session.sessionId"
            custom-style="border-radius: 24rpx"
          >
            <view class="session-card" @click="handleSessionClick(session)">
              <view class="session-card-icon">
                <view class="i-solar:chat-round-dots-outline" />
              </view>
              <view class="session-card-body">
                <text class="session-card-title">{{ session.sessionTitle || '未命名对话' }}</text>
                <text class="session-card-time">{{ session.createTime }}</text>
              </view>
              <view class="session-card-arrow i-solar:alt-arrow-right-outline" />
            </view>
            <template #right>
              <view class="btn delete-btn" @click.stop="handleDelete(session)">
                <view class="delete-icon i-solar:trash-bin-trash-outline" />
                <text class="delete-text">删除</text>
              </view>
            </template>
          </t-swipe-cell>
        </view>

        <!-- 加载更多 -->
        <view v-if="hasMore && !loading" class="load-more" @click="loadMore">
          <view class="load-more-icon i-solar:refresh-outline" />
          <text class="load-more-text">加载更多</text>
        </view>
      </scroll-view>
    </view>
  </t-drawer>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { getSessionPage, removeSession } from '@/api/ai/chat';
import type { Session } from '@/types/biz/ai/chat';
import { getCapsulePosition } from '@/utils/capsule';
import TDrawer from '@tdesign/uniapp/drawer/drawer.vue';
import TLoading from '@tdesign/uniapp/loading/loading.vue';
import TSwipeCell from '@tdesign/uniapp/swipe-cell/swipe-cell.vue';
import TLEmpty from '@/components/TLEmpty/index.vue';

/**
 * 组件属性
 */
const props = defineProps<{
  /** 是否显示抽屉 */
  visible: boolean;
}>();

/**
 * 组件事件
 */
const emit = defineEmits<{
  /** 关闭抽屉 */
  (e: 'update:visible', value: boolean): void;
  /** 选择会话 */
  (e: 'select', session: Session): void;
  /** 新建会话 */
  (e: 'create'): void;
}>();


/**
 * 会话列表
 */
const sessionList = ref<Session[]>([]);

/**
 * 加载状态
 */
const loading = ref(false);

/**
 * 当前页码
 */
const currentPage = ref(1);

/**
 * 每页数量
 */
const pageSize = ref(20);

/**
 * 是否还有更多数据
 */
const hasMore = ref(true);

/**
 * 胶囊位置信息
 */
const capsulePosition = computed(() => getCapsulePosition());

/**
 * 监听visible变化，显示时加载数据
 */
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadSessions();
  }
});

/**
 * 加载会话列表
 */
const loadSessions = async (isLoadMore = false) => {
  if (loading.value) {
    return;
  }

  loading.value = true;

  try {
    const result = await getSessionPage({
      pageNo: currentPage.value,
      pageSize: pageSize.value,
    });

    if (result.list && result.list.length > 0) {
      if (isLoadMore) {
        sessionList.value = [...sessionList.value, ...result.list];
      } else {
        sessionList.value = result.list;
      }

      // 判断是否还有更多数据
      hasMore.value = result.list.length >= pageSize.value;
    } else {
      hasMore.value = false;
    }
  } catch (error) {
    console.error('加载会话列表失败', error);
    uni.showToast({
      title: '加载失败',
      icon: 'none',
    });
  } finally {
    loading.value = false;
  }
};

/**
 * 加载更多
 */
const loadMore = () => {
  currentPage.value++;
  loadSessions(true);
};

/**
 * 点击会话
 */
const handleSessionClick = (session: Session) => {
  emit('select', session);
  handleClose();
};

/**
 * 删除会话
 */
const handleDelete = async (session: Session) => {
  try {
    await removeSession(session.sessionId);
    
    // 从列表中移除
    sessionList.value = sessionList.value.filter(item => item.sessionId !== session.sessionId);
    
    uni.showToast({
      title: '删除成功',
      icon: 'none',
    });
  } catch (error) {
    console.error('删除会话失败', error);
    uni.showToast({
      title: '删除失败',
      icon: 'none',
    });
  }
};

/**
 * 关闭抽屉
 */
const handleClose = () => {
  emit('update:visible', false);
};

/**
 * 新建对话
 */
const handleCreate = () => {
  emit('create');
  handleClose();
};
</script>

<style lang="scss">

.drawer-content {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: $tf-page-bg-color;
}

.custom-header {
  flex-shrink: 0;
  padding-left: $tf-space-8;
  padding-right: $tf-space-8;
  padding-bottom: $tf-space-6;
  border-bottom: $tf-border-light;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: $tf-space-3;
  height: 100%;
}

.header-icon {
  width: 36rpx;
  height: 36rpx;
  color: $tf-brand;
}

.header-title {
  font-size: $tf-text-2xl;
  font-weight: bold;
  color: $tf-gray-900;
}

.header-count {
  font-size: $tf-text-sm;
  color: $tf-brand;
  background: $tf-brand-bg;
  padding: $tf-space-1 $tf-space-4;
  border-radius: $tf-radius-pill;
}

.history-list {
  flex: 1;
  height: 0;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: $tf-space-16 0;
}

.session-list {
  padding: $tf-space-6 $tf-space-8;
  display: flex;
  flex-direction: column;
  gap: $tf-space-6;
}

.session-card {
  display: flex;
  align-items: center;
  gap: $tf-space-5;
  background: $tf-surface;
  padding: $tf-space-6 $tf-space-8;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.session-card-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: $tf-radius-lg;
  background: $tf-brand-bg;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  > view {
    width: 32rpx;
    height: 32rpx;
    color: $tf-brand;
  }
}

.session-card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: $tf-space-2;
}

.session-card-title {
  font-size: $tf-text-lg;
  color: $tf-gray-800;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-card-time {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
}

.session-card-arrow {
  width: 28rpx;
  height: 28rpx;
  color: $tf-gray-400;
  flex-shrink: 0;
}

.new-session-wrapper {
  padding: $tf-space-6 $tf-space-8;
}

.new-session-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $tf-space-3;
  height: 88rpx;
  background: $tf-primary-color;
  border-radius: $tf-radius-4xl;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.96);
  }
}

.new-session-icon {
  width: 36rpx;
  height: 36rpx;
  color: $tf-surface;
}

.new-session-text {
  font-size: $tf-text-lg;
  color: $tf-surface;
  font-weight: 500;
}

.btn {
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: $tf-space-1;
  width: 120rpx;
  height: 100%;
  color: white;
}

.delete-btn {
  background-color: #e34d59;
}

.delete-icon {
  width: 28rpx;
  height: 28rpx;
}

.delete-text {
  font-size: $tf-text-sm;
}

.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $tf-space-2;
  margin: $tf-space-8 auto;
  padding: $tf-space-3 $tf-space-8;
  border: $tf-border-light;
  border-radius: $tf-radius-3xl;
  background: $tf-surface;
  width: fit-content;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.96);
  }
}

.load-more-icon {
  width: 28rpx;
  height: 28rpx;
  color: $tf-gray-500;
}

.load-more-text {
  font-size: $tf-text-md;
  color: $tf-gray-500;
}
</style>
