<template>
  <t-popup
    :visible="visible"
    placement="bottom"
    :close-on-overlay-click="true"
    @close="handleClose"
    class="adjust-popup"
  >
    <view class="adjust-container">
      <!-- 标题栏 -->
      <view class="adjust-header">
        <text class="adjust-title">调整{{ planTypeName }}</text>
        <view class="adjust-close i-solar:close-circle-outline" @click="handleClose" />
      </view>

      <!-- 智能标签区 -->
      <view v-if="!previewMode" class="adjust-body">
        <view v-if="suggestionsLoading" class="suggestions-loading">
          <text class="loading-text">正在获取推荐...</text>
        </view>
        <view v-else-if="suggestions.length > 0" class="suggestions-area">
          <text class="section-label">推荐调整</text>
          <view class="suggestions-list">
            <view
              v-for="(item, index) in suggestions"
              :key="index"
              class="suggestion-tag"
              @click="selectSuggestion(item)"
            >
              <text class="suggestion-text">{{ item }}</text>
            </view>
          </view>
        </view>

        <!-- 自定义输入 -->
        <view class="input-area">
          <text class="section-label">描述你的调整需求</text>
          <textarea
            class="adjust-textarea"
            v-model="inputText"
            placeholder="例如：最近没时间运动，想简化运动方案..."
            :maxlength="500"
            :auto-height="false"
          />
          <view class="submit-row">
            <text class="char-count">{{ inputText.length }}/500</text>
            <view
              class="submit-btn"
              :class="{ 'submit-btn-disabled': !inputText.trim() || submitting }"
              @click="handleSubmit"
            >
              <text class="submit-btn-text">{{ submitting ? '生成中...' : '生成调整' }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 对比预览 -->
      <view v-else class="preview-body">
        <view class="preview-card">
          <text class="preview-label">调整后方案</text>
          <scroll-view class="preview-scroll" scroll-y>
            <chatMarkdown :content="previewContent" class="preview-markdown" />
          </scroll-view>
        </view>
        <view class="preview-actions">
          <view class="preview-btn preview-btn-cancel" @click="handleCancel">
            <text class="preview-btn-text">取消</text>
          </view>
          <view class="preview-btn preview-btn-confirm" @click="handleConfirm">
            <text class="preview-btn-text confirm-text">确认应用</text>
          </view>
        </view>
      </view>
    </view>
  </t-popup>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import TPopup from '@tdesign/uniapp/popup/popup.vue'
import chatMarkdown from '@tdesign/uniapp-chat/chat-markdown/chat-markdown.vue'
import { getAdjustmentSuggestions, aiAdjust, confirmAiAdjust } from '@/api/plan/adjustment'

const PLAN_TYPE_NAME: Record<number, string> = {
  1: '饮食方案', 2: '运动方案', 3: '穴位按摩', 4: '经络调理', 5: '生活起居',
}

const props = defineProps<{
  visible: boolean
  userPlanId: string
  planType: number
}>()

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void
  (e: 'confirmed'): void
}>()

const planTypeName = computed(() => PLAN_TYPE_NAME[props.planType] || '方案')
const suggestions = ref<string[]>([])
const suggestionsLoading = ref(false)
const inputText = ref('')
const submitting = ref(false)
const previewMode = ref(false)
const previewContent = ref('')
const currentAdjustmentId = ref('')

watch(() => props.visible, (val) => {
  if (val) {
    resetState()
    loadSuggestions()
  }
})

const resetState = () => {
  inputText.value = ''
  submitting.value = false
  previewMode.value = false
  previewContent.value = ''
  currentAdjustmentId.value = ''
}

const loadSuggestions = async () => {
  suggestionsLoading.value = true
  try {
    const result = await getAdjustmentSuggestions(props.userPlanId, props.planType)
    suggestions.value = result || []
  } catch (e) {
    console.error('获取推荐标签失败', e)
  } finally {
    suggestionsLoading.value = false
  }
}

const selectSuggestion = (text: string) => {
  inputText.value = text
}

const handleSubmit = async () => {
  if (!inputText.value.trim() || submitting.value) return
  submitting.value = true
  try {
    const result = await aiAdjust({
      userPlanId: props.userPlanId,
      planType: props.planType,
      adjustmentRequest: inputText.value.trim(),
    })
    previewContent.value = result.afterContent || ''
    currentAdjustmentId.value = result.adjustmentId || ''
    previewMode.value = true
  } catch (e) {
    console.error('AI调整失败', e)
    uni.showToast({ title: '调整失败，请重试', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const handleConfirm = async () => {
  if (!currentAdjustmentId.value) return
  try {
    await confirmAiAdjust(currentAdjustmentId.value)
    uni.showToast({ title: '已应用调整', icon: 'none' })
    handleClose()
    emit('confirmed')
  } catch (e) {
    console.error('确认调整失败', e)
    uni.showToast({ title: '确认失败', icon: 'none' })
  }
}

const handleCancel = () => {
  previewMode.value = false
  previewContent.value = ''
  currentAdjustmentId.value = ''
}

const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style lang="scss">
.adjust-popup {
  :deep(.t-popup__content) {
    border-radius: $tf-radius-lg $tf-radius-lg 0 0;
    max-height: 80vh;
  }
}

.adjust-container {
  padding: $tf-space-8 $tf-space-8 calc($tf-space-8 + env(safe-area-inset-bottom));
}

.adjust-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $tf-space-6;
}

.adjust-title {
  font-size: $tf-text-xl;
  font-weight: 600;
  color: $tf-gray-900;
}

.adjust-close {
  width: 44rpx;
  height: 44rpx;
  color: $tf-gray-500;
}

.section-label {
  font-size: $tf-text-sm;
  color: $tf-gray-500;
  margin-bottom: $tf-space-3;
}

.suggestions-loading {
  padding: $tf-space-6 0;
  text-align: center;
}

.loading-text {
  font-size: $tf-text-sm;
  color: $tf-gray-400;
}

.suggestions-area {
  margin-bottom: $tf-space-6;
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  gap: $tf-space-3;
}

.suggestion-tag {
  padding: $tf-space-2 $tf-space-5;
  background-color: $tf-brand-bg-light;
  border-radius: $tf-radius-pill;
}

.suggestion-text {
  font-size: $tf-text-sm;
  color: $tf-brand;
}

.input-area {
  margin-top: $tf-space-4;
}

.adjust-textarea {
  width: 100%;
  height: 200rpx;
  padding: $tf-space-4;
  background-color: $tf-page-bg-color;
  border-radius: $tf-radius-md;
  font-size: $tf-text-base;
  color: $tf-gray-800;
  box-sizing: border-box;
}

.submit-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $tf-space-4;
}

.char-count {
  font-size: $tf-text-xs;
  color: $tf-gray-400;
}

.submit-btn {
  padding: $tf-space-3 $tf-space-8;
  background: $tf-gradient-brand;
  border-radius: $tf-radius-pill;
}

.submit-btn-disabled {
  opacity: 0.5;
}

.submit-btn-text {
  font-size: $tf-text-base;
  color: $tf-surface;
}

.preview-body {
  display: flex;
  flex-direction: column;
}

.preview-card {
  margin-bottom: $tf-space-6;
}

.preview-label {
  font-size: $tf-text-sm;
  font-weight: 600;
  color: $tf-brand;
  margin-bottom: $tf-space-3;
}

.preview-scroll {
  max-height: 500rpx;
  padding: $tf-space-4;
  background-color: $tf-page-bg-color;
  border-radius: $tf-radius-md;
}

.preview-markdown {
  font-size: $tf-text-sm;
}

.preview-actions {
  display: flex;
  gap: $tf-space-4;
}

.preview-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $tf-space-4;
  border-radius: $tf-radius-pill;
}

.preview-btn-cancel {
  background-color: $tf-surface-dim;
}

.preview-btn-confirm {
  background: $tf-gradient-brand;
}

.preview-btn-text {
  font-size: $tf-text-base;
  color: $tf-gray-700;
}

.confirm-text {
  color: $tf-surface;
  font-weight: 600;
}
</style>
