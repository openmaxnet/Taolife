/// <reference path="../global.d.ts"/>

namespace Entity {
  // ==================== 会话管理 ====================
  interface SessionPageAdminParam {
    pageNo?: number
    pageSize?: number
    accountId?: string
    keyword?: string
  }

  interface SessionAdminVO {
    sessionId: string
    accountId: string
    sessionTitle: string
    constitutionId?: string
    constitutionName?: string
    messageCount: number
    lastMessage?: string
    lastMessageTime?: string
    chatModel?: string
    embeddingModel?: string
    status: number
    createTime: string
  }

  interface MessageAdminVO {
    messageId: string
    sessionId: string
    accountId: string
    role: number
    roleName: string
    content: string
    questionType?: number
    questionTypeName?: string
    isSafe?: number
    riskReason?: string
    knowledgeRefs?: string
    modelUsed?: string
    tokensUsed?: number
    responseTime?: number
    createTime: string
  }

  interface SessionDetailAdminVO {
    session: SessionAdminVO
    messages: MessageAdminVO[]
  }

  // ==================== 敏感词管理 ====================
  interface SensitiveWordPageParam {
    pageNo?: number
    pageSize?: number
    wordType?: number
    keyword?: string
  }

  interface SensitiveWordVO {
    id: string
    word: string
    wordType: number
    wordTypeName: string
    severity: number
    severityName: string
    actionType: number
    actionTypeName: string
    replaceWord?: string
    isEnabled: number
    createTime: string
  }

  interface SensitiveWordSaveParam {
    id?: string
    word: string
    wordType: number
    severity: number
    actionType: number
    replaceWord?: string
    isEnabled?: number
  }

  // ==================== 分类规则管理 ====================
  interface QuestionRulePageParam {
    pageNo?: number
    pageSize?: number
    keyword?: string
  }

  interface QuestionRuleVO {
    id: string
    categoryCode: string
    categoryName: string
    keywords?: string[]
    keywordsStr?: string
    priority: number
    isEnabled: number
    createTime: string
  }

  interface QuestionRuleSaveParam {
    id?: string
    categoryCode: string
    categoryName: string
    keywords: string
    priority?: number
    isEnabled?: number
  }

  // ==================== 体质类型管理 ====================
  interface ConstitutionTypePageParam {
    pageNo?: number
    pageSize?: number
  }

  interface ConstitutionTypeVO {
    id: string
    code: string
    name: string
    nameEn?: string
    description?: string
    characteristics?: string
    formationReason?: string
    healthAdvice?: string
    dietGuidance?: string
    exerciseGuidance?: string
    emotionGuidance?: string
    acupointGuidance?: string
    sortOrder?: number
    isDisabled?: number
    createTime: string
  }

  interface ConstitutionTypeSaveParam {
    id?: string
    code: string
    name: string
    nameEn?: string
    description?: string
    characteristics?: string
    formationReason?: string
    healthAdvice?: string
    dietGuidance?: string
    exerciseGuidance?: string
    emotionGuidance?: string
    acupointGuidance?: string
    sortOrder?: number
    isDisabled?: number
  }

  // ==================== 体质问题管理 ====================
  interface QuestionPageParam {
    pageNo?: number
    pageSize?: number
    questionMode?: number
  }

  interface QuestionVO {
    id: string
    questionNo?: number
    questionText: string
    questionTextSecondary?: string
    category?: string
    dimension?: string
    answerType?: number
    isRequired?: number
    questionMode?: number
    weight?: number
    reverseScore?: number
    isConsistencyCheck?: number
    sortOrder?: number
    isDisabled?: number
    optionCount?: number
    createTime: string
  }

  interface OptionVO {
    id: string
    questionId: string
    optionNo?: number
    optionText: string
    optionValue?: number
    targetTypeCode?: string
    targetTypeName?: string
    reverseValue?: number
    sortOrder?: number
    createTime: string
  }

  interface QuestionDetailVO {
    question: QuestionVO
    options: OptionVO[]
  }

  interface QuestionSaveParam {
    id?: string
    questionNo?: number
    questionText: string
    questionTextSecondary?: string
    category?: string
    dimension?: string
    answerType?: number
    isRequired?: number
    questionMode?: number
    weight?: number
    reverseScore?: number
    isConsistencyCheck?: number
    sortOrder?: number
    isDisabled?: number
  }

  interface OptionSaveParam {
    id?: string
    questionId: string
    optionNo?: number
    optionText: string
    optionValue?: number
    targetTypeCode?: string
    reverseValue?: number
    sortOrder?: number
  }

  // ==================== 节气管理 ====================
  interface SolarTermPageParam {
    pageNo?: number
    pageSize?: number
  }

  interface SolarTermVO {
    id: string
    termName: string
    termOrder?: number
    startMonth?: number
    startDay?: number
    endMonth?: number
    endDay?: number
    description?: string
    season?: number
    coverUrl?: string
    backgroundUrl?: string
    introduction?: string
    climate?: string
    healthPrinciples?: string
    customs?: string
    proverbs?: string
    dietSummary?: string
    createTime?: string
    updateTime?: string
  }

  interface SolarTermSaveParam {
    id?: string
    termName: string
    termOrder?: number
    startMonth?: number
    startDay?: number
    endMonth?: number
    endDay?: number
    description?: string
    season?: number
    coverUrl?: string
    backgroundUrl?: string
    introduction?: string
    climate?: string
    healthPrinciples?: string
    customs?: string
    proverbs?: string
    dietSummary?: string
  }

  // ==================== 轮播管理 ====================
  interface BannerPageParam {
    pageNo?: number
    pageSize?: number
    status?: number
  }

  interface BannerVO {
    id: string
    title: string
    subtitle?: string
    imageUrl?: string
    linkType?: number
    linkUrl?: string
    sortOrder?: number
    status?: number
    startDate?: string
    endDate?: string
    createTime?: string
    updateTime?: string
  }

  interface BannerSaveParam {
    id?: string
    title: string
    subtitle?: string
    imageUrl?: string
    linkType?: number
    linkUrl?: string
    sortOrder?: number
    status?: number
    startDate?: string
    endDate?: string
  }

  // ==================== 文档管理 ====================
  interface KnowledgePageParam {
    pageNo?: number
    pageSize?: number
    category?: string
    keyword?: string
  }

  interface KnowledgeVO {
    id: string
    docId?: string
    title: string
    content?: string
    category?: string
    categoryName?: string
    tags?: string[]
    source?: string
    constitutionType?: string
    season?: string
    createdAt?: string
  }

  interface KnowledgeSaveParam {
    id?: string
    title: string
    content?: string
    category?: string
    tags?: string[]
    source?: string
    constitutionType?: string
    season?: string
  }

  // ==================== 批量导入结果 ====================
  interface ImportResultVO {
    totalCount: number
    successCount: number
    failCount: number
    failList: string[]
  }

  // ==================== AI模型厂商管理 ====================
  interface AiProviderPageParam {
    pageNo?: number
    pageSize?: number
    providerType?: string
    keyword?: string
  }

  interface AiProviderVO {
    id: string
    providerCode: string
    providerName: string
    providerType: string
    apiEndpoint: string
    apiKeyMasked?: string
    isEncrypted: number
    isDefault: number
    priority: number
    status: number
    description?: string
    configJson?: string
    createTime: string
    updateTime: string
  }

  interface AiProviderSaveParam {
    id?: string
    providerCode: string
    providerName: string
    providerType: string
    apiEndpoint: string
    apiKey?: string
    isEncrypted?: number
    isDefault?: number
    priority?: number
    status?: number
    description?: string
    configJson?: string
  }

  // ==================== AI模型管理 ====================
  interface AiModelPageParam {
    pageNo?: number
    pageSize?: number
    providerId?: string
    modelType?: string
    keyword?: string
  }

  interface AiModelVO {
    id: string
    providerId: string
    providerName?: string
    modelCode: string
    modelName: string
    modelType: string
    temperature?: number
    maxTokens?: number
    topP?: number
    supportsThinking?: number
    supportsImage?: number
    maxConcurrency?: number
    extraParamsJson?: string
    capabilitiesJson?: string
    isDefault: number
    status: number
    createTime: string
    updateTime: string
  }

  interface AiModelSaveParam {
    id?: string
    providerId: string
    modelCode: string
    modelName: string
    modelType: string
    temperature?: number
    maxTokens?: number
    topP?: number
    supportsThinking?: number
    supportsImage?: number
    maxConcurrency?: number
    extraParamsJson?: string
    capabilitiesJson?: string
    isDefault?: number
    status?: number
  }

  // ==================== AI提示词模板管理 ====================
  interface AiPromptTemplatePageParam {
    pageNo?: number
    pageSize?: number
    templateType?: string
    keyword?: string
  }

  interface AiPromptTemplateVO {
    id: string
    templateCode: string
    templateName: string
    templateType: string
    templateContent?: string
    variablesJson?: string
    version: number
    isEnabled: number
    description?: string
    createTime: string
    updateTime: string
  }

  interface AiPromptTemplateSaveParam {
    id?: string
    templateCode: string
    templateName: string
    templateType: string
    templateContent: string
    variablesJson?: string
    version?: number
    isEnabled?: number
    description?: string
  }

  // ==================== AI场景配置管理 ====================
  interface AiSceneConfigPageParam {
    pageNo?: number
    pageSize?: number
    keyword?: string
  }

  interface AiSceneConfigVO {
    id: string
    sceneCode: string
    sceneName: string
    modelInstanceId: string
    modelName?: string
    modelCode?: string
    promptTemplateId: string
    promptTemplateName?: string
    parametersJson?: string
    extraConfigJson?: string
    isEnabled: number
    description?: string
    createTime: string
    updateTime: string
  }

  interface AiSceneConfigSaveParam {
    id?: string
    sceneCode: string
    sceneName: string
    modelInstanceId: string
    promptTemplateId: string
    parametersJson?: string
    extraConfigJson?: string
    isEnabled?: number
    description?: string
  }

  // ==================== AI厂商端点配置管理 ====================
  interface AiProviderEndpointPageParam {
    pageNo?: number
    pageSize?: number
    providerId?: string
    endpointType?: string
    keyword?: string
  }

  interface AiProviderEndpointVO {
    id: string
    providerId: string
    providerName?: string
    endpointType: string
    endpointTypeName: string
    endpointUri: string
    requestType: string
    timeoutMs?: number
    retryTimes?: number
    isEnabled: number
    description?: string
    configJson?: string
    createTime: string
    updateTime: string
  }

  interface AiProviderEndpointSaveParam {
    id?: string
    providerId: string
    endpointType: string
    endpointUri: string
    requestType: string
    timeoutMs?: number
    retryTimes?: number
    isEnabled?: number
    description?: string
    configJson?: string
  }
}
