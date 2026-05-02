/**
 * 用户偏好/健康档案类型定义
 * 对应后端 UserPreferenceProfileVO
 */

/** 偏好聚合档案 */
export interface UserPreferenceProfile {
  id: string

  // 身体数据
  height?: number
  weight?: number
  bloodType?: number
  allergyHistory?: string
  medicalHistory?: string

  // 饮食偏好
  preferredFoodNature?: string
  preferredFoodTexture?: number
  dislikedFoods?: string[]
  allergicFoods?: string[]
  dietaryRestrictions?: string[]
  preferredCuisines?: string[]
  dailyWaterIntake?: number
  dietaryGoal?: string

  // 运动偏好
  preferredExerciseType?: number
  preferredExerciseIntensity?: number
  preferredExerciseTime?: number
  preferredExerciseDuration?: number

  // 生活习惯
  sleepTime?: string
  wakeTime?: string
  sleepQuality?: number
  stressLevel?: number
  smokingStatus?: number
  drinkingStatus?: number
  occupation?: string

  // 健康目标
  healthGoalPrimary?: number
  healthGoals?: string[]

  // AI偏好
  aiTonePreference?: number
  aiDetailLevel?: number

  // 完善度
  preferenceCompleteness: number
  lastUserEditTime?: string
  updateTime?: string
}

/** 身体数据参数 */
export interface HealthProfileParam {
  height?: number
  weight?: number
  bloodType?: number
  allergyHistory?: string
  medicalHistory?: string
}

/** 饮食偏好参数 */
export interface DietaryPreferenceParam {
  preferredFoodNature?: string
  preferredFoodTexture?: number
  dislikedFoods?: string[]
  allergicFoods?: string[]
  dietaryRestrictions?: string[]
  preferredCuisines?: string[]
  dailyWaterIntake?: number
  dietaryGoal?: string
}

/** 运动偏好参数 */
export interface ExercisePreferenceParam {
  preferredExerciseType?: number
  preferredExerciseIntensity?: number
  preferredExerciseTime?: number
  preferredExerciseDuration?: number
}

/** 生活习惯参数 */
export interface LifestylePreferenceParam {
  sleepTime?: string
  wakeTime?: string
  sleepQuality?: number
  stressLevel?: number
  smokingStatus?: number
  drinkingStatus?: number
  occupation?: string
}

/** 健康目标参数 */
export interface HealthGoalsParam {
  healthGoalPrimary?: number
  healthGoals?: string[]
}

/** AI偏好参数 */
export interface AiPreferenceParam {
  aiTonePreference?: number
  aiDetailLevel?: number
}
