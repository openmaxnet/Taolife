<template>
  <view class="page-container">
    <TLTopBar :title="pageTitle" />
    <view class="content-area" :style="{ paddingTop: contentPaddingTop + 'px' }">

      <!-- 身体数据 -->
      <template v-if="showSection('health')">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">身高(cm)</text>
            <input class="form-input" type="digit" v-model="health.height" placeholder="请输入身高" />
          </view>
          <view class="form-item">
            <text class="form-label">体重(kg)</text>
            <input class="form-input" type="digit" v-model="health.weight" placeholder="请输入体重" />
          </view>
          <view class="form-item">
            <text class="form-label">血型</text>
            <picker :range="bloodTypeOptions" :value="health.bloodType ? health.bloodType - 1 : -1" @change="(e: any) => health.bloodType = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', health.bloodType ? '' : 'placeholder']">
                  {{ health.bloodType ? bloodTypeOptions[health.bloodType - 1] : '请选择血型' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">过敏史</text>
            <textarea class="form-textarea" v-model="health.allergyHistory" placeholder="请描述过敏史（如无则留空）" :maxlength="500" />
          </view>
          <view class="form-item">
            <text class="form-label">病史</text>
            <textarea class="form-textarea" v-model="health.medicalHistory" placeholder="请描述病史（如无则留空）" :maxlength="500" />
          </view>
        </view>
      </template>

      <!-- 饮食偏好 -->
      <template v-if="showSection('dietary')">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">口味偏好</text>
            <picker :range="textureOptions" :value="dietary.preferredFoodTexture ? dietary.preferredFoodTexture - 1 : -1" @change="(e: any) => dietary.preferredFoodTexture = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', dietary.preferredFoodTexture ? '' : 'placeholder']">
                  {{ dietary.preferredFoodTexture ? textureOptions[dietary.preferredFoodTexture - 1] : '请选择口味偏好' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">食性偏好</text>
            <picker :range="natureOptions" :value="dietary.natureIndex" @change="(e: any) => dietary.natureIndex = e.detail.value">
              <view class="form-picker">
                <text :class="['form-picker-text', dietary.natureIndex >= 0 ? '' : 'placeholder']">
                  {{ dietary.natureIndex >= 0 ? natureOptions[dietary.natureIndex] : '请选择食性偏好' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">忌口食材ID（逗号分隔）</text>
            <textarea class="form-textarea" v-model="dietary.dislikedFoods" placeholder="输入食材ID，多个用逗号分隔" />
          </view>
          <view class="form-item">
            <text class="form-label">过敏食材ID（逗号分隔）</text>
            <textarea class="form-textarea" v-model="dietary.allergicFoods" placeholder="输入食材ID，多个用逗号分隔" />
          </view>
          <view class="form-item">
            <text class="form-label">饮食限制标签（逗号分隔）</text>
            <textarea class="form-textarea" v-model="dietary.dietaryRestrictions" placeholder="如 vegetarian, low_sugar" />
          </view>
          <view class="form-item">
            <text class="form-label">菜系偏好（逗号分隔）</text>
            <textarea class="form-textarea" v-model="dietary.preferredCuisines" placeholder="如 sichuan, cantonese" />
          </view>
          <view class="form-item">
            <text class="form-label">目标饮水量(ml)</text>
            <input class="form-input" type="number" v-model="dietary.dailyWaterIntake" placeholder="如 2000" />
          </view>
          <view class="form-item">
            <text class="form-label">饮食目标</text>
            <textarea class="form-textarea" v-model="dietary.dietaryGoal" placeholder="描述你的饮食目标" :maxlength="200" />
          </view>
        </view>
      </template>

      <!-- 运动偏好 -->
      <template v-if="showSection('exercise')">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">偏好运动类型</text>
            <picker :range="exerciseTypeOptions" :value="exercise.preferredExerciseType ? exercise.preferredExerciseType - 1 : -1" @change="(e: any) => onPickerChange(e, exercise, 'preferredExerciseType')">
              <view class="form-picker">
                <text :class="['form-picker-text', exercise.preferredExerciseType ? '' : 'placeholder']">
                  {{ exercise.preferredExerciseType ? exerciseTypeOptions[exercise.preferredExerciseType - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">偏好运动强度</text>
            <picker :range="intensityOptions" :value="exercise.preferredExerciseIntensity ? exercise.preferredExerciseIntensity - 1 : -1" @change="(e: any) => onPickerChange(e, exercise, 'preferredExerciseIntensity')">
              <view class="form-picker">
                <text :class="['form-picker-text', exercise.preferredExerciseIntensity ? '' : 'placeholder']">
                  {{ exercise.preferredExerciseIntensity ? intensityOptions[exercise.preferredExerciseIntensity - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">偏好运动时间</text>
            <picker :range="exerciseTimeOptions" :value="exercise.preferredExerciseTime ? exercise.preferredExerciseTime - 1 : -1" @change="(e: any) => onPickerChange(e, exercise, 'preferredExerciseTime')">
              <view class="form-picker">
                <text :class="['form-picker-text', exercise.preferredExerciseTime ? '' : 'placeholder']">
                  {{ exercise.preferredExerciseTime ? exerciseTimeOptions[exercise.preferredExerciseTime - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">运动时长(分钟)</text>
            <input class="form-input" type="number" v-model="exercise.preferredExerciseDuration" placeholder="如 30" />
          </view>
        </view>
      </template>

      <!-- 生活习惯 -->
      <template v-if="showSection('lifestyle')">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">就寝时间</text>
            <picker mode="time" :value="lifestyle.sleepTime || '23:00'" @change="(e: any) => lifestyle.sleepTime = e.detail.value">
              <view class="form-picker">
                <text :class="['form-picker-text', lifestyle.sleepTime ? '' : 'placeholder']">{{ lifestyle.sleepTime || '请选择' }}</text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">起床时间</text>
            <picker mode="time" :value="lifestyle.wakeTime || '07:00'" @change="(e: any) => lifestyle.wakeTime = e.detail.value">
              <view class="form-picker">
                <text :class="['form-picker-text', lifestyle.wakeTime ? '' : 'placeholder']">{{ lifestyle.wakeTime || '请选择' }}</text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">睡眠质量</text>
            <picker :range="sleepQualityOptions" :value="lifestyle.sleepQuality ? lifestyle.sleepQuality - 1 : -1" @change="(e: any) => lifestyle.sleepQuality = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', lifestyle.sleepQuality ? '' : 'placeholder']">
                  {{ lifestyle.sleepQuality ? sleepQualityOptions[lifestyle.sleepQuality - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">压力水平</text>
            <picker :range="stressOptions" :value="lifestyle.stressLevel ? lifestyle.stressLevel - 1 : -1" @change="(e: any) => lifestyle.stressLevel = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', lifestyle.stressLevel ? '' : 'placeholder']">
                  {{ lifestyle.stressLevel ? stressOptions[lifestyle.stressLevel - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">吸烟状态</text>
            <picker :range="smokingOptions" :value="lifestyle.smokingStatus != null ? lifestyle.smokingStatus : -1" @change="(e: any) => lifestyle.smokingStatus = e.detail.value >= 0 ? e.detail.value : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', lifestyle.smokingStatus != null ? '' : 'placeholder']">
                  {{ lifestyle.smokingStatus != null ? smokingOptions[lifestyle.smokingStatus] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">饮酒状态</text>
            <picker :range="drinkingOptions" :value="lifestyle.drinkingStatus != null ? lifestyle.drinkingStatus : -1" @change="(e: any) => lifestyle.drinkingStatus = e.detail.value >= 0 ? e.detail.value : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', lifestyle.drinkingStatus != null ? '' : 'placeholder']">
                  {{ lifestyle.drinkingStatus != null ? drinkingOptions[lifestyle.drinkingStatus] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">职业</text>
            <input class="form-input" v-model="lifestyle.occupation" placeholder="请输入职业" />
          </view>
        </view>
      </template>

      <!-- 健康目标 -->
      <template v-if="showSection('goals')">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">主要健康目标</text>
            <picker :range="goalOptions" :value="goals.healthGoalPrimary ? goals.healthGoalPrimary - 1 : -1" @change="(e: any) => goals.healthGoalPrimary = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', goals.healthGoalPrimary ? '' : 'placeholder']">
                  {{ goals.healthGoalPrimary ? goalOptions[goals.healthGoalPrimary - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">更多目标（可多选）</text>
            <view class="tag-list">
              <view v-for="g in allGoals" :key="g.code" :class="['tag-item', selectedGoals.includes(g.code) ? 'tag-active' : '']" @click="toggleGoal(g.code)">
                <text class="tag-text">{{ g.name }}</text>
              </view>
            </view>
          </view>
        </view>
      </template>

      <!-- AI偏好 -->
      <template v-if="showSection('ai')">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">回复风格</text>
            <picker :range="toneOptions" :value="ai.aiTonePreference ? ai.aiTonePreference - 1 : -1" @change="(e: any) => ai.aiTonePreference = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', ai.aiTonePreference ? '' : 'placeholder']">
                  {{ ai.aiTonePreference ? toneOptions[ai.aiTonePreference - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">回复详细程度</text>
            <picker :range="detailOptions" :value="ai.aiDetailLevel ? ai.aiDetailLevel - 1 : -1" @change="(e: any) => ai.aiDetailLevel = e.detail.value >= 0 ? e.detail.value + 1 : undefined">
              <view class="form-picker">
                <text :class="['form-picker-text', ai.aiDetailLevel ? '' : 'placeholder']">
                  {{ ai.aiDetailLevel ? detailOptions[ai.aiDetailLevel - 1] : '请选择' }}
                </text>
                <view class="i-solar:alt-arrow-right-linear picker-arrow" />
              </view>
            </picker>
          </view>
        </view>
      </template>

      <!-- 保存 -->
      <view class="form-actions">
        <button class="btn-save" :loading="saving" @click="save">保存</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import TLTopBar from '@/components/TLTopBar/index.vue'
import { usePageLayout } from '@/composables/usePageLayout'
import { enAuth } from '@/utils/authManager'
import {
  getPreferenceProfile,
  updateHealthProfile,
  updateDietary,
  updateExercise,
  updateLifestyle,
  updateHealthGoals,
  updateAiPreference,
} from '@/api/identity/preference'

const { contentPaddingTop } = usePageLayout()

const saving = ref(false)

// ──── section 参数：空=全部，否则只显示对应区块 ────
const section = ref('')

const sectionTitleMap: Record<string, string> = {
  health: '身体数据',
  dietary: '饮食偏好',
  exercise: '运动偏好',
  lifestyle: '生活习惯',
  goals: '健康目标',
  ai: 'AI偏好',
}

const pageTitle = computed(() => sectionTitleMap[section.value] || '编辑偏好')

const showSection = (key: string): boolean => {
  return !section.value || section.value === key
}

onLoad((options) => {
  section.value = options?.section || ''
})

// ──── 选项 ────
const bloodTypeOptions = ['A型', 'B型', 'AB型', 'O型']
const textureOptions = ['清淡', '浓郁', '辛辣', '偏甜']
const natureOptions = ['温性', '凉性', '平性']
const exerciseTypeOptions = ['有氧', '力量', '柔韧', '球类', '传统功法']
const intensityOptions = ['低强度', '中强度', '高强度']
const exerciseTimeOptions = ['清晨', '上午', '下午', '傍晚', '晚上']
const sleepQualityOptions = ['优', '良', '一般', '差']
const stressOptions = ['低', '中', '高']
const smokingOptions = ['从不', '已戒', '吸烟']
const drinkingOptions = ['从不', '偶尔', '经常']
const goalOptions = ['增强免疫', '改善睡眠', '体重管理', '缓解压力', '改善消化', '增强体力']
const toneOptions = ['专业严谨', '亲切温暖', '简洁明了']
const detailOptions = ['简要', '适中', '详细']
const allGoals = [
  { code: 'immunity', name: '增强免疫' },
  { code: 'sleep', name: '改善睡眠' },
  { code: 'weight', name: '体重管理' },
  { code: 'stress', name: '缓解压力' },
  { code: 'digestion', name: '改善消化' },
  { code: 'stamina', name: '增强体力' },
  { code: 'cardiovascular', name: '心血管健康' },
  { code: 'mental', name: '心理健康' },
  { code: 'flexibility', name: '柔韧性' },
  { code: 'detox', name: '排毒养颜' },
]

// ──── 表单数据 ────
const health = reactive({
  height: '',
  weight: '',
  bloodType: undefined as number | undefined,
  allergyHistory: '',
  medicalHistory: '',
})

const dietary = reactive({
  preferredFoodTexture: undefined as number | undefined,
  natureIndex: -1,
  dislikedFoods: '',
  allergicFoods: '',
  dietaryRestrictions: '',
  preferredCuisines: '',
  dailyWaterIntake: '',
  dietaryGoal: '',
})

const exercise = reactive({
  preferredExerciseType: undefined as number | undefined,
  preferredExerciseIntensity: undefined as number | undefined,
  preferredExerciseTime: undefined as number | undefined,
  preferredExerciseDuration: '',
})

const lifestyle = reactive({
  sleepTime: '',
  wakeTime: '',
  sleepQuality: undefined as number | undefined,
  stressLevel: undefined as number | undefined,
  smokingStatus: undefined as number | undefined,
  drinkingStatus: undefined as number | undefined,
  occupation: '',
})

const goals = reactive({
  healthGoalPrimary: undefined as number | undefined,
})

const selectedGoals = ref<string[]>([])

const ai = reactive({
  aiTonePreference: undefined as number | undefined,
  aiDetailLevel: undefined as number | undefined,
})

// ──── 工具方法 ────
const onPickerChange = (e: any, obj: any, field: string) => {
  const idx = e.detail.value
  if (idx >= 0) obj[field] = idx + 1
}

const splitToList = (text: string): string[] | undefined => {
  const list = text.split(',').map(s => s.trim()).filter(Boolean)
  return list.length ? list : undefined
}

const joinList = (list?: string[]): string => {
  return list?.join(', ') || ''
}

const toggleGoal = (code: string) => {
  const idx = selectedGoals.value.indexOf(code)
  if (idx >= 0) selectedGoals.value.splice(idx, 1)
  else selectedGoals.value.push(code)
}

// ──── 保存：只保存当前可见的 section ────
const save = async () => {
  saving.value = true
  try {
    const tasks: Promise<any>[] = []
    const s = section.value

    if (!s || s === 'health') {
      tasks.push(updateHealthProfile({
        height: health.height ? Number(health.height) : undefined,
        weight: health.weight ? Number(health.weight) : undefined,
        bloodType: health.bloodType,
        allergyHistory: health.allergyHistory || undefined,
        medicalHistory: health.medicalHistory || undefined,
      }))
    }
    if (!s || s === 'dietary') {
      tasks.push(updateDietary({
        preferredFoodNature: dietary.natureIndex >= 0 ? natureOptions[dietary.natureIndex] : undefined,
        preferredFoodTexture: dietary.preferredFoodTexture,
        dislikedFoods: splitToList(dietary.dislikedFoods),
        allergicFoods: splitToList(dietary.allergicFoods),
        dietaryRestrictions: splitToList(dietary.dietaryRestrictions),
        preferredCuisines: splitToList(dietary.preferredCuisines),
        dailyWaterIntake: dietary.dailyWaterIntake ? Number(dietary.dailyWaterIntake) : undefined,
        dietaryGoal: dietary.dietaryGoal || undefined,
      }))
    }
    if (!s || s === 'exercise') {
      tasks.push(updateExercise({
        preferredExerciseType: exercise.preferredExerciseType,
        preferredExerciseIntensity: exercise.preferredExerciseIntensity,
        preferredExerciseTime: exercise.preferredExerciseTime,
        preferredExerciseDuration: exercise.preferredExerciseDuration ? Number(exercise.preferredExerciseDuration) : undefined,
      }))
    }
    if (!s || s === 'lifestyle') {
      tasks.push(updateLifestyle({
        sleepTime: lifestyle.sleepTime || undefined,
        wakeTime: lifestyle.wakeTime || undefined,
        sleepQuality: lifestyle.sleepQuality,
        stressLevel: lifestyle.stressLevel,
        smokingStatus: lifestyle.smokingStatus,
        drinkingStatus: lifestyle.drinkingStatus,
        occupation: lifestyle.occupation || undefined,
      }))
    }
    if (!s || s === 'goals') {
      tasks.push(updateHealthGoals({
        healthGoalPrimary: goals.healthGoalPrimary,
        healthGoals: selectedGoals.value.length ? selectedGoals.value : undefined,
      }))
    }
    if (!s || s === 'ai') {
      tasks.push(updateAiPreference({
        aiTonePreference: ai.aiTonePreference,
        aiDetailLevel: ai.aiDetailLevel,
      }))
    }

    await Promise.all(tasks)
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e) {
    console.error('保存失败', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}

// ──── 加载 ────
onMounted(async () => {
  await enAuth()
  try {
    const p = await getPreferenceProfile()
    if (p.height) health.height = String(p.height)
    if (p.weight) health.weight = String(p.weight)
    health.bloodType = p.bloodType
    health.allergyHistory = p.allergyHistory || ''
    health.medicalHistory = p.medicalHistory || ''

    dietary.preferredFoodTexture = p.preferredFoodTexture
    dietary.natureIndex = p.preferredFoodNature ? natureOptions.indexOf(p.preferredFoodNature) : -1
    dietary.dislikedFoods = joinList(p.dislikedFoods)
    dietary.allergicFoods = joinList(p.allergicFoods)
    dietary.dietaryRestrictions = joinList(p.dietaryRestrictions)
    dietary.preferredCuisines = joinList(p.preferredCuisines)
    dietary.dailyWaterIntake = p.dailyWaterIntake ? String(p.dailyWaterIntake) : ''
    dietary.dietaryGoal = p.dietaryGoal || ''

    exercise.preferredExerciseType = p.preferredExerciseType
    exercise.preferredExerciseIntensity = p.preferredExerciseIntensity
    exercise.preferredExerciseTime = p.preferredExerciseTime
    exercise.preferredExerciseDuration = p.preferredExerciseDuration ? String(p.preferredExerciseDuration) : ''

    lifestyle.sleepTime = p.sleepTime || ''
    lifestyle.wakeTime = p.wakeTime || ''
    lifestyle.sleepQuality = p.sleepQuality
    lifestyle.stressLevel = p.stressLevel
    lifestyle.smokingStatus = p.smokingStatus
    lifestyle.drinkingStatus = p.drinkingStatus
    lifestyle.occupation = p.occupation || ''

    goals.healthGoalPrimary = p.healthGoalPrimary
    selectedGoals.value = p.healthGoals || []

    ai.aiTonePreference = p.aiTonePreference
    ai.aiDetailLevel = p.aiDetailLevel
  } catch (e) {
    console.error('加载偏好失败', e)
  }
})
</script>

<style lang="scss">
.form-card {
  margin: $tf-space-4 $tf-space-6;
  padding: $tf-space-4 $tf-space-6;
  background: $tf-surface;
  border-radius: $tf-radius-2xl;
}

.form-item {
  padding: $tf-space-4 0;
  border-bottom: 1rpx solid $tf-gray-100;

  &:last-child {
    border-bottom: none;
  }
}

.form-label {
  display: block;
  font-size: $tf-text-md;
  color: $tf-gray-800;
  font-weight: 500;
  margin-bottom: $tf-space-2;
}

.form-input {
  width: 100%;
  height: 72rpx;
  padding: 0 $tf-space-3;
  background: $tf-gray-50;
  border-radius: $tf-radius-lg;
  font-size: $tf-text-md;
  color: $tf-gray-800;
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: $tf-space-3;
  background: $tf-gray-50;
  border-radius: $tf-radius-lg;
  font-size: $tf-text-md;
  color: $tf-gray-800;
  box-sizing: border-box;
}

.form-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72rpx;
  padding: 0 $tf-space-3;
  background: $tf-gray-50;
  border-radius: $tf-radius-lg;
}

.form-picker-text {
  font-size: $tf-text-md;
  color: $tf-gray-800;

  &.placeholder {
    color: $tf-gray-400;
  }
}

.picker-arrow {
  font-size: 32rpx;
  color: $tf-gray-400;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: $tf-space-3;
  margin-top: $tf-space-2;
}

.tag-item {
  padding: $tf-space-2 $tf-space-5;
  border-radius: $tf-radius-3xl;
  background: $tf-gray-50;
  border: 2rpx solid $tf-gray-200;
}

.tag-active {
  background: $tf-brand-bg;
  border-color: $tf-brand;
}

.tag-text {
  font-size: $tf-text-sm;
  color: $tf-gray-600;
}

.tag-active .tag-text {
  color: $tf-brand;
}

.form-actions {
  padding: $tf-space-6;
  padding-bottom: $tf-space-10;
}

.btn-save {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: $tf-radius-3xl;
  font-size: $tf-text-lg;
  font-weight: 500;
  background: linear-gradient(135deg, $tf-brand, $tf-brand-700);
  color: #fff;
  border: none;
}
</style>
