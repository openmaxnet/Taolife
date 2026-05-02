<template>
  <t-popup
    :visible="visible"
    placement="bottom"
    :close-on-overlay-click="true"
    @close="handleClose"
    class="batch-adjust-popup"
  >
    <view class="batch-container">
      <!-- 标题栏 -->
      <view class="batch-header">
        <text class="batch-title">AI 调整方案</text>
        <view class="batch-close i-solar:close-circle-outline" @click="handleClose" />
      </view>

      <!-- 输入模式 -->
      <view v-if="!previewMode" class="batch-body">
        <textarea
          class="batch-textarea"
          v-model="inputText"
          placeholder="描述你想要的调整，AI会自动分析并调整相关方案..."
          :maxlength="500"
          :auto-height="false"
        />
        <view class="batch-submit-row">
          <text class="char-count">{{ inputText.length }}/500</text>
          <view
            class="batch-submit-btn"
            :class="{ 'batch-submit-btn-disabled': !inputText.trim() || submitting }"
            @click="handleSubmit"
          >
            <text class="batch-submit-btn-text">{{ submitting ? '分析中...' : '开始调整' }}</text>
          </view>
        </view>
      </view>

      <!-- 预览模式 -->
      <view v-else class="batch-preview">
        <text class="preview-hint">AI建议调整以下方案（点击查看详情）：</text>
        <scroll-view class="preview-list" scroll-y>
          <view
            v-for="item in adjustItems"
            :key="item.adjustmentId"
            class="preview-item"
          >
            <view class="preview-item-header" @click="toggleExpand(item.adjustmentId)">
              <view class="preview-item-left">
                <view
                  class="preview-checkbox"
                  :class="{ 'preview-checkbox-checked': selectedIds.includes(item.adjustmentId) }"
                  @click.stop="toggleSelect(item.adjustmentId)"
                >
                  <view v-if="selectedIds.includes(item.adjustmentId)" class="i-solar:check-circle-bold" />
                </view>
                <text class="preview-item-name">{{ item.planTypeName }}</text>
              </view>
              <view class="preview-toggle i-solar:arrow-down-outline" :class="{ 'expanded': expandedId === item.adjustmentId }" />
            </view>
            <view v-if="expandedId === item.adjustmentId" class="preview-item-content">
              <chatMarkdown :content="item.afterContent" class="preview-markdown" />
            </view>
          </view>
        </scroll-view>

        <view class="batch-actions">
          <view class="batch-btn batch-btn-cancel" @click="handleCancel">
            <text class="batch-btn-text">取消</text>
          </view>
          <view class="batch-btn batch-btn-confirm" @click="handleConfirm">
            <text class="batch-btn-text confirm-text">确认应用（{{ selectedIds.length }}）</text>
          </view>
        </view>
      </view>
    </view>
  </t-popup>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import TPopup from '@tdesign/uniapp/popup/popup.vue'
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue'
import { batchAiAdjust, batchConfirmAiAdjust } from '@/api/plan/adjustment'
import type { SubPlanAdjustItem } from '@/types/biz/plan/plan'

const props = defineProps<{
  visible: boolean
  userPlanId: string
  initialText?: string
}>()

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void
  (e: 'confirmed'): void
}>()

const inputText = ref('')
const submitting = ref(false)
const previewMode = ref(false)
const adjustItems = ref<SubPlanAdjustItem[]>([])
const selectedIds = ref<string[]>([])
const expandedId = ref('')

watch(() => props.visible, (val) => {
  if (val) {
    inputText.value = props.initialText || ''
    submitting.value = false
    previewMode.value = false
    adjustItems.value = []
    selectedIds.value = []
    expandedId.value = ''
  }
})

const handleSubmit = async () => {
  if (!inputText.value.trim() || submitting.value) return
  submitting.value = true
  try {
    const result = await batchAiAdjust({
      userPlanId: props.userPlanId,
      adjustmentRequest: inputText.value.trim(),
    })
    if (!result.adjustments || result.adjustments.length === 0) {
      uni.showToast({ title: '当前方案无需调整', icon: 'none' })
      return
    }
    adjustItems.value = result.adjustments
    selectedIds.value = result.adjustments.map((a: SubPlanAdjustItem) => a.adjustmentId)
    previewMode.value = true
  } catch (e) {
    console.error('批量调整失败', e)
    uni.showToast({ title: '调整失败，请重试', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const toggleSelect = (id: string) => {
  const idx = selectedIds.value.indexOf(id)
  if (idx >= 0) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const toggleExpand = (id: string) => {
  expandedId.value = expandedId.value === id ? '' : id
}

const handleConfirm = async () => {
  if (selectedIds.value.length === 0) {
    uni.showToast({ title: '请至少选择一项', icon: 'none' })
    return
  }
  try {
    await batchConfirmAiAdjust(selectedIds.value)
    uni.showToast({ title: '已应用调整', icon: 'none' })
    handleClose()
    emit('confirmed')
  } catch (e) {
    console.error('确认失败', e)
    uni.showToast({ title: '确认失败', icon: 'none' })
  }
}

const handleCancel = () => {
  previewMode.value = false
  adjustItems.value = []
  selectedIds.value = []
}

const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style lang="scss">
.batch-adjust-popup {
  :deep(.t-popup__content) {
    border-radius: $tf-radius-lg $tf-radius-lg 0 0;
    max-height: 85vh;
  }
}

.batch-container {
  padding: $tf-space-8 $tf-space-8 calc($tf-space-8 + env(safe-area-inset-bottom));
}

.batch-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $tf-space-6;
}

.batch-title {
  font-size: $tf-text-xl;
  font-weight: 600;
  color: $tf-gray-900;
}

.batch-close {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-500;
}

.batch-textarea {
  width: 100%;
  height: 240rpx;
  padding: $tf-space-4;
  background-color: $tf-page-bg-color;
  border-radius: $tf-radius-md;
  font-size: $tf-text-base;
  color: $tf-gray-800;
  box-sizing: border-box;
}

.batch-submit-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $tf-space-4;
}

.char-count {
  font-size: $tf-text-xs;
  color: $tf-gray-400;
}

.batch-submit-btn {
  padding: $tf-space-3 $tf-space-8;
  background: $tf-gradient-brand;
  border-radius: $tf-radius-pill;
}

.batch-submit-btn-disabled {
  opacity: 0.5;
}

.batch-submit-btn-text {
  font-size: $tf-text-base;
  color: $tf-surface;
}

.preview-hint {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-bottom: $tf-space-4;
}

.preview-list {
  max-height: 500rpx;
}

.preview-item {
  margin-bottom: $tf-space-3;
  background-color: $tf-page-bg-color;
  border-radius: $tf-radius-md;
  overflow: hidden;
}

.preview-item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $tf-space-4;
}

.preview-item-left {
  display: flex;
  align-items: center;
  gap: $tf-space-3;
}

.preview-checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $tf-gray-300;
  border-radius: $tf-radius-sm;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-checkbox-checked {
  background-color: $tf-brand;
  border-color: $tf-brand;

  .i-solar:check-circle-bold {
    width: 24rpx;
    height: 24rpx;
    color: $tf-surface;
  }
}

.preview-item-name {
  font-size: $tf-text-base;
  font-weight: 500;
  color: $tf-gray-800;
}

.preview-toggle {
  width: 28rpx;
  height: 28rpx;
  color: $tf-gray-400;
  transition: transform 0.2s;

  &.expanded {
    transform: rotate(180deg);
  }
}

.preview-item-content {
  padding: 0 $tf-space-4 $tf-space-4;
}

.preview-markdown {
  font-size: $tf-text-sm;
}

.batch-actions {
  display: flex;
  gap: $tf-space-4;
  margin-top: $tf-space-6;
}

.batch-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $tf-space-4;
  border-radius: $tf-radius-pill;
}

.batch-btn-cancel {
  background-color: $tf-surface-dim;
}

.batch-btn-confirm {
  background: $tf-gradient-brand;
}

.batch-btn-text {
  font-size: $tf-text-base;
  color: $tf-gray-700;
}

.confirm-text {
  color: $tf-surface;
  font-weight: 600;
}
</style>
