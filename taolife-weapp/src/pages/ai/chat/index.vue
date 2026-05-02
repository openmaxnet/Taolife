<template>
  <view class="page-container">
    <!-- 历史记录抽屉 -->
    <HistoryDrawer
      v-model:visible="historyVisible"
      @select="handleSelectSession"
      @create="handleNewSession"
    />
    <!-- 自定义导航栏 -->
    <TLTopBar title="道养AI" :show-back="true">
      <template #left>
        <view class="left-actions">
          <view class="default-back" @click="handleBack">
            <view class="back-icon i-solar:alt-arrow-left-outline" />
          </view>
          <view class="history-btn" @click="handleHistory">
            <view class="history-icon i-solar:history-2-outline" />
          </view>
          <view class="history-btn" @click="handleNewSession">
            <view class="history-icon i-solar:add-circle-outline" />
          </view>
        </view>
      </template>
    </TLTopBar>

    <!-- 加载失败时显示 -->
    <TLReload v-if="loadFailed" @reload="handleReload" />

    <!-- 主内容区 -->
    <view v-else class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <!-- 聊天列表 -->
      <scroll-view
        class="chat-list"
        scroll-y
        :scroll-top="scrollTop"
        :scroll-with-animation="true"
        @scroll="onScroll"
      >
        <!-- 消息列表 -->
        <view class="message-list">
          <view
            v-for="(item) in chatListData"
            :key="item.chatId"
            class="message-item"
            :class="item.role === 'user' ? 'message-user' : 'message-ai'"
          >
            <view class="message-content-wrapper">
              <!-- AI 消息（单一气泡，包含思考 + 答案） -->
              <view v-if="item.role === 'assistant'" class="message-content">
                <!-- 思考区域 -->
                <view v-if="item.reasoningContent || item.isThinkingStreaming" class="thinking-inline">
                  <!-- 思考进行中 -->
                  <view v-if="item.isThinkingStreaming">
                    <view class="thinking-header">
                      <view class="thinking-header-left">
                        <view class="thinking-dots">
                          <view class="thinking-dot" />
                          <view class="thinking-dot" />
                          <view class="thinking-dot" />
                        </view>
                        <text class="thinking-header-text">思考中...</text>
                      </view>
                    </view>
                    <scroll-view
                      class="thinking-scroll thinking-scroll-streaming"
                      scroll-y
                      :scroll-top="thinkingScrollTop"
                      :scroll-with-animation="false"
                    >
                      <view class="thinking-scroll-inner">
                        <chatMarkdown :content="item.reasoningContent" class="thinking-markdown" />
                        <ChatLoading v-if="item.isThinkingStreaming" inline />
                      </view>
                    </scroll-view>
                  </view>

                  <!-- 思考完成 - 折叠态 -->
                  <view
                    v-else-if="!item.isThinkingExpanded"
                    class="thinking-header thinking-folded"
                    @click="toggleThinkingExpand(item)"
                  >
                    <view class="thinking-header-left">
                      <view class="thinking-done-icon i-solar:check-circle-outline" />
                      <text class="thinking-header-text">思考完成</text>
                    </view>
                    <view class="thinking-toggle-icon i-solar:alt-arrow-down-outline" />
                  </view>

                  <!-- 思考完成 - 展开态 -->
                  <view v-else>
                    <view class="thinking-header" @click="toggleThinkingExpand(item)">
                      <view class="thinking-header-left">
                        <view class="thinking-done-icon i-solar:check-circle-outline" />
                        <text class="thinking-header-text">思考过程</text>
                      </view>
                      <view class="thinking-toggle-icon i-solar:alt-arrow-up-outline" />
                    </view>
                    <view class="thinking-scroll-expanded">
                      <view class="thinking-scroll-inner">
                        <chatMarkdown :content="item.reasoningContent" class="thinking-markdown" />
                      </view>
                    </view>
                    <view class="thinking-collapse-bar" @click="toggleThinkingExpand(item)">
                      <view class="thinking-collapse-btn-text">
                        <view class="thinking-collapse-btn-icon i-solar:alt-arrow-up-outline" />
                        <text>收起思考过程</text>
                      </view>
                    </view>
                  </view>
                </view>

                <!-- 思考与答案之间的分割线 -->
                <view v-if="item.reasoningContent && !item.isThinkingStreaming && getMessageText(item.content)" class="thinking-divider" />

                <!-- 最终答案 -->
                <chatMarkdown :content="getMessageText(item.content)" class="message-text" />
                <!-- 流式加载动画 -->
                <ChatLoading v-if="item.status === 'streaming' && !item.isThinkingStreaming" inline />
                <!-- AI生成标记 -->
                <text
                  v-if="item.status === 'complete'"
                  class="ai-label"
                >AI生成</text>
              </view>

              <!-- 用户消息 -->
              <view v-else class="message-content">
                <text class="message-text">{{ getMessageText(item.content) }}</text>
              </view>

              <!-- 操作栏 - AI 消息完成或失败时显示，用户消息也显示 -->
              <MessageActions
                v-if="(item.role === 'assistant' && (item.status === 'complete' || item.status === 'error')) || item.role === 'user'"
                :actions="getActions(item)"
                @action="handleMessageAction($event, item)"
              />
            </view>
          </view>

        </view>

        <!-- 底部占位 -->
        <view class="bottom-placeholder"></view>
      </scroll-view>

      <!-- 上下文 + 配额提示条 -->
      <view v-if="contextHint || quotaStatus.total !== -1" class="context-hint-bar">
        <view class="context-hint-left">
          <view class="context-hint-icon i-solar:shield-check-outline" />
          <text class="context-hint-text">{{ contextHint || 'AI 智能问答' }}</text>
        </view>
        <view v-if="quotaStatus.total !== -1 && quotaStatus.hasRemaining" class="context-hint-right">
          <text class="context-hint-text">剩余 {{ quotaStatus.remaining }}/{{ quotaStatus.total }}</text>
        </view>
        <view v-else-if="quotaStatus.total !== -1 && !quotaStatus.hasRemaining" class="context-hint-right" @tap="quotaDialogVisible = true">
          <text class="context-hint-link">额度用尽</text>
        </view>
      </view>

      <!-- 输入框区域 -->
      <ChatInput
        v-model="inputMessage"
        :placeholder="isLoading ? '正在生成回答...' : '请输入您的问题...'"
        :disabled="isLoading"
        :is-loading="isLoading"
        :max-length="2000"
        :left-actions="['thinking']"
        :active-actions="thinkingEnabled ? ['thinking'] : []"
        @send="handleSend"
        @stop="handleStop"
        @focus="onFocus"
        @thinking="handleThinkingToggle"
      />

      <!-- 配额超限弹窗 -->
      <t-dialog
        v-model:visible="quotaDialogVisible"
        class="quota-dialog"
        title="今日次数已用完"
        content="您今日的AI问答次数已用完，可通过以下方式继续使用"
        :close-on-overlay-click="true"
        button-layout="horizontal"
        :actions="[{ content: '看广告 +1', theme: 'primary' }, { content: '积分商城', theme: 'primary' }]"
        @action="handleQuotaAction"
      />
    </view>

    <!-- 激励视频广告 -->
    <TLAdRewardedVideo
      ref="rewardedVideoRef"
      config-key="rewarded_video_chat"
      @reward="onAdReward"
      @error="onAdError"
      @close="onAdClose"
    />
  </view>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted, nextTick, triggerRef } from 'vue';
import TLTopBar from '@/components/TLTopBar/index.vue';
import TLReload from '@/components/TLReload/index.vue';
import MessageActions from './MessageActions.vue';
import ChatLoading from './ChatLoading.vue';
import ChatInput from './ChatInput.vue';
import HistoryDrawer from './HistoryDrawer.vue';
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue';
import TDialog from '@tdesign/uniapp/dialog/dialog.vue';
import { enAuth } from '@/utils/authManager';
import { usePageLayout } from '@/composables/usePageLayout';
import { sendMessage, regenerateMessage, deleteMessage, createSession, getSessionDetail } from '@/api/ai/chat';
import type { Session } from '@/types/biz/ai/chat';
import { useUserProfile } from '@/composables/useUserProfile';
import { getCurrentSolarTerm } from '@/api/wisdom/solarTerm';
	import type { SolarTermDetail } from '@/types/biz/wisdom/solarTerm';
import { useQuota } from '@/composables/useQuota';
import TLAdRewardedVideo from '@/components/TLAdRewardedVideo/index.vue';

const { constitution, fetchConstitution } = useUserProfile();
const solarTerm = ref<SolarTermDetail | null>(null);
	const fetchSolarTerm = async () => {
	  try {
	    solarTerm.value = await getCurrentSolarTerm();
	  } catch (e) {
	    console.error('获取当前节气失败:', e);
	  }
	};
const { quotaStatus, loadQuota, decrementLocal, incrementLocal } = useQuota();

const contextHint = computed(() => {
  const parts: string[] = [];
  if (constitution.value) {
    parts.push(`已了解你的${constitution.value.constitutionName}`);
  }
  if (solarTerm.value) {
    parts.push(`${solarTerm.value.termName}时节`);
  }
  return parts.length > 0 ? `AI ${parts.join(' · ')}` : '';
});


const { contentPaddingTop } = usePageLayout();

const loadFailed = ref(false);
const chatListData = ref<any[]>([]);
const inputMessage = ref('');
const isLoading = ref(false);
const currentSessionId = ref('');
const scrollTop = ref(0);
let currentAbort: (() => void) | null = null;
const historyVisible = ref(false);

// 思考模式开关 - 本地持久化
const thinkingEnabled = ref(uni.getStorageSync('tf_thinking_enabled') === 'true');

// 思考区域自动滚动位置
const thinkingScrollTop = ref(0);

// 滚动节流：避免每个 SSE chunk 都触发 DOM 测量
let lastScrollTime = 0;
const SCROLL_THROTTLE_MS = 200;

// 页面卸载标志：离开页面后跳过 UI 更新
let isUnmounted = false;

let uniqueId = 0;
const getUniqueKey = () => {
  uniqueId += 1;
  return `key-${uniqueId}`;
};

const handleReload = () => {
  loadFailed.value = false;
  initSession();
};

const initSession = async () => {
  try {
    const session = await createSession({ title: '新对话' });
    currentSessionId.value = session.sessionId;
    loadFailed.value = false;
  } catch (error) {
    console.error('创建会话失败', error);
    loadFailed.value = true;
    uni.showToast({ title: '创建会话失败', icon: 'none' });
  }
};

const getMessageText = (content: any[]) => {
  if (!content || content.length === 0) return '';
  const textContent = content.find((item) => item.type === 'text' || item.type === 'markdown');
  if (!textContent) return '';
  return textContent.data
    .replace(/\\n/g, '\n')
    .replace(/^(#{1,6})([^\s#])/gm, '$1 $2')
    .trim();
};

const scrollToBottom = () => {
  const now = Date.now();
  if (now - lastScrollTime < SCROLL_THROTTLE_MS) return;
  lastScrollTime = now;
  nextTick(() => {
    if (isUnmounted) return;
    const query = uni.createSelectorQuery();
    query.select('.message-list').boundingClientRect();
    query.exec((res) => {
      if (res && res[0]) {
        scrollTop.value = res[0].height;
      }
    });
  });
};

const toggleThinkingExpand = (item: any) => {
  item.isThinkingExpanded = !item.isThinkingExpanded;
  triggerRef(chatListData);
};

const handleThinkingToggle = () => {
  thinkingEnabled.value = !thinkingEnabled.value;
  uni.setStorageSync('tf_thinking_enabled', String(thinkingEnabled.value));
};

const handleSend = async () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) return;

  const userMessage: any = {
    chatId: getUniqueKey(),
    role: 'user',
    content: [{ type: 'text', data: message }],
    status: 'complete',
  };
  chatListData.value.push(userMessage);
  inputMessage.value = '';

  if (!currentSessionId.value) {
    await initSession();
  }

  isLoading.value = true;

  let aiMessage: any = null;
  let aiMessageIndex = -1;

  try {
    // 始终立即创建 AI 消息占位
    aiMessage = {
      chatId: getUniqueKey(),
      role: 'assistant',
      content: [{ type: 'text', data: '' }],
      reasoningContent: '',
      isThinkingStreaming: thinkingEnabled.value,
      isThinkingExpanded: thinkingEnabled.value,
      isThinkingComplete: false,
      status: 'streaming',
    };
    chatListData.value.push(aiMessage);
    aiMessageIndex = chatListData.value.length - 1;

    currentAbort = sendMessage(
      {
        sessionId: currentSessionId.value,
        content: message,
        stream: true,
        enableThinking: thinkingEnabled.value,
      },
      // onMessage
      (chunk: string) => {
        if (isUnmounted) return;

        if (aiMessage?.isThinkingStreaming) {
          aiMessage.isThinkingStreaming = false;
          aiMessage.isThinkingComplete = true;
          aiMessage.isThinkingExpanded = false;
        }
        chatListData.value[aiMessageIndex].content[0].data += chunk;
        triggerRef(chatListData);

        scrollToBottom();
      },
      // onError
      (error: string) => {
        if (isUnmounted) return;
        console.error('[handleSend] 流式发送消息失败:', error);
        if (error && (error.includes('F15001') || error.includes('AI_DAILY_QUOTA_EXHAUSTED'))) {
          showQuotaExceededDialog();
          isLoading.value = false;
          currentAbort = null;
        } else {
          if (aiMessage) {
            aiMessage.content[0].data = '抱歉，生成回答时出现错误，请稍后重试。';
            aiMessage.status = 'error';
            aiMessage.isThinkingStreaming = false;
          }
          isLoading.value = false;
          currentAbort = null;
          uni.showToast({ title: '发送消息失败', icon: 'none' });
        }
      },
      // onCompleted
      () => {
        if (isUnmounted) return;
        if (aiMessage) {
          aiMessage.status = 'complete';
          if (aiMessage.isThinkingStreaming) {
            aiMessage.isThinkingStreaming = false;
            aiMessage.isThinkingComplete = true;
            aiMessage.isThinkingExpanded = false;
          }
        }
        thinkingScrollTop.value = 0;
        triggerRef(chatListData);
        isLoading.value = false;
        decrementLocal();
        currentAbort = null;
      },
      // onThinking
      (thinkingChunk: string) => {
        if (isUnmounted) return;
        if (aiMessage) {
          aiMessage.reasoningContent += thinkingChunk;
          thinkingScrollTop.value += 50;
        }
        scrollToBottom();
      }
    );
  } catch (error: any) {
    console.error('发送消息失败', error);
    // 配额超限
    if (error?.data?.code === 'F15001' || error?.data?.code === 'AI_DAILY_QUOTA_EXHAUSTED') {
      showQuotaExceededDialog();
      if (aiMessage) {
        chatListData.value.pop();
      }
    } else {
      if (aiMessage) {
        aiMessage.content[0].data = '抱歉，生成回答时出现错误，请稍后重试。';
        aiMessage.status = 'error';
        aiMessage.isThinkingStreaming = false;
      }
    }
    isLoading.value = false;
    currentAbort = null;
  }
};

// 配额超限弹窗
const quotaDialogVisible = ref(false);
const showQuotaExceededDialog = () => {
  quotaDialogVisible.value = true;
};

const handleQuotaAction = (context: { index: number }) => {
  quotaDialogVisible.value = false;
  if (context.index === 0) {
    showRewardedVideo();
  } else if (context.index === 1) {
    goToPointsMall();
  }
};

const goToPointsMall = () => {
  uni.navigateTo({ url: '/pages/fee/points/index' });
};

// 激励视频广告
const rewardedVideoRef = ref<InstanceType<typeof TLAdRewardedVideo> | null>(null);
const adLoading = ref(false);

const showRewardedVideo = () => {
  if (adLoading.value) return;
  adLoading.value = true;
  rewardedVideoRef.value?.show();
};

const onAdReward = () => {
  incrementLocal(1);
  adLoading.value = false;
  uni.showToast({ title: '获得1次AI对话机会', icon: 'success' });
};

const onAdError = () => {
  adLoading.value = false;
};

const onAdClose = () => {
  adLoading.value = false;
};

const handleStop = () => {
  if (currentAbort) {
    currentAbort();
    currentAbort = null;
  }
  isLoading.value = false;
  uni.showToast({ title: '已停止生成', icon: 'none' });
};

const onFocus = () => {};

const onScroll = () => {};

const getActions = (item: any) => {
  const isLastAi = chatListData.value
    .filter((m: any) => m.role === 'assistant').pop()?.chatId === item.chatId;
  const isError = item.status === 'error';
  const isUser = item.role === 'user';
  return [
    { name: 'copy' as const, iconClass: 'i-solar:copy-outline', show: !isError },
    { name: 'replay' as const, iconClass: 'i-solar:refresh-outline', show: !isUser && isLastAi },
    { name: 'delete' as const, iconClass: 'i-solar:trash-bin-minimalistic-2-outline', show: true },
  ];
};

const handleMessageAction = (actionName: string, item: any) => {
  switch (actionName) {
    case 'copy': handleCopy(item); break;
    case 'replay': handleReplay(item); break;
    case 'delete': handleDelete(item); break;
  }
};

const handleCopy = (item: any) => {
  const text = getMessageText(item.content);
  uni.setClipboardData({
    data: text,
    success: () => uni.showToast({ title: '复制成功', icon: 'none' }),
  });
};

const handleReplay = (item: any) => {
  if (isLoading.value) return;

  const targetIndex = chatListData.value.findIndex((m: any) => m.chatId === item.chatId);
  if (targetIndex === -1) return;

  isLoading.value = true;

  let aiMessage: any = null;

  // 始终立即创建 AI 消息（原地替换）
  aiMessage = {
    chatId: getUniqueKey(),
    role: 'assistant',
    content: [{ type: 'text', data: '' }],
    reasoningContent: '',
    isThinkingStreaming: thinkingEnabled.value,
    isThinkingExpanded: thinkingEnabled.value,
    isThinkingComplete: false,
    status: 'streaming',
  };
  chatListData.value[targetIndex] = aiMessage;

  currentAbort = regenerateMessage(
    {
      sessionId: currentSessionId.value,
      messageId: item.chatId,
      enableThinking: thinkingEnabled.value,
    },
    // onMessage
    (chunk: string) => {
      if (isUnmounted) return;

      if (aiMessage?.isThinkingStreaming) {
        aiMessage.isThinkingStreaming = false;
        aiMessage.isThinkingComplete = true;
        aiMessage.isThinkingExpanded = false;
      }
      chatListData.value[targetIndex].content[0].data += chunk;
      triggerRef(chatListData);

      scrollToBottom();
    },
    // onError
    (error: string) => {
      if (isUnmounted) return;
      console.error('[handleReplay] 重新生成失败:', error);
      if (aiMessage) {
        aiMessage.content[0].data = '抱歉，重新生成回答时出现错误，请稍后重试。';
        aiMessage.status = 'error';
        aiMessage.isThinkingStreaming = false;
      }
      isLoading.value = false;
      currentAbort = null;
      uni.showToast({ title: '重新生成失败', icon: 'none' });
    },
    // onCompleted
    () => {
      if (isUnmounted) return;
      if (aiMessage) {
        aiMessage.status = 'complete';
        if (aiMessage.isThinkingStreaming) {
          aiMessage.isThinkingStreaming = false;
          aiMessage.isThinkingComplete = true;
          aiMessage.isThinkingExpanded = false;
        }
      }
      thinkingScrollTop.value = 0;
      triggerRef(chatListData);
      isLoading.value = false;
      currentAbort = null;
    },
    // onThinking
    (thinkingChunk: string) => {
      if (isUnmounted) return;
      if (aiMessage) {
        aiMessage.reasoningContent += thinkingChunk;
        thinkingScrollTop.value += 50;
      }
      scrollToBottom();
    },
  );
};

const handleDelete = (item: any) => {
  if (isLoading.value) return;

  uni.showModal({
    title: '删除消息',
    content: '确定要删除这条消息吗？',
    confirmText: '删除',
    confirmColor: '#e34d59',
    success: async (res) => {
      if (!res.confirm) return;

      const targetIndex = chatListData.value.findIndex((m: any) => m.chatId === item.chatId);
      if (targetIndex === -1) return;

      try {
        await deleteMessage(currentSessionId.value, item.chatId);
        chatListData.value.splice(targetIndex, 1);
        uni.showToast({ title: '已删除', icon: 'none' });
      } catch (error) {
        console.error('删除消息失败', error);
        uni.showToast({ title: '删除失败', icon: 'none' });
      }
    },
  });
};

const handleBack = () => {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack();
  } else {
    uni.switchTab({
      url: '/pages/index/index',
      fail: () => uni.navigateTo({ url: '/pages/index/index' }),
    });
  }
};

const handleHistory = () => {
  historyVisible.value = true;
};

const handleNewSession = () => {
  currentSessionId.value = '';
  chatListData.value = [];
  historyVisible.value = false;
};

const handleSelectSession = (session: Session) => {
  // 切换会话时中断当前流（后端会保存部分响应）
  if (currentAbort) {
    currentAbort();
    currentAbort = null;
    isLoading.value = false;
  }
  currentSessionId.value = session.sessionId;
  loadSessionMessages(session.sessionId);
  uni.showToast({ title: '已切换会话', icon: 'none' });
};

const loadSessionMessages = async (sessionId: string) => {
  try {
    chatListData.value = [];
    const result = await getSessionDetail({ sessionId, pageNo: 1, pageSize: 100 });
    if (result.list && result.list.length > 0) {
      const messages = result.list.map((msg: any) => ({
        chatId: msg.messageId,
        role: msg.role,
        content: [{ type: 'text', data: msg.content || '' }],
        reasoningContent: msg.reasoningContent || '',
        isThinkingStreaming: false,
        isThinkingExpanded: false,
        isThinkingComplete: !!(msg.reasoningContent),
        status: (msg.status && msg.status !== 1) ? 'error' : 'complete' as string,
      }));
      chatListData.value = messages;
      scrollToBottom();
    }
  } catch (error) {
    console.error('加载会话消息失败', error);
    uni.showToast({ title: '加载失败', icon: 'none' });
  }
};

onMounted(async () => {
  isUnmounted = false;
  await enAuth();

  // 加载用户上下文（体质、节气）
  fetchConstitution();
  fetchSolarTerm();
  loadQuota();

  // P3: 场景化入口 — 从其他页面跳转时携带预设问题
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1] as any;
  const presetQuestion = currentPage?.options?.presetQuestion;
  if (presetQuestion) {
    inputMessage.value = decodeURIComponent(presetQuestion);
  }
});

onUnmounted(() => {
  isUnmounted = true;
  // 中断 SSE 请求（后端已在 catch 块中保存部分响应）
  if (currentAbort) {
    currentAbort();
    currentAbort = null;
  }
  // 释放聊天数据内存
  chatListData.value = [];
});
</script>

<style lang="scss">
.page-container {
  height: 100vh;
  overflow: hidden;
  background-color: $tf-page-bg-color;
}

.content-area {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;
  overflow: hidden;
}

.chat-list {
  flex: 1;
  height: 0;
  overflow-y: auto;
}

.message-list {
  padding: $tf-space-8;
  min-height: 100%;
  box-sizing: border-box;
}

.message-item {
  display: flex;
  margin-bottom: $tf-space-12;
  align-items: flex-start;

  &.message-user {
    flex-direction: row-reverse;

    .message-content-wrapper {
      align-items: flex-end;
    }

    .message-content {
      background-color: var(--tf-brand-alpha-6);
      color: $tf-gray-800;
      border-radius: $tf-radius-lg $tf-radius-lg $tf-radius-sm $tf-radius-lg;
    }
  }

  &.message-ai {
    .message-content {
      background-color: $tf-surface;
      color: $tf-gray-800;
      border-radius: $tf-radius-lg $tf-radius-lg $tf-radius-lg $tf-radius-sm;
      // box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
    }
  }
}

.message-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 100%;
}

.message-content {
  padding: $tf-space-6 $tf-space-8;
  font-size: $tf-text-xl;
  line-height: 1.6;
  word-break: break-word;
  position: relative;

  .loading-dots {
    margin-top: $tf-space-2;
  }
}

.ai-label {
  position: absolute;
  right: $tf-space-6;
  bottom: $tf-space-3;
  font-size: $tf-text-xs;
  color: $tf-gray-400;
}

.message-text {
  display: block;
  white-space: pre-wrap;
}

.bottom-placeholder {
  height: calc(160rpx + env(safe-area-inset-bottom));
}

// ========== 思考过程样式 ==========

.thinking-inline {
  margin-bottom: $tf-space-4;
}

.thinking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $tf-space-3 $tf-space-4;
  background-color: var(--tf-brand-alpha-4);
  border-radius: $tf-radius-sm;
  min-height: 64rpx;

  &.thinking-folded {
    border-radius: $tf-radius-sm;
  }
}

.thinking-header-left {
  display: flex;
  align-items: center;
  gap: $tf-space-3;
}

.thinking-done-icon {
  width: 28rpx;
  height: 28rpx;
  color: $tf-brand;
}

// 跳动圆点动画（与 ChatLoading 统一）
.thinking-dots {
  display: flex;
  align-items: center;
  gap: 6rpx;

  .thinking-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    background-color: $tf-brand;
    animation: thinking-dot-bounce 1.4s infinite ease-in-out both;

    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}

@keyframes thinking-dot-bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.thinking-header-text {
  font-size: $tf-text-sm;
  color: $tf-brand;
  font-weight: 500;
}

.thinking-toggle-icon {
  width: 28rpx;
  height: 28rpx;
  color: $tf-brand;
}

// 思考与答案分割线
.thinking-divider {
  height: 1rpx;
  background-color: $tf-gray-200;
  margin: $tf-space-2 0 $tf-space-4;
}

// 思考中 - 限制高度
.thinking-scroll {
  max-height: 400rpx;
}

.thinking-scroll-streaming {
  padding-bottom: $tf-space-4;
}

// 思考完成展开 - 不限制高度
.thinking-scroll-expanded {
  padding: $tf-space-2 0 $tf-space-4;
}

.thinking-scroll-inner {
  padding: $tf-space-2 0 0;
}

.thinking-markdown {
  font-size: $tf-text-sm;
  line-height: 1.6;
  color: $tf-gray-600;
}

// 折叠按钮（类似历史记录抽屉的新建会话按钮）
.thinking-collapse-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: $tf-space-3 $tf-space-6 $tf-space-5;
}

.thinking-collapse-btn-text {
  display: inline-flex;
  align-items: center;
  gap: $tf-space-2;
  padding: $tf-space-2 $tf-space-6;
  font-size: $tf-text-sm;
  color: $tf-brand;
  background-color: var(--tf-brand-alpha-8);
  border-radius: $tf-radius-3xl;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

.thinking-collapse-btn-icon {
  width: 24rpx;
  height: 24rpx;
}

// ========== 导航栏样式 ==========

.context-hint-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $tf-space-3;
  padding: $tf-space-3 24rpx;
  background-color: var(--tf-brand-alpha-6);
  margin: 24rpx 24rpx 0;
  border-radius: $tf-radius-sm;
}

.context-hint-left {
  display: flex;
  align-items: center;
  gap: $tf-space-2;
  flex: 1;
  min-width: 0;
}

.context-hint-right {
  display: flex;
  align-items: center;
  gap: $tf-space-4;
  flex-shrink: 0;
}

.context-hint-icon {
  width: 28rpx;
  height: 28rpx;
  color: $tf-brand;
  flex-shrink: 0;
}

.context-hint-text {
  font-size: $tf-text-sm;
  color: $tf-brand;
  line-height: 1.4;
}

.context-hint-link {
  font-size: $tf-text-sm;
  color: $tf-brand;
  font-weight: 500;
  white-space: nowrap;
}

// 配额超限弹窗
.quota-dialog {
  :deep(.t-dialog__footer) {
    display: flex;
    gap: $tf-space-4;
    padding: $tf-space-2 $tf-space-6 $tf-space-6;
    border-top: none;
  }

  :deep(.t-button--variant-base) {
    background: $tf-gradient-brand;
    border-radius: $tf-radius-3xl;
    color: $tf-surface;
    border: none;
    font-weight: 600;
  }
}

.left-actions {
  display: flex;
  align-items: center;
}

.default-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-900;
}

.history-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: $tf-space-1;
}

.history-icon {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-900;
}
</style>
