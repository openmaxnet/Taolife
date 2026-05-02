/// <reference path="../global.d.ts"/>

declare namespace Entity {
  /**
   * 广告配置管理端VO
   */
  interface AdConfigAdminVO {
    /** 配置ID */
    id?: string
    /** 配置唯一键 */
    configKey?: string
    /** 广告类型 */
    adType?: number
    /** 微信广告单元ID */
    adUnitId?: string
    /** 页面展示位置标识 */
    placement?: string
    /** 是否启用 */
    isEnabled?: number
    /** 仅展示给免费用户 */
    freeUserOnly?: number
    /** 展示间隔秒数 */
    displayIntervalSeconds?: number
    /** 额外配置JSON */
    extraConfig?: string
    /** 排序权重 */
    priority?: number
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  /**
   * 广告日志管理端VO
   */
  interface AdLogAdminVO {
    /** 日志ID */
    id?: string
    /** 账号ID */
    accountId?: string
    /** 广告配置ID */
    adConfigId?: string
    /** 广告类型 */
    adType?: number
    /** 动作 */
    action?: number
    /** 观看时长 */
    duration?: number
    /** 创建时间 */
    createTime?: string
  }

  /**
   * 广告配置保存参数
   */
  interface AdConfigSaveParam {
    /** 配置ID，为空表示新增 */
    id?: string
    /** 配置Key */
    configKey?: string
    /** 广告类型 */
    adType?: number
    /** 微信广告单元ID */
    adUnitId?: string
    /** 展示位置 */
    placement?: string
    /** 是否启用 */
    isEnabled?: number
    /** 仅展示给免费用户 */
    freeUserOnly?: number
    /** 展示间隔秒数 */
    displayIntervalSeconds?: number
    /** 额外配置JSON */
    extraConfig?: string
    /** 排序权重 */
    priority?: number
  }

  /**
   * 积分商品管理端VO
   */
  interface PointsGoodsAdminVO {
    /** 商品ID */
    id?: string
    /** 商品编码 */
    goodsCode?: string
    /** 商品名称 */
    goodsName?: string
    /** 商品类型 */
    goodsType?: number
    /** 兑换值 */
    value?: number
    /** 所需积分 */
    pointsRequired?: number
    /** 每日限制次数 */
    dailyLimit?: number
    /** 总限制次数 */
    totalLimit?: number
    /** 图标URL */
    iconUrl?: string
    /** 描述 */
    description?: string
    /** 排序权重 */
    sortOrder?: number
    /** 是否启用 */
    isEnabled?: number
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  /**
   * 积分兑换记录管理端VO
   */
  interface PointsExchangeAdminVO {
    /** 记录ID */
    id?: string
    /** 账号ID */
    accountId?: string
    /** 积分商品ID */
    goodsId?: string
    /** 消耗积分 */
    pointsCost?: number
    /** 兑换值 */
    exchangeValue?: number
    /** 关联业务ID */
    businessId?: string
    /** 状态 */
    status?: number
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  /**
   * 积分商品保存参数
   */
  interface PointsGoodsSaveParam {
    /** 商品ID，为空表示新增 */
    id?: string
    /** 商品编码 */
    goodsCode?: string
    /** 商品名称 */
    goodsName?: string
    /** 商品类型 */
    goodsType?: number
    /** 兑换值 */
    value?: number
    /** 所需积分 */
    pointsRequired?: number
    /** 每日限制次数 */
    dailyLimit?: number
    /** 总限制次数 */
    totalLimit?: number
    /** 图标URL */
    iconUrl?: string
    /** 描述 */
    description?: string
    /** 排序权重 */
    sortOrder?: number
    /** 是否启用 */
    isEnabled?: number
  }

  /**
   * 会员套餐管理端VO
   */
  interface MemberPlanVO {
    /** 套餐ID */
    id?: string
    /** 套餐代码 */
    planCode?: string
    /** 套餐名称 */
    planName?: string
    /** 会员等级 */
    memberLevel?: number
    /** 会员时长（天） */
    durationDays?: number
    /** 原价（分） */
    originalPrice?: number
    /** 现价（分） */
    currentPrice?: number
    /** 折扣标签 */
    discountLabel?: string
    /** AI每日配额 */
    aiDailyQuota?: number
    /** 最大活跃方案数 */
    maxActivePlans?: number
    /** 最大方案周期（天） */
    maxCycleDays?: number
    /** 最大调整次数 */
    maxAdjustments?: number
    /** 每月体质评估配额 */
    assessmentMonthlyQuota?: number
    /** 积分获取倍率 */
    pointsMultiplier?: number
    /** 商城折扣 */
    storeDiscount?: number
    /** 积分抵现比例 */
    pointsToYuanRatio?: number
    /** 权益详情JSON */
    benefitsJson?: string
    /** 排序 */
    sortOrder?: number
    /** 是否启用 */
    isEnabled?: number
    /** 开通/续费奖励成长值 */
    subGrowthBonus?: number
    /** 创建时间 */
    createTime?: string
  }

  /**
   * 积分记录
   */
  interface PointsRecord {
    id?: string
    accountId?: string
    pointsChange?: number
    pointsType?: number
    businessType?: string
    businessId?: string
    remark?: string
    balanceAfter?: number
    createTime?: string
  }

  /**
   * 签到记录
   */
  interface CheckinRecord {
    id?: string
    accountId?: string
    checkinDate?: string
    consecutiveCheckinDays?: number
    totalCheckinDays?: number
    pointsEarned?: number
    remark?: string
    createTime?: string
  }

  /**
   * 积分规则
   */
  interface PointsRule {
    id?: string
    ruleCode?: string
    ruleName?: string
    pointsType?: number
    points?: number
    conditionType?: number
    conditionValue?: number
    dailyLimit?: number
    isEnabled?: 0 | 1
    sortOrder?: number
    remark?: string
    createTime?: string
    updateTime?: string
  }

  interface PointsRuleSaveParam {
    ruleCode?: string
    ruleName?: string
    pointsType?: number
    points?: number
    conditionType?: number
    conditionValue?: number
    dailyLimit?: number
    isEnabled?: 0 | 1
    sortOrder?: number
    remark?: string
  }

  /**
   * 会员套餐保存参数
   */
  interface MemberPlanSaveParam {
    /** 套餐ID，为空表示新增 */
    id?: string
    /** 套餐代码 */
    planCode?: string
    /** 套餐名称 */
    planName?: string
    /** 会员等级 */
    memberLevel?: number
    /** 会员时长（天） */
    durationDays?: number
    /** 原价（分） */
    originalPrice?: number
    /** 现价（分） */
    currentPrice?: number
    /** 折扣标签 */
    discountLabel?: string
    /** AI每日配额 */
    aiDailyQuota?: number
    /** 最大活跃方案数 */
    maxActivePlans?: number
    /** 最大方案周期（天） */
    maxCycleDays?: number
    /** 最大调整次数 */
    maxAdjustments?: number
    /** 每月体质评估配额 */
    assessmentMonthlyQuota?: number
    /** 积分获取倍率 */
    pointsMultiplier?: number
    /** 商城折扣 */
    storeDiscount?: number
    /** 积分抵现比例 */
    pointsToYuanRatio?: number
    /** 权益详情JSON */
    benefitsJson?: string
    /** 开通/续费奖励成长值 */
    subGrowthBonus?: number
    /** 排序 */
    sortOrder?: number
    /** 是否启用 */
    isEnabled?: number
  }

  /**
   * 会员成长记录管理端VO
   */
  interface MemberGrowthRecordAdminVO {
    id?: string
    accountId?: string
    growthChange?: number
    growthSource?: number
    businessType?: string
    businessId?: string
    remark?: string
    growthValueAfter?: number
    createTime?: string
  }

  /**
   * 成长等级保存参数
   */
  interface GrowthLevelSaveParam {
    /** 等级ID，为空表示新增 */
    id?: string
    /** 等级序号 */
    level?: number
    /** 等级名称 */
    levelName?: string
    /** 最低成长值 */
    minGrowthValue?: number
    /** AI额外配额 */
    bonusAiQuota?: number
    /** 积分倍率 */
    bonusPointsMultiplier?: number
    /** 商城折扣 */
    bonusStoreDiscount?: number
    /** 权益说明 */
    privilege?: string
    /** 是否启用 */
    isEnabled?: number
    /** 排序 */
    sortOrder?: number
  }

  /**
   * 成长等级管理端VO
   */
  interface GrowthLevelAdminVO {
    id?: string
    level?: number
    levelName?: string
    minGrowthValue?: number
    bonusAiQuota?: number
    bonusPointsMultiplier?: number
    bonusStoreDiscount?: number
    privilege?: string
    isEnabled?: number
    sortOrder?: number
    createTime?: string
    updateTime?: string
  }
}
