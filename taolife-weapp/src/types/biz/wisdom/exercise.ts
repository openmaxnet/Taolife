/**
 * 运动知识库类型定义
 * 对应后端 HealthExerciseListVO 和 HealthExerciseDetailVO
 */

/**
 * 运动列表项
 */
export interface ExerciseListItem {
  id: string;
  name: string;
  namePinyin: string;
  category: number;
  categoryName: string;
  intensity: number;
  intensityName: string;
  difficultyLevel: number;
  durationMin: number;
  caloriesConsumption: number;
  efficacy: string;
  imageUrl: string;
  videoUrl: string;
  videoType: number;
  viewCount: number;
  collectCount: number;
}

/**
 * 运动详情
 */
export interface ExerciseDetail extends ExerciseListItem {
  description: string;
  steps: string;
  indications: string;
  contraindications: string;
  targetConstitutionCodes: string;
  contraConstitutionCodes: string;
  targetSeason: string;
  targetAgeGroup: string;
  relatedExercises: ExerciseListItem[];
}

/**
 * 运动查询参数
 */
export interface ExerciseQueryParam {
  category?: number;
  intensity?: number;
  keyword?: string;
  pageNo?: number;
  pageSize?: number;
}

/**
 * 运动列表响应（分页）
 */
export interface ExercisePageResult {
  list: ExerciseListItem[];
  total: number;
  page: number;
  pageSize: number;
}
