import { request } from '../http'

// ==================== 会话管理 ====================

export function getSessionPage(params: Entity.SessionPageAdminParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.SessionAdminVO>>>('/api/ai/admin/chat/getSessionPage', { params })
}

export function getSessionDetail(sessionId: string) {
  return request.Get<Service.ResponseResult<Entity.SessionDetailAdminVO>>('/api/ai/admin/chat/getSessionDetail', { params: { sessionId } })
}

export function removeSession(sessionId: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/chat/removeSession', undefined, { params: { sessionId } })
}

// ==================== 敏感词管理 ====================

export function getSensitiveWordPage(params: Entity.SensitiveWordPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.SensitiveWordVO>>>('/api/ai/admin/sensitive/getSensitiveWordPage', { params })
}

export function getSensitiveWordDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.SensitiveWordVO>>('/api/ai/admin/sensitive/getSensitiveWordDetail', { params: { id } })
}

export function createSensitiveWord(data: Entity.SensitiveWordSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/sensitive/createSensitiveWord', data)
}

export function modifySensitiveWordInfo(data: Entity.SensitiveWordSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/sensitive/modifySensitiveWordInfo', data)
}

export function removeSensitiveWord(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/sensitive/removeSensitiveWord', undefined, { params: { id } })
}

export function modifySensitiveWordStatus(id: string, isEnabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/sensitive/modifySensitiveWordStatus', undefined, { params: { id, isEnabled } })
}

export function importSensitiveWords(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.Post<Service.ResponseResult<Entity.ImportResultVO>>('/api/ai/admin/sensitive/importSensitiveWords', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 分类规则管理 ====================

export function getQuestionRulePage(params: Entity.QuestionRulePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.QuestionRuleVO>>>('/api/ai/admin/rule/getQuestionRulePage', { params })
}

export function getQuestionRuleDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.QuestionRuleVO>>('/api/ai/admin/rule/getQuestionRuleDetail', { params: { id } })
}

export function createQuestionRule(data: Entity.QuestionRuleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/rule/createQuestionRule', data)
}

export function modifyQuestionRuleInfo(data: Entity.QuestionRuleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/rule/modifyQuestionRuleInfo', data)
}

export function removeQuestionRule(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/rule/removeQuestionRule', undefined, { params: { id } })
}

export function modifyQuestionRuleStatus(id: string, isEnabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/rule/modifyQuestionRuleStatus', undefined, { params: { id, isEnabled } })
}

export function importQuestionRules(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.Post<Service.ResponseResult<Entity.ImportResultVO>>('/api/ai/admin/rule/importQuestionRules', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 文档管理 ====================

export function getDocumentPage(params: Entity.KnowledgePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.KnowledgeVO>>>('/api/ai/admin/doc/getDocumentPage', { params })
}

export function getDocumentDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.KnowledgeVO>>('/api/ai/admin/doc/getDocumentDetail', { params: { id } })
}

export function createDocument(data: Entity.KnowledgeSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/doc/createDocument', data)
}

export function modifyDocumentInfo(data: Entity.KnowledgeSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/doc/modifyDocumentInfo', data)
}

export function removeDocument(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/doc/removeDocument', undefined, { params: { id } })
}

// ==================== AI模型厂商管理 ====================

export function getAiProviderPage(params: Entity.AiProviderPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AiProviderVO>>>('/api/ai/admin/provider/getProviderPage', { params })
}

export function getAiProviderDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AiProviderVO>>('/api/ai/admin/provider/getProviderDetail', { params: { id } })
}

export function createAiProvider(data: Entity.AiProviderSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/provider/createProvider', data)
}

export function modifyAiProviderInfo(data: Entity.AiProviderSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/provider/modifyProviderInfo', data)
}

export function removeAiProvider(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/provider/removeProvider', { id })
}

export function modifyAiProviderStatus(id: string, status: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/provider/modifyProviderStatus', { id, status })
}

export function getEnabledAiProviderList(providerType?: string) {
  return request.Get<Service.ResponseResult<Entity.AiProviderVO[]>>('/api/ai/admin/provider/getEnabledProviderList', { params: { providerType } })
}

// ==================== AI模型管理 ====================

export function getAiModelPage(params: Entity.AiModelPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AiModelVO>>>('/api/ai/admin/model/getModelPage', { params })
}

export function getAiModelDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AiModelVO>>('/api/ai/admin/model/getModelDetail', { params: { id } })
}

export function createAiModel(data: Entity.AiModelSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/model/createModel', data)
}

export function modifyAiModelInfo(data: Entity.AiModelSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/model/modifyModelInfo', data)
}

export function removeAiModel(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/model/removeModel', { id })
}

export function modifyAiModelStatus(id: string, status: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/model/modifyModelStatus', { id, status })
}

export function getAiModelByCode(modelCode: string) {
  return request.Get<Service.ResponseResult<Entity.AiModelVO>>('/api/ai/admin/model/getModelByCode', { params: { modelCode } })
}

export function getAiModelListByProviderId(providerId: string) {
  return request.Get<Service.ResponseResult<Entity.AiModelVO[]>>('/api/ai/admin/model/getModelListByProviderId', { params: { providerId } })
}

// ==================== AI提示词模板管理 ====================

export function getAiPromptTemplatePage(params: Entity.AiPromptTemplatePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AiPromptTemplateVO>>>('/api/ai/admin/prompt/getTemplatePage', { params })
}

export function getAiPromptTemplateDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AiPromptTemplateVO>>('/api/ai/admin/prompt/getTemplateDetail', { params: { id } })
}

export function createAiPromptTemplate(data: Entity.AiPromptTemplateSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/prompt/createTemplate', data)
}

export function modifyAiPromptTemplateInfo(data: Entity.AiPromptTemplateSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/prompt/modifyTemplateInfo', data)
}

export function removeAiPromptTemplate(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/prompt/removeTemplate', { id })
}

export function modifyAiPromptTemplateStatus(id: string, status: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/prompt/modifyTemplateStatus', { id, status })
}

export function getAiPromptTemplateByCode(templateCode: string) {
  return request.Get<Service.ResponseResult<Entity.AiPromptTemplateVO>>('/api/ai/admin/prompt/getTemplateByCode', { params: { templateCode } })
}

export function renderAiPromptTemplate(templateCode: string, variables: Record<string, any>) {
  return request.Post<Service.ResponseResult<string>>('/api/ai/admin/prompt/render', variables, { params: { templateCode } })
}

// ==================== AI场景配置管理 ====================

export function getAiSceneConfigPage(params: Entity.AiSceneConfigPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AiSceneConfigVO>>>('/api/ai/admin/scene/getScenePage', { params })
}

export function getAiSceneConfigDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AiSceneConfigVO>>('/api/ai/admin/scene/getSceneDetail', { params: { id } })
}

export function createAiSceneConfig(data: Entity.AiSceneConfigSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/scene/createScene', data)
}

export function modifyAiSceneConfigInfo(data: Entity.AiSceneConfigSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/scene/modifySceneInfo', data)
}

export function removeAiSceneConfig(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/scene/removeScene', { id })
}

export function modifyAiSceneConfigStatus(id: string, status: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/scene/modifySceneStatus', { id, status })
}

export function getAiSceneConfigByCode(sceneCode: string) {
  return request.Get<Service.ResponseResult<Entity.AiSceneConfigVO>>('/api/ai/admin/scene/getSceneByCode', { params: { sceneCode } })
}

export function getEnabledAiSceneConfigList() {
  return request.Get<Service.ResponseResult<Entity.AiSceneConfigVO[]>>('/api/ai/admin/scene/getEnabledSceneList')
}

// ==================== AI厂商端点配置管理 ====================

export function getAiProviderEndpointPage(params: Entity.AiProviderEndpointPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AiProviderEndpointVO>>>('/api/ai/admin/endpoint/getEndpointPage', { params })
}

export function getAiProviderEndpointDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AiProviderEndpointVO>>('/api/ai/admin/endpoint/getEndpointDetail', { params: { id } })
}

export function createAiProviderEndpoint(data: Entity.AiProviderEndpointSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/endpoint/createEndpoint', data)
}

export function modifyAiProviderEndpointInfo(data: Entity.AiProviderEndpointSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/endpoint/modifyEndpointInfo', data)
}

export function removeAiProviderEndpoint(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/endpoint/removeEndpoint', { id })
}

export function modifyAiProviderEndpointStatus(id: string, status: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/ai/admin/endpoint/modifyEndpointStatus', { id, status })
}

export function getAiProviderEndpointListByProvider(providerId: string) {
  return request.Get<Service.ResponseResult<Entity.AiProviderEndpointVO[]>>('/api/ai/admin/endpoint/getEndpointListByProviderId', { params: { providerId } })
}

export function getAiProviderEndpointByType(providerId: string, endpointType: string) {
  return request.Get<Service.ResponseResult<Entity.AiProviderEndpointVO>>('/api/ai/admin/endpoint/getEndpointByProviderIdAndType', { params: { providerId, endpointType } })
}