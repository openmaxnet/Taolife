import { request } from '../http'

// ==================== 食材管理 ====================

export function getFoodPage(params: Entity.FoodPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.Food>>>('/api/wisdom/admin/food/getFoodPage', { params })
}

export function getFoodDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Food>>('/api/wisdom/admin/food/getFoodDetail', { params: { id } })
}

export function createFood(data: Entity.FoodSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/food/createFood', data)
}

export function modifyFoodInfo(id: string, data: Entity.FoodSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/food/modifyFoodInfo', data, { params: { id } })
}

export function removeFood(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/food/removeFood', undefined, { params: { id } })
}

export function modifyFoodStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/food/modifyFoodStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 经络管理 ====================

export function getMeridianPage(params: Entity.MeridianPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.Meridian>>>('/api/wisdom/admin/meridian/getMeridianPage', { params })
}

export function getMeridianDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Meridian>>('/api/wisdom/admin/meridian/getMeridianDetail', { params: { id } })
}

export function createMeridian(data: Entity.MeridianSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/meridian/createMeridian', data)
}

export function modifyMeridianInfo(id: string, data: Entity.MeridianSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/meridian/modifyMeridianInfo', data, { params: { id } })
}

export function removeMeridian(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/meridian/removeMeridian', undefined, { params: { id } })
}

export function modifyMeridianStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/meridian/modifyMeridianStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 穴位管理 ====================

export function getAcupointPage(params: Entity.AcupointPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.Acupoint>>>('/api/wisdom/admin/acupoint/getAcupointPage', { params })
}

export function getAcupointDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Acupoint>>('/api/wisdom/admin/acupoint/getAcupointDetail', { params: { id } })
}

export function createAcupoint(data: Entity.AcupointSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupoint/createAcupoint', data)
}

export function modifyAcupointInfo(id: string, data: Entity.AcupointSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupoint/modifyAcupointInfo', data, { params: { id } })
}

export function removeAcupoint(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupoint/removeAcupoint', undefined, { params: { id } })
}

export function modifyAcupointStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupoint/modifyAcupointStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 文章管理 ====================

export function getArticlePage(params: Entity.ArticlePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.Article>>>('/api/wisdom/admin/article/getArticlePage', { params })
}

export function getArticleDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Article>>('/api/wisdom/admin/article/getArticleDetail', { params: { id } })
}

export function createArticle(data: Entity.ArticleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/article/createArticle', data)
}

export function modifyArticleInfo(id: string, data: Entity.ArticleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/article/modifyArticleInfo', data, { params: { id } })
}

export function removeArticle(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/article/removeArticle', undefined, { params: { id } })
}

export function modifyArticleStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/article/modifyArticleStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 运动管理 ====================

export function getExercisePage(params: Entity.ExercisePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.Exercise>>>('/api/wisdom/admin/exercise/getExercisePage', { params })
}

export function getExerciseDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Exercise>>('/api/wisdom/admin/exercise/getExerciseDetail', { params: { id } })
}

export function createExercise(data: Entity.ExerciseSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/exercise/createExercise', data)
}

export function modifyExerciseInfo(id: string, data: Entity.ExerciseSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/exercise/modifyExerciseInfo', data, { params: { id } })
}

export function removeExercise(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/exercise/removeExercise', undefined, { params: { id } })
}

export function modifyExerciseStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/exercise/modifyExerciseStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 穴位组合管理 ====================

export function getAcupointCombinePage(params: Entity.AcupointCombinePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AcupointCombine>>>('/api/wisdom/admin/acupointCombine/getAcupointCombinePage', { params })
}

export function getAcupointCombineDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AcupointCombine>>('/api/wisdom/admin/acupointCombine/getAcupointCombineDetail', { params: { id } })
}

export function createAcupointCombine(data: Entity.AcupointCombineSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupointCombine/createAcupointCombine', data)
}

export function modifyAcupointCombineInfo(id: string, data: Entity.AcupointCombineSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupointCombine/modifyAcupointCombineInfo', data, { params: { id } })
}

export function removeAcupointCombine(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupointCombine/removeAcupointCombine', undefined, { params: { id } })
}

export function modifyAcupointCombineStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/acupointCombine/modifyAcupointCombineStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 体质类型管理 ====================

export function getConstitutionTypePage(params: Entity.ConstitutionTypePageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.ConstitutionTypeVO>>>('/api/wisdom/admin/constitution/getConstitutionTypePage', { params })
}

export function getConstitutionTypeDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.ConstitutionTypeVO>>('/api/wisdom/admin/constitution/getConstitutionTypeDetail', { params: { id } })
}

export function createConstitutionType(data: Entity.ConstitutionTypeSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/createConstitutionType', data)
}

export function modifyConstitutionTypeInfo(data: Entity.ConstitutionTypeSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/modifyConstitutionTypeInfo', data)
}

export function removeConstitutionType(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/removeConstitutionType', undefined, { params: { id } })
}

export function modifyConstitutionTypeStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/modifyConstitutionTypeStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 体质问题管理 ====================

export function getQuestionPage(params: Entity.QuestionPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.QuestionVO>>>('/api/wisdom/admin/constitution/question/getQuestionPage', { params })
}

export function getQuestionDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.QuestionDetailVO>>('/api/wisdom/admin/constitution/question/getQuestionDetail', { params: { id } })
}

export function createQuestion(data: Entity.QuestionSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/createQuestion', data)
}

export function modifyQuestionInfo(data: Entity.QuestionSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/modifyQuestionInfo', data)
}

export function removeQuestion(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/removeQuestion', undefined, { params: { id } })
}

export function modifyQuestionStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/modifyQuestionStatus', undefined, { params: { id, isDisabled } })
}

export function createOption(data: Entity.OptionSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/createOption', data)
}

export function modifyOptionInfo(data: Entity.OptionSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/modifyOptionInfo', data)
}

export function removeOption(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/constitution/question/removeOption', undefined, { params: { id } })
}

// ==================== 节气管理 ====================

export function getSolarTermPage(params: Entity.SolarTermPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.SolarTermVO>>>('/api/wisdom/admin/solarterm/getSolarTermPage', { params })
}

export function getSolarTermDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.SolarTermVO>>('/api/wisdom/admin/solarterm/getSolarTermDetail', { params: { id } })
}

export function createSolarTerm(data: Entity.SolarTermSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/solarterm/createSolarTerm', data)
}

export function modifySolarTermInfo(data: Entity.SolarTermSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/solarterm/modifySolarTermInfo', data)
}

export function removeSolarTerm(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/wisdom/admin/solarterm/removeSolarTerm', undefined, { params: { id } })
}

