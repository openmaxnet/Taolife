<template>
  <view v-if="inline" class="loading-dots">
    <view class="dot"></view>
    <view class="dot"></view>
    <view class="dot"></view>
  </view>
  <view v-else class="loading-container">
    <view class="message-content-wrapper">
      <view class="message-content">
        <view class="loading-dots">
          <view class="dot"></view>
          <view class="dot"></view>
          <view class="dot"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  inline?: boolean;
}>(), {
  inline: false,
});
</script>

<style lang="scss">
.loading-container {
  display: flex;
  align-items: flex-start;
  margin-bottom: $tf-space-12;
}

.message-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 100%;
}

.message-content {
  padding: $tf-space-8;
  background-color: $tf-surface-dim;
  border-radius: $tf-radius-lg $tf-radius-lg $tf-radius-lg $tf-radius-sm;
  display: inline-block;
}

.loading-dots {
  display: inline-flex;
  align-items: center;
  gap: $tf-space-2;

  .dot {
    width: $tf-space-2;
    height: $tf-space-2;
    border-radius: 50%;
    background-color: $tf-gradient-from;
    animation: loading 1.4s infinite ease-in-out both;

    &:nth-child(1) {
      animation-delay: -0.32s;
    }

    &:nth-child(2) {
      animation-delay: -0.16s;
    }
  }
}

@keyframes loading {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}
</style>
