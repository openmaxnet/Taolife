/** 今日签到状态 */
export interface TodayCheckinStatus {
  /** 是否可以签到 */
  canCheckin: boolean
  /** 连续签到天数 */
  consecutiveDays: number
  /** 累计签到天数 */
  totalDays: number
  /** 用户等级 */
  userLevel: number
  /** 获得积分 */
  points: number
  /** 是否升级 */
  levelUp: boolean
  /** 奖励列表 */
  rewards: any[]
}

/** 签到日历数据 */
export interface CheckinCalendar {
  /** 已签到日期列表 */
  checkedDays: number[]
  /** 连续签到天数 */
  consecutiveDays: number
  /** 累计签到天数 */
  totalDays: number
  /** 当月获得积分 */
  currentMonthPoints: number
}
