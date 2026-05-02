export interface AdConfigVO {
  configKey: string
  adType: number
  adUnitId: string
  placement: string
  displayIntervalSeconds: number
}

export interface AdActionParam {
  adConfigKey: string
  action: number
  duration?: number
}
