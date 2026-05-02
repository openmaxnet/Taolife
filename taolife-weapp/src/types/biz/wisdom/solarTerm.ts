/** 节气列表项 */
export interface SolarTermListItem {
  id: string
  termName: string
  termOrder?: number
  dateRange?: string
  season?: number
  seasonName?: string
  coverUrl?: string
  description?: string
  isCurrent?: boolean
}

/** 节气详情 */
export interface SolarTermDetail {
  id: string
  termName: string
  termOrder?: number
  dateRange?: string
  season?: number
  seasonName?: string
  coverUrl?: string
  backgroundUrl?: string
  description?: string
  introduction?: string
  climate?: string
  healthPrinciples?: string
  customs?: string
  proverbs?: string
  dietSummary?: string
  isCurrent?: boolean
}
