/**
 * 体质辨识类型定义
 * 对应后端 VO 和 Param
 */

/**
 * 体质类型
 */
export interface ConstitutionType {
  /** 体质编码 */
  code: string;
  /** 体质名称 */
  name: string;
  /** 英文名 */
  nameEn: string;
  /** 体质描述 */
  description: string;
  /** 体质特征 */
  characteristics: string;
}

/**
 * 体质问卷选项
 */
export interface ConstitutionOption {
  /** 选项ID */
  id: string;
  /** 选项编号 */
  optionNo: number;
  /** 选项内容 */
  optionText: string;
  /** 选项分值 */
  optionValue: number;
  /** 关联体质编码 */
  targetTypeCode: string;
}

/**
 * 体质问卷题目
 */
export interface ConstitutionQuestion {
  /** 题目ID */
  id: string;
  /** 题目编号 */
  questionNo: number;
  /** 题目内容 */
  questionText: string;
  /** 题目补充说明 */
  questionTextSecondary: string;
  /** 所属分类 */
  category: string;
  /** 答案类型：1-单选，2-多选 */
  answerType: number;
  /** 是否必答：0-否，1-是 */
  isRequired: number;
  /** 选项列表 */
  options: ConstitutionOption[];
}

/**
 * 体质测评结果
 */
export interface ConstitutionResult {
  /** 记录ID */
  recordId: string;
  /** 体质编码 */
  constitutionCode: string;
  /** 体质名称 */
  constitutionName: string;
  /** 体质得分 */
  score: number;
  /** 置信度 */
  confidence: number;
  /** 测评模式：1-简易模式，2-精细模式 */
  assessmentMode: number;
  /** 体质描述 */
  description: string;
  /** 体质特征 */
  characteristics: string;
  /** 健康建议 */
  healthAdvice: string;
  /** 饮食指导 */
  dietGuidance: string;
  /** 运动指导 */
  exerciseGuidance: string;
  /** 情志调节 */
  emotionGuidance: string;
  /** 穴位保健 */
  acupointGuidance: string;
  /** 所有体质得分 */
  allScores: Record<string, number>;
  /** 测评时间 */
  createTime: string;
}

/**
 * 体质测评历史记录
 */
export interface ConstitutionRecord {
  /** 记录ID */
  recordId: string;
  /** 体质编码 */
  constitutionCode: string;
  /** 体质名称 */
  constitutionName: string;
  /** 体质得分 */
  score: number;
  /** 置信度 */
  confidence: number;
  /** 测评模式：1-简易模式，2-精细模式 */
  assessmentMode: number;
  /** 兼夹体质JSON */
  mixedTypes: string;
  /** 测评时间 */
  createTime: string;
}

/**
 * 答案项
 */
export interface AnswerItem {
  /** 题目ID */
  questionId: string;
  /** 选项分值 */
  optionValue: number;
}

/**
 * 测评提交参数
 */
export interface AssessmentSubmitParam {
  /** 答案列表 */
  answers: AnswerItem[];
}

/**
 * 评估状态
 */
export interface AssessmentStatus {
  /** 评估状态：0-没有评估记录，1-有评估记录 */
  assessmentStatus: number;
}
