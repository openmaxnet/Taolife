/**
 * 养生方案类型定义
 * 对应后端 HealthPlanVO / PlanTaskVO / PlanAdjustmentVO / PlanSquareVO
 */

// ==================== 方案相关 ====================

/** 方案任务摘要 */
export interface TaskSummary {
  /** 今日任务总数 */
  todayTaskCount: number;
  /** 已完成数 */
  completedCount: number;
}

/** 方案摘要（getHealthPlan接口使用） */
export interface PlanSummary {
  id: string;
  constitutionCode: string;
  constitutionName: string;
  planTitle: string;
  planTags: string;
  todayFocus: string;
  foodPlanTags: string;
  exercisePlanTags: string;
  acupointPlanTags: string;
  meridianPlanTags: string;
  lifestylePlanTags: string;
  status: number;
  taskSummary: TaskSummary;
}

/** 方案详情（getHealthPlanDetail接口使用） */
export interface HealthPlanDetail {
  id: string;
  constitutionCode: string;
  constitutionName: string;
  season: number;
  seasonName: string;
  startDate: string;
  endDate: string;
  status: number;
  cycleDays: number;
  completionRate: number;
  foodPlanContent: string;
  exercisePlanContent: string;
  acupointPlanContent: string;
  meridianPlanContent: string;
  lifestylePlanContent: string;
  foodPlanTags: string;
  exercisePlanTags: string;
  acupointPlanTags: string;
  meridianPlanTags: string;
  lifestylePlanTags: string;
  userNotes: string;
  aiAdjustmentCount: number;
  planTitle: string;
  planTags: string;
  todayFocus: string;
  taskSummary: TaskSummary;
}

/** 方案历史项 */
export interface PlanHistoryItem {
  id: string;
  planDate: string;
  constitutionName: string;
  seasonName: string;
  completionRate: number;
  totalTasks: number;
  completedTasks: number;
  userRating: number;
  isEffective: number;
  adjustmentCount: number;
  planTitle: string;
  planTags: string;
}

/** 方案生成结果 */
export interface GeneratePlanVO {
  taskId: string;
  message: string;
  estimatedTime: string;
}

/** 方案生成状态 */
export interface GenerationStatusVO {
  taskId: string;
  status: number;
  progress: number;
  result: string | null;
  errorMessage: string | null;
  message: string;
}

/** 方案反馈参数 */
export interface PlanFeedbackParam {
  feedbackType: number;
  targetId: string;
  content: string;
  rating?: number;
}

/** 方案查询参数 */
export interface HealthPlanQueryParam {
  pageNo?: number;
  pageSize?: number;
  startDate?: string;
  endDate?: string;
  status?: number;
}

// ==================== 任务相关 ====================

/** 任务列表项 */
export interface PlanTaskItem {
  id: string;
  planType: number;
  taskName: string;
  taskDescription: string;
  taskDate: string;
  status: number;
  priority: number;
  taskCategory: number;
  targetCount: number;
  completedCount: number;
  resourceType: string;
  resourceId: string;
}

/** 任务详情 */
export interface PlanTaskDetail extends PlanTaskItem {
  createTime: string;
  updateTime: string;
}

/** 任务查询参数 */
export interface PlanTaskQueryParam {
  pageNo?: number;
  pageSize?: number;
  planType?: number;
  taskDate?: string;
  status?: number;
}

/** 完成任务参数 */
export interface TaskCompleteParam {
  taskId: string;
}

// ==================== 调整相关 ====================

/** 调整记录项 */
export interface AdjustmentRecordItem {
  id: string;
  planType: number;
  adjustmentType: number;
  adjustmentReason: string;
  beforeContent: string;
  afterContent: string;
  effectivenessScore: number;
  createTime: string;
}

/** 手动调整参数 */
export interface ManualAdjustmentParam {
  userPlanId: string;
  planType: number;
  adjustmentReason: string;
  afterContent: string;
}

/** 调整记录查询参数 */
export interface AdjustmentRecordQueryParam {
  pageNo?: number;
  pageSize?: number;
  adjustmentType?: number;
  startDate?: string;
  endDate?: string;
}

// ==================== AI调整相关 ====================

/** AI单条调整参数 */
export interface AiAdjustParam {
  userPlanId: string;
  planType: number;
  adjustmentRequest: string;
}

/** AI调整结果 */
export interface AiAdjustResult {
  beforeContent: string;
  afterContent: string;
  adjustmentId: string;
}

/** AI批量调整参数 */
export interface BatchAiAdjustParam {
  userPlanId: string;
  adjustmentRequest: string;
}

/** 批量调整子方案项 */
export interface SubPlanAdjustItem {
  planType: number;
  planTypeName: string;
  beforeContent: string;
  afterContent: string;
  adjustmentId: string;
}

/** AI批量调整结果 */
export interface BatchAiAdjustResult {
  adjustments: SubPlanAdjustItem[];
}

// ==================== 广场相关 ====================

/** 广场列表项 */
export interface PlanSquareItem {
  id: string;
  nickname: string;
  avatarUrl: string;
  constitutionName: string;
  planTitle?: string;
  planSummary: string;
  planTags: string;
  completionRate: number;
  viewCount: number;
  likeCount: number;
  collectCount: number;
  commentCount: number;
  isOfficial: number;
  createTime: string;
}

/** 广场详情 */
export interface PlanSquareDetail extends PlanSquareItem {
  planType: number;
  adjustmentCount: number;
  isLiked: boolean;
  isCollected: boolean;
}

/** 广场查询参数 */
export interface PlanSquareQueryParam {
  pageNo?: number;
  pageSize?: number;
  keyword?: string;
  planType?: number;
  constitutionName?: string;
  sortBy?: number;
}

/** 广场操作参数 */
export interface PlanSquareActionParam {
  squareId: string;
  actionType: number;
}

/** 分享到广场参数 */
export interface PlanSquareShareParam {
  userPlanId: string;
  planSummary: string;
  planTags?: string;
}
