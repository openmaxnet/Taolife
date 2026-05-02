/**
 * 用户相关类型定义
 * 与后端用户数据结构对应
 */

/**
 * 用户基本信息
 * 与后端 WxLoginVO.UserBasicInfoVO 对应
 */
export interface UserBasicInfo {
  /** 账号ID */
  accountId: string;
  /** 昵称 */
  nickname: string;
  /** 头像URL */
  avatarUrl: string;
  /** 性别：0-未知，1-男，2-女 */
  gender: number;
  /** 会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员 */
  memberLevel: number;
}

/**
 * 微信登录返回
 * 与后端 WxLoginVO 对应
 */
export interface WxLoginResult {
  /** 访问令牌 */
  accessToken: string;
  /** 刷新令牌 */
  refreshToken: string;
  /** 用户基本信息 */
  userInfo: UserBasicInfo;
  /** 是否新用户 */
  isNewUser: boolean;
}

/**
 * 微信登录参数
 * 与后端 WxLoginParam 对应
 */
export interface WxLoginParam {
  /** 微信登录code */
  code: string;
  /** 手机号获取凭证（暂时注释）
  phoneCode?: string;
   */
  /** 用户昵称（可选） */
  nickname?: string;
  /** 用户头像（可选） */
  avatarUrl?: string;
  /** 性别：0-未知，1-男，2-女（可选） */
  gender?: number;
  /** 邀请码（可选） */
  inviteCode?: string;
}

/**
 * 用户信息VO
 * 与后端 UserInfoVO 对应
 */
export interface UserInfoVO {
  /** 账号ID */
  accountId: string;
  /** 昵称 */
  nickname: string;
  /** 头像URL */
  avatarUrl: string;
  /** 手机号 */
  phone: string;
  /** 性别：0-未知，1-男，2-女 */
  gender: number;
  /** 生日 */
  birthday: string;
  /** 会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员 */
  memberLevel: number;
  /** 会员过期时间 */
  memberExpireTime: string;
  /** 总积分 */
  totalPoints: number;
  /** 可用积分 */
  availablePoints: number;
  /** 创建时间 */
  createTime: string;
  /** 点赞数 */
  likeCount?: number;
  /** 收藏数 */
  collectCount?: number;
  /** 关注数 */
  followingCount?: number;
}

/**
 * 用户信息修改参数
 * 与后端 UserInfoParam 对应
 */
export interface UserInfoParam {
  /** 昵称（可选） */
  nickname?: string;
  /** 头像URL（可选） */
  avatarUrl?: string;
  /** 性别：0-未知，1-男，2-女（可选） */
  gender?: number;
  /** 生日（可选） */
  birthday?: string;
}
