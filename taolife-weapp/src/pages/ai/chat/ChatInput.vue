<template>
  <view class="chat-input-wrapper">
    <view class="chat-input">
      <!-- 文本输入区域 -->
      <view class="input-container">
        <textarea
          class="input-textarea"
          :value="modelValue"
          :placeholder="placeholder"
          :disabled="disabled"
          :auto-height="true"
          :maxlength="maxLength"
          @input="handleInput"
          @focus="handleFocus"
        />
      </view>

      <!-- 操作栏 -->
      <view class="action-bar">
        <!-- 左侧操作按钮 -->
        <view class="action-left">
          <view
            v-for="action in effectiveLeftActions"
            :key="action.name"
            class="action-tag"
            :class="[
              { 'action-tag-active': activeActions.includes(action.name) }
            ]"
            @click="handleActionClick(action)"
          >
            <view :class="action.iconClass" class="action-tag-icon" />
            <text class="action-tag-text">{{ action.label }}</text>
          </view>
        </view>
  
        <!-- 右侧主操作按钮 -->
        <view class="action-right">
          <view
            v-for="action in effectiveRightActions"
            :key="action.name"
            class="action-btn"
            :class="[
              action.className,
              { 'disabled': action.disabled && action.disabled() }
            ]"
            @click="handleActionClick(action)"
          >
            <view :class="action.iconClass" class="action-icon"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- AI提示语 -->
    <view class="ai-tip">以上内容均由AI生成，仅供参考和借鉴</view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

/**
 * 聊天输入框组件
 * 包含文本输入和操作栏
 */

/**
 * 操作按钮配置接口
 */
interface ActionConfig {
  /** 按钮名称 */
  name: string;
  /** 图标类名 */
  iconClass: string;
  /** 显示标签 */
  label: string;
  /** 额外类名 */
  className?: string;
  /** 是否禁用（函数） */
  disabled?: () => boolean;
  /** 是否显示（函数） */
  show?: () => boolean;
  /** 点击事件名 */
  eventName: string;
}

/**
 * 组件属性
 */
const props = withDefaults(defineProps<{
  /** 输入框值 */
  modelValue: string;
  /** 占位符文本 */
  placeholder?: string;
  /** 是否禁用 */
  disabled?: boolean;
  /** 是否正在加载 */
  isLoading?: boolean;
  /** 最大输入长度 */
  maxLength?: number;
  /** 左侧操作按钮名称数组 */
  leftActions?: string[];
  /** 右侧操作按钮名称数组 */
  rightActions?: string[];
  /** 已激活的操作名称数组 */
  activeActions?: string[];
}>(), {
  placeholder: '请输入您的问题...',
  disabled: false,
  isLoading: false,
  maxLength: 2000,
  leftActions: () => ['add'],
  rightActions: () => [],
  activeActions: () => [],
});

/**
 * 所有可用的操作按钮配置
 */
const allActions: Record<string, ActionConfig> = {
  // 左侧按钮
  image: {
    name: 'image',
    iconClass: 'i-solar:gallery-outline',
    label: '图片',
    className: 'image-btn',
    eventName: 'image',
  },
  thinking: {
    name: 'thinking',
    iconClass: 'i-solar:paperclip-rounded-line-duotone',
    label: '深度思考',
    className: 'thinking-btn',
    eventName: 'thinking',
  },
  // 右侧按钮
  send: {
    name: 'send',
    iconClass: 'i-solar:plain-3-outline',
    label: '发送',
    className: 'send-btn',
    disabled: () => !props.modelValue.trim(),
    eventName: 'send',
  },
  stop: {
    name: 'stop',
    iconClass: 'i-solar:stop-circle-outline',
    label: '停止',
    className: 'stop-btn',
    eventName: 'stop',
  },
};

/**
 * 根据名称数组获取左侧按钮配置
 */
const effectiveLeftActions = computed<ActionConfig[]>(() => {
  return props.leftActions
    .map(name => allActions[name])
    .filter(Boolean);
});

/**
 * 根据名称数组或默认配置获取右侧按钮配置
 */
const effectiveRightActions = computed<ActionConfig[]>(() => {
  if (props.rightActions && props.rightActions.length > 0) {
    return props.rightActions
      .map(name => allActions[name])
      .filter(Boolean);
  }
  // 默认根据加载状态显示发送或停止按钮
  return props.isLoading ? [allActions.stop] : [allActions.send];
});

/**
 * 组件事件
 */
const emit = defineEmits<{
  /** 更新值 */
  (e: 'update:modelValue', value: string): void;
  /** 聚焦事件 */
  (e: 'focus'): void;
  /** 动态操作事件 */
  (e: string): void;
}>();

/**
 * 处理输入
 */
const handleInput = (e: any) => {
  emit('update:modelValue', e.detail.value);
};

/**
 * 处理聚焦
 */
const handleFocus = (e: any) => {
  emit('focus');
};

/**
 * 处理操作按钮点击
 */
const handleActionClick = (action: ActionConfig) => {
  // 检查是否禁用
  if (action.disabled && action.disabled()) {
    return;
  }
  // 发送对应的事件
  emit(action.eventName);
};
</script>

<style lang="scss">
.chat-input-wrapper {
  margin: $tf-space-6 $tf-space-6 calc(env(safe-area-inset-bottom) + 16rpx) $tf-space-6;
}

.chat-input {
  padding: $tf-space-6;
  box-shadow: $tf-shadow-card-light;
  border-radius: $tf-radius-md;
  background-color: $tf-surface;
}

.ai-tip {
  text-align: center;
  font-size: $tf-text-base;
  color: $tf-gray-500;
  margin-top: $tf-space-2;
  padding: 0 $tf-space-1;
}

.input-container {
  margin-bottom: $tf-space-6;
}

.input-textarea {
  width: 100%;
  min-height: $tf-space-10;
  max-height: 240rpx;
  font-size: $tf-text-base;
  line-height: 1.5;
  color: $tf-gray-800;
  background-color: transparent;
  border: none;
  padding-top: $tf-space-5;
  outline: none;
  resize: none;
}

.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $tf-space-6;
}

.action-left {
  display: flex;
  align-items: center;
  gap: $tf-space-3;
}

.action-tag {
  display: inline-flex;
  align-items: center;
  gap: $tf-space-2;
  padding: $tf-space-2 $tf-space-5;
  border-radius: $tf-radius-3xl;
  border: 2rpx solid $tf-gray-300;
  background: $tf-surface;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

.action-tag-active {
  background: $tf-brand-bg;
  border-color: $tf-brand;

  .action-tag-icon {
    color: $tf-brand;
  }

  .action-tag-text {
    color: $tf-brand;
  }
}

.action-tag-icon {
  width: 28rpx;
  height: 28rpx;
  color: $tf-gray-600;
}

.action-tag-text {
  font-size: $tf-text-sm;
  color: $tf-gray-600;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  font-size: $tf-text-lg;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;

  &.disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }

  &:active {
    transform: scale(0.9);
  }

  .action-icon {
    width: $tf-space-10;
    height: $tf-space-10;
    color: $tf-primary-color;
  }
}
</style>
