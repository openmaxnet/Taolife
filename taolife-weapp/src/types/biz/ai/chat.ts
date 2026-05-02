/**
 * AI聊天类型定义
 * 对应后端 VO 和 Param
 */

/**
 * 会话信息
 */
export interface Session {
  /** 会话ID */
  sessionId: string;
  /** 会话标题 */
  sessionTitle: string;
  /** 创建时间 */
  createTime: string;
}

/**
 * 消息类型
 */
export interface Message {
  /** 消息ID */
  messageId: string;
  /** 会话ID */
  sessionId: string;
  /** 角色：user、assistant、system、tool（与智谱API保持一致） */
  role: string;
  /** 消息内容 */
  content: string;
  /** 思考过程内容（仅assistant消息） */
  reasoningContent?: string;
  /** 消息状态：1-完成，2-LLM失败，3-安全拒绝，4-用户中断 */
  status?: number;
  /** 创建时间 */
  createTime: string;
}

/**
 * 创建会话参数
 */
export interface CreateSessionParam {
  /** 会话标题 */
  title: string;
}

/**
 * 发送消息参数
 */
export interface SendMessageParam {
  /** 会话ID */
  sessionId: string;
  /** 消息内容 */
  content: string;
  /** 是否开启流式输出 */
  stream?: boolean;
  /** 是否启用思考模式 */
  enableThinking?: boolean;
}

/**
 * 重新生成消息参数
 */
export interface RegenerateMessageParam {
  /** 会话ID */
  sessionId: string;
  /** 要重新生成的AI消息ID */
  messageId: string;
  /** 是否启用思考模式 */
  enableThinking?: boolean;
}

/**
 * 会话分页参数
 */
export interface SessionPageParam {
  /** 会话ID */
  sessionId?: string;
  /** 页码 */
  pageNo?: number;
  /** 每页数量 */
  pageSize?: number;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  /** 数据列表 */
  list: T[];
  /** 总记录数 */
  totalRow: number;
  /** 当前页码 */
  pageNo: number;
  /** 每页数量 */
  pageSize: number;
  /** 总页数 */
  totalPage: number;
}
