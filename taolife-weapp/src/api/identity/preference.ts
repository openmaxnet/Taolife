import { get, post } from '@/utils/request'
import type {
  UserPreferenceProfile,
  HealthProfileParam,
  DietaryPreferenceParam,
  ExercisePreferenceParam,
  LifestylePreferenceParam,
  HealthGoalsParam,
  AiPreferenceParam,
} from '@/types'

/** 获取偏好聚合档案 */
export const getPreferenceProfile = () =>
  get<UserPreferenceProfile>('/api/identity/preference/getProfile')

/** 更新身体数据 */
export const updateHealthProfile = (data: HealthProfileParam) =>
  post('/api/identity/preference/updateHealthProfile', data)

/** 更新饮食偏好 */
export const updateDietary = (data: DietaryPreferenceParam) =>
  post('/api/identity/preference/updateDietary', data)

/** 更新运动偏好 */
export const updateExercise = (data: ExercisePreferenceParam) =>
  post('/api/identity/preference/updateExercise', data)

/** 更新生活习惯 */
export const updateLifestyle = (data: LifestylePreferenceParam) =>
  post('/api/identity/preference/updateLifestyle', data)

/** 更新健康目标 */
export const updateHealthGoals = (data: HealthGoalsParam) =>
  post('/api/identity/preference/updateHealthGoals', data)

/** 更新AI偏好 */
export const updateAiPreference = (data: AiPreferenceParam) =>
  post('/api/identity/preference/updateAiPreference', data)
