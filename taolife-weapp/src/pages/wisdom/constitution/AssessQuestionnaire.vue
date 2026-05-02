<template>
  <view class="page-container">

    <!-- 顶部导航栏 -->
    <TLTopBar title="体质辨识" />

    <!-- 加载失败组件 -->
    <TLReload
      v-if="loadFailed"
      title="加载失败"
      description="无法获取体质测评信息，请稍后重试"
      @reload="checkStatus"
    />

    <!-- 正在加载 -->
    <view v-if="isLoading && !loadFailed" class="loading-state">
      <TLLoading />
    </view>

    <!-- 主内容区 -->
    <view v-if="!isLoading && !loadFailed" class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 模式选择页 -->
      <view v-if="currentPage === 'mode'" class="mode-selection">
        <view class="page-header">
          <text class="page-title">体质辨识</text>
          <text class="page-subtitle">基于《体质分类与判定》标准</text>
        </view>

        <view class="intro-card">
          <text class="intro-title">什么是体质？</text>
          <text class="intro-text">体质是人体在先天禀赋和后天调养基础上形成的相对稳定的生理特性和病理倾向。中医将体质分为九种类型，不同体质有不同的养生方法。</text>
        </view>

        <view class="mode-cards">
          <view
            v-for="mode in assessmentModes"
            :key="mode.value"
            class="mode-card"
            @click="startAssessment(mode.value)"
          >
            <view :class="mode.iconClass" class="mode-icon" />
            <text class="mode-name">{{ mode.name }}</text>
            <text class="mode-desc">{{ mode.desc }}</text>
            <text class="mode-time">{{ mode.time }}</text>
          </view>
        </view>
      </view>

      <!-- 测评答题页 -->
      <view v-else-if="currentPage === 'assessment'" class="assessment-page">
        <!-- 进度条 -->
        <view class="progress-wrap">
          <t-progress
            theme="plump"
            :color="{ from: '#10B981', to: '#34D399' }"
            :percentage="progressPercent"
            :label="`${currentQuestionIndex + 1} / ${questions.length}`"
            status="active"
          />
        </view>

        <!-- 题目卡片 -->
        <view class="question-card">
          <!-- 题目内容 -->
          <text class="question-content">{{ currentQuestion?.questionText }}</text>
          <text v-if="currentQuestion?.questionTextSecondary" class="question-hint">
            {{ currentQuestion.questionTextSecondary }}
          </text>

          <!-- 选项列表 -->
          <view class="options-list">
            <view
              v-for="option in currentQuestion?.options"
              :key="option.id"
              class="option-item"
              :class="{ 'option-selected': answers[currentQuestionIndex]?.optionValue === option.optionValue }"
              @click="selectAnswer(option)"
            >
              <text class="option-label">{{ option.optionText }}</text>
              <text v-if="answers[currentQuestionIndex]?.optionValue === option.optionValue" class="option-check">✓</text>
            </view>
          </view>
        </view>

        <!-- 底部操作 -->
        <view class="action-bar">
          <button
            v-if="currentQuestionIndex > 0"
            class="btn-prev"
            @click="prevQuestion"
          >
            上一题
          </button>
          <button
            v-if="answers[currentQuestionIndex]"
            class="btn-next"
            @click="nextOrSubmit"
          >
            {{ isLastQuestion ? '提交' : '下一题' }}
          </button>
        </view>
      </view>

    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TProgress from '@tdesign/uniapp/progress/progress.vue'
import TLTopBar from '@/components/TLTopBar/index.vue'
import TLReload from '@/components/TLReload/index.vue'
import TLLoading from '@/components/TLLoading/index.vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { usePageLayout } from '@/composables/usePageLayout'
import { enAuth } from '@/utils/authManager'
import {
  getQuestionList,
  submitAssessment
} from '@/api/wisdom/constitution'
import type { ConstitutionQuestion, ConstitutionOption, AnswerItem } from '@/types/biz/ai/constitution'

/**
 * 测评模式配置
 */
interface AssessmentMode {
  /** 模式值 */
  value: number
  /** 模式名称 */
  name: string
  /** 模式描述 */
  desc: string
  /** 预计用时 */
  time: string
  /** 图标类名 */
  iconClass: string
}

/**
 * 测评模式列表
 */
const assessmentModes: AssessmentMode[] = [
  {
    value: 1,
    name: '简易模式',
    desc: '约18题，快速了解体质',
    time: '1-2分钟',
    iconClass: 'i-solar:bolt-outline'
  },
  {
    value: 2,
    name: '精细模式',
    desc: '约90题，全面精准分析',
    time: '5-10分钟',
    iconClass: 'i-solar:clipboard-text-outline'
  }
]

// 页面布局
const { contentPaddingTop } = usePageLayout()

// 页面状态
type PageType = 'mode' | 'assessment'
const currentPage = ref<PageType>('mode')

// 测评模式
const assessmentMode = ref<number>(1)

// 题目数据
const questions = ref<ConstitutionQuestion[]>([])
const currentQuestionIndex = ref(0)

// 用户答案 - 数组索引对应题目索引
const answers = ref<(AnswerItem | null)[]>([])

// 加载状态
const isLoading = ref(true)


// 加载失败状态
const loadFailed = ref(false)

// 当前题目
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || null
})

// 是否是最后一题
const isLastQuestion = computed(() => {
  return currentQuestionIndex.value === questions.value.length - 1
})

// 进度百分比
const progressPercent = computed(() => {
  if (questions.value.length === 0) return 0
  return ((currentQuestionIndex.value + 1) / questions.value.length) * 100
})

// 下拉刷新
onPullDownRefresh(() => {
  uni.stopPullDownRefresh()
})

// 页面加载
onMounted(async () => {
  await enAuth()
  await checkStatus()
})

// 检查用户测评状态
const checkStatus = async () => {
  isLoading.value = true
  loadFailed.value = false
  try {
    // 无论是否有测评记录，都显示模式选择页面
    currentPage.value = 'mode'
  } catch (e) {
    console.error('检查测评状态失败', e)
    loadFailed.value = true
  } finally {
    isLoading.value = false
  }
}

// 开始测评
const startAssessment = async (mode: number) => {
  isLoading.value = true
  assessmentMode.value = mode

  try {
    uni.showLoading({ title: '加载中...', mask: true })

    const data = await getQuestionList(mode)
    questions.value = data || []

    // 初始化答案数组
    answers.value = new Array(questions.value.length).fill(null)
    currentQuestionIndex.value = 0

    currentPage.value = 'assessment'

    uni.hideLoading()
  } catch (e: any) {
    uni.hideLoading()
    uni.showToast({
      title: e?.msg || '加载题目失败',
      icon: 'none'
    })
  } finally {
    isLoading.value = false
  }
}

// 选择答案
const selectAnswer = (option: ConstitutionOption) => {
  answers.value[currentQuestionIndex.value] = {
    questionId: currentQuestion.value!.id,
    optionValue: option.optionValue
  }
}

// 上一题
const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题或提交
const nextOrSubmit = async () => {
  // 检查是否已答题
  if (!answers.value[currentQuestionIndex.value]) {
    uni.showToast({
      title: '请选择一个答案',
      icon: 'none'
    })
    return
  }

  if (isLastQuestion.value) {
    // 提交答案
    await submitResult()
  } else {
    // 下一题
    currentQuestionIndex.value++
  }
}

// 提交结果
const submitResult = async () => {
  // 过滤掉未答题的题目
  const validAnswers = answers.value.filter((a): a is AnswerItem => a !== null)

  if (validAnswers.length !== questions.value.length) {
    uni.showToast({
      title: '请完成所有题目',
      icon: 'none'
    })
    return
  }

  try {
    uni.showLoading({ title: '分析中...', mask: true })

    await submitAssessment(assessmentMode.value, {
      answers: validAnswers
    })

    // 提交成功，返回 index 页面查看结果
    uni.hideLoading()
    uni.redirectTo({
      url: '/pages/wisdom/constitution/index'
    })
  } catch (e: any) {
    uni.hideLoading()
    uni.showToast({
      title: e?.msg || '提交失败',
      icon: 'none'
    })
  }
}

</script>

<style lang="scss">
.page-container {
  padding-bottom: 120rpx;
}

.page-header {
  padding: 48rpx 24rpx 32rpx;
  text-align: center;
  // background: $tf-surface;
}

.page-title {
  display: block;
  font-size: 40rpx;
  color: $tf-primary-color;
  font-weight: bold;
}

.page-subtitle {
  display: block;
  font-size: 24rpx;
  color: $tf-gray-500;
  margin-top: 12rpx;
}

.content-area {
  padding-bottom: 32rpx;
}

// 模式选择页
.mode-selection {
  padding: 0 24rpx;
}

.intro-card {
  background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.intro-title {
  display: block;
  font-size: 28rpx;
  color: #065F46;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.intro-text {
  display: block;
  font-size: 26rpx;
  color: #047857;
  line-height: 1.6;
}

.mode-cards {
  display: flex;
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.mode-card {
  flex: 1;
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 40rpx 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  // box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.mode-card:active {
  transform: scale(0.98);
  // box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.mode-icon {
  width: 80rpx;
  height: 80rpx;
  font-size: 48rpx;
  color: $tf-brand;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mode-name {
  font-size: 28rpx;
  color: $tf-gray-900;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.mode-desc {
  font-size: 24rpx;
  color: $tf-gray-600;
  margin-bottom: 8rpx;
  text-align: center;
}

.mode-time {
  font-size: 22rpx;
  color: $tf-gray-500;
}

// 测评答题页
.assessment-page {
  padding: 24rpx;
}

.progress-wrap {
  margin-bottom: 24rpx;
}

.question-card {
  background: $tf-surface;
  border-radius: 24rpx;
  padding: 48rpx;
  // box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.question-content {
  display: block;
  font-size: 34rpx;
  color: $tf-gray-900;
  font-weight: 500;
  line-height: 1.5;
  margin-bottom: 16rpx;
}

.question-hint {
  display: block;
  font-size: 26rpx;
  color: $tf-gray-500;
  margin-bottom: 32rpx;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.option-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  border: $tf-border-input;
  border-radius: 16rpx;
  transition: all 0.2s ease;
}

.option-item:active {
  background: $tf-gray-50;
}

.option-selected {
  background: $tf-brand-bg;
  border-color: $tf-brand-light;
}

.option-label {
  font-size: 28rpx;
  color: $tf-gray-800;
}

.option-selected .option-label {
  color: $tf-brand;
  font-weight: 500;
}

.option-check {
  color: $tf-brand-light;
  font-size: 32rpx;
  font-weight: bold;
}

.action-bar {
  display: flex;
  gap: 24rpx;
  margin-top: 32rpx;
}

.btn-prev, .btn-next {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  font-weight: 500;
}

.btn-prev {
  background: $tf-surface;
  color: $tf-gray-600;
  border: $tf-border-input;
}

.btn-next {
  background: linear-gradient(135deg, #10B981 0%, $tf-brand 100%);
  color: $tf-surface;
}

// 加载状态
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
}

</style>
