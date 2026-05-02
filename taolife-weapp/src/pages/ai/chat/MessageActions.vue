<template>
  <view class="message-actions">
    <view
      v-for="action in visibleActions"
      :key="action.name"
      class="action-item"
      @click="handleAction(action)"
    >
      <view :class="action.iconClass" class="action-icon"></view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

/**
 * 操作按钮类型
 */
type ActionType = 'copy' | 'replay' | 'delete';

/**
 * 操作按钮配置
 */
interface ActionConfig {
  /** 操作名称 */
  name: ActionType;
  /** 图标类名 */
  iconClass: string;
  /** 是否显示 */
  show: boolean;
}

/**
 * 组件属性
 */
const props = withDefaults(
  defineProps<{
    /** 操作按钮配置 */
    actions?: ActionConfig[];
  }>(),
  {
    actions: () => [
      {
        name: 'copy',
        iconClass: 'i-solar:copy-outline',
        show: true,
      },
      {
        name: 'replay',
        iconClass: 'i-solar:refresh-outline',
        show: true,
      },
    ],
  }
);

/**
 * 组件事件
 */
const emit = defineEmits<{
  /** 操作点击事件 */
  (e: 'action', name: ActionType): void;
}>();

/**
 * 过滤出需要显示的操作按钮
 */
const visibleActions = computed(() => props.actions.filter(a => a.show));

/**
 * 处理操作点击
 */
const handleAction = (action: ActionConfig) => {
  emit('action', action.name);
};
</script>

<style lang="scss">
.message-actions {
  display: flex;
  gap: $tf-space-3;
  margin-top: $tf-space-2;
}

.action-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
  border-radius: $tf-radius-sm;
  background-color: var(--tf-brand-alpha-6);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: scale(0.9);
  }

  .action-icon {
    width: 32rpx;
    height: 32rpx;
    color: $tf-brand;
  }
}
</style>
