/*
 Navicat Premium Dump SQL

 Source Server         : dev-mysql
 Source Server Type    : MySQL
 Source Server Version : 80408 (8.4.8)
 Source Host           : 10.10.0.143:3306
 Source Schema         : taolife_dev

 Target Server Type    : MySQL
 Target Server Version : 80408 (8.4.8)
 File Encoding         : 65001

 Date: 02/05/2026 00:09:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tl_ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_chat_message`;
CREATE TABLE `tl_ai_chat_message`  (
  `id` binary(16) NOT NULL COMMENT '消息ID',
  `session_id` binary(16) NOT NULL COMMENT '会话ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `role` int NOT NULL COMMENT '角色：1-user，2-assistant，3-system',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `reasoning_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '思考过程内容',
  `question_type` int NULL DEFAULT NULL COMMENT '问题类型：1-体质类，2-食疗类，3-穴位类，4-养生类，5-其他',
  `is_safe` int NULL DEFAULT 1 COMMENT '是否安全：0-否，1-是',
  `risk_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险原因',
  `knowledge_refs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '引用的知识库文档ID（JSON数组）',
  `model_used` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '使用的模型',
  `tokens_used` int NULL DEFAULT NULL COMMENT '消耗的Token数',
  `response_time` int NULL DEFAULT NULL COMMENT '响应时间（毫秒）',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int NULL DEFAULT 1 COMMENT '消息状态：0-生成中，1-完成，2-LLM调用失败，3-安全检测拒绝，4-用户中断',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_session_create_time`(`session_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_account_message_type`(`account_id` ASC, `role` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问答消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_ai_chat_rule
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_chat_rule`;
CREATE TABLE `tl_ai_chat_rule`  (
  `id` binary(16) NOT NULL COMMENT '规则ID',
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `keywords` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关键词列表（JSON数组）',
  `priority` int NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_code`(`category_code` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_priority_enabled`(`priority` ASC, `is_enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问题规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_ai_chat_rule_keyword
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_chat_rule_keyword`;
CREATE TABLE `tl_ai_chat_rule_keyword`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `rule_id` binary(16) NOT NULL COMMENT '规则ID',
  `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关键词',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_keyword`(`rule_id` ASC, `keyword` ASC) USING BTREE,
  INDEX `idx_rule_id`(`rule_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问题规则关键词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_chat_session`;
CREATE TABLE `tl_ai_chat_session`  (
  `id` binary(16) NOT NULL COMMENT '会话ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `session_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话标题（自动生成）',
  `constitution_id` binary(16) NULL DEFAULT NULL COMMENT '关联的体质记录ID',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `last_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '最后一条消息',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后一条消息时间',
  `chat_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '聊天模型编码(来自tf_ai_model.model_code)',
  `embedding_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'qwen-embedding' COMMENT '向量模型：qwen-embedding/bge-m3',
  `status` int NULL DEFAULT 1 COMMENT '状态：0-已结束，1-进行中',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_message_status` int NULL DEFAULT 1 COMMENT '最后AI回复状态：1-完成，2-LLM失败，3-安全拒绝，4-用户中断',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_account_status`(`account_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_account_update_time`(`account_id` ASC, `update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问答会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_ai_model
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_model`;
CREATE TABLE `tl_ai_model`  (
  `id` binary(16) NOT NULL COMMENT '实例ID',
  `provider_id` binary(16) NOT NULL COMMENT '厂商ID',
  `model_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型编码：glm-4-flash/gpt-4/qwen-turbo',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型名称',
  `model_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型类型：chat/embedding',
  `parameters_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '默认参数JSON（如temperature,max_tokens等）',
  `temperature` double NULL DEFAULT NULL COMMENT '温度参数(0-2)',
  `max_tokens` int NULL DEFAULT NULL COMMENT '最大输出Token数',
  `top_p` double NULL DEFAULT NULL COMMENT 'Top-P参数',
  `supports_thinking` tinyint NULL DEFAULT 0 COMMENT '支持思考模式:0-否,1-是',
  `supports_image` tinyint NULL DEFAULT 0 COMMENT '支持图像输入:0-否,1-是',
  `max_concurrency` int NULL DEFAULT 0 COMMENT '模型全局最大并发数(0=不限)',
  `extra_params_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外参数JSON(厂商特定)',
  `capabilities_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模型能力JSON（如是否支持function call）',
  `is_default` int NULL DEFAULT 0 COMMENT '是否默认模型：0-否，1-是',
  `status` int NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_model_code`(`model_code` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_model_type`(`model_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI模型实例配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_ai_prompt_template
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_prompt_template`;
CREATE TABLE `tl_ai_prompt_template`  (
  `id` binary(16) NOT NULL COMMENT '模板ID',
  `template_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板编码：chat_system/constitution_assessment/health_advice',
  `template_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `template_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板类型：system_prompt/user_prompt/assistant_prompt',
  `template_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板内容（支持变量占位符 ${var}）',
  `variables_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变量定义JSON',
  `version` int NULL DEFAULT 1 COMMENT '版本号',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_code_version`(`template_code` ASC, `version` ASC) USING BTREE,
  INDEX `idx_template_type`(`template_type` ASC) USING BTREE,
  INDEX `idx_template_code`(`template_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI提示词模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_ai_provider
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_provider`;
CREATE TABLE `tl_ai_provider`  (
  `id` binary(16) NOT NULL COMMENT '厂商ID',
  `provider_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '厂商编码：glm/openai/claude/qwen',
  `provider_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '厂商名称：智谱AI/OpenAI/Claude/通义千问',
  `provider_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '厂商类型：chat/embedding/image',
  `api_endpoint` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API Endpoint',
  `api_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API Key（加密存储）',
  `is_encrypted` int NULL DEFAULT 1 COMMENT 'API Key是否加密：0-否，1-是',
  `is_default` int NULL DEFAULT 0 COMMENT '是否默认厂商：0-否，1-是',
  `priority` int NULL DEFAULT 0 COMMENT '优先级（负载均衡用，数字越大越优先）',
  `status` int NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '厂商特定配置JSON',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_provider_code_type`(`provider_code` ASC, `provider_type` ASC) USING BTREE,
  INDEX `idx_provider_type`(`provider_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI模型厂商配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_ai_provider_endpoint
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_provider_endpoint`;
CREATE TABLE `tl_ai_provider_endpoint`  (
  `id` binary(16) NOT NULL COMMENT '端点ID',
  `provider_id` binary(16) NOT NULL COMMENT '厂商ID',
  `endpoint_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '端点类型：model_api/tool_api/agent_api/file_api/batch_api/knowledge_api/realtime_api',
  `endpoint_uri` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '端点URI，如: /chat/completions',
  `request_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求类型：GET/POST/PUT/DELETE/WSS',
  `timeout_ms` int NULL DEFAULT 30000 COMMENT '超时时间(ms)',
  `retry_times` int NULL DEFAULT 3 COMMENT '重试次数',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外配置JSON',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_endpoint_type`(`endpoint_type` ASC) USING BTREE,
  CONSTRAINT `fk_endpoint_provider` FOREIGN KEY (`provider_id`) REFERENCES `tl_ai_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI厂商端点配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_ai_scene_config
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_scene_config`;
CREATE TABLE `tl_ai_scene_config`  (
  `id` binary(16) NOT NULL COMMENT '配置ID',
  `scene_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景编码：ai_chat/constitution_assessment/plan_generation',
  `scene_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景名称',
  `model_instance_id` binary(16) NOT NULL COMMENT '使用的模型实例ID',
  `prompt_template_id` binary(16) NOT NULL COMMENT '使用的提示词模板ID',
  `parameters_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '该场景覆盖的模型参数',
  `extra_config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外配置（如相似度阈值等）',
  `similarity_threshold` double NULL DEFAULT NULL COMMENT '知识检索相似度阈值',
  `history_limit` int NULL DEFAULT NULL COMMENT '历史消息数量限制',
  `knowledge_top_k` int NULL DEFAULT NULL COMMENT '知识检索TopK',
  `concurrency_strategy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'REJECT' COMMENT '并发满载策略:REJECT/QUEUE/FALLBACK',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scene_code`(`scene_code` ASC) USING BTREE,
  INDEX `idx_model_instance_id`(`model_instance_id` ASC) USING BTREE,
  INDEX `idx_prompt_template_id`(`prompt_template_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI场景配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_ai_scene_model
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_scene_model`;
CREATE TABLE `tl_ai_scene_model`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `scene_config_id` binary(16) NOT NULL COMMENT '场景配置ID',
  `model_id` binary(16) NOT NULL COMMENT '模型ID',
  `priority` int NULL DEFAULT 0 COMMENT '优先级(越大越优先)',
  `max_concurrency` int NULL DEFAULT 0 COMMENT '场景级最大并发数(0=使用模型全局配置)',
  `weight` int NULL DEFAULT 100 COMMENT '权重(同优先级下按权重分配)',
  `status` int NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scene_model`(`scene_config_id` ASC, `model_id` ASC) USING BTREE,
  INDEX `idx_scene_config_id`(`scene_config_id` ASC) USING BTREE,
  INDEX `idx_model_id`(`model_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场景-模型关联表(负载均衡)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_ai_sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `tl_ai_sensitive_word`;
CREATE TABLE `tl_ai_sensitive_word`  (
  `id` binary(16) NOT NULL COMMENT '敏感词ID',
  `word` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '敏感词（支持短语）',
  `word_type` int NOT NULL COMMENT '敏感词类型：1-医疗诊断，2-政治敏感，3-不当内容',
  `severity` int NULL DEFAULT 1 COMMENT '严重程度：1-低，2-中，3-高',
  `action_type` int NULL DEFAULT 1 COMMENT '处理方式：1-拒绝回答，2-替换，3-警告',
  `replace_word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '替换词',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_word`(`word` ASC) USING BTREE,
  INDEX `idx_word_type`(`word_type` ASC) USING BTREE,
  INDEX `idx_type_enabled`(`word_type` ASC, `is_enabled` ASC) USING BTREE,
  INDEX `idx_severity_enabled`(`severity` ASC, `is_enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '敏感词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_ad_config
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_ad_config`;
CREATE TABLE `tl_fee_ad_config`  (
  `id` binary(16) NOT NULL COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置唯一键，如 banner_home、rewarded_video_chat',
  `ad_type` int NOT NULL COMMENT '广告类型：1-Banner，2-插屏，3-激励视频，4-开屏',
  `ad_unit_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '微信广告单元ID',
  `placement` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面展示位置标识',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `free_user_only` int NULL DEFAULT 1 COMMENT '仅展示给免费用户：0-否，1-是',
  `display_interval_seconds` int NULL DEFAULT 0 COMMENT '展示间隔秒数，0表示每次都展示',
  `extra_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外配置JSON',
  `priority` int NULL DEFAULT 0 COMMENT '排序权重',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_ad_display_log
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_ad_display_log`;
CREATE TABLE `tl_fee_ad_display_log`  (
  `id` binary(16) NOT NULL COMMENT '日志ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `ad_config_id` binary(16) NOT NULL COMMENT '广告配置ID',
  `ad_type` int NOT NULL COMMENT '广告类型：1-Banner，2-插屏，3-激励视频，4-开屏',
  `action` int NOT NULL COMMENT '动作：1-展示，2-点击，3-关闭，4-获得奖励',
  `duration` int NULL DEFAULT NULL COMMENT '观看时长（秒）',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告展示日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_checkin_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_checkin_record`;
CREATE TABLE `tl_fee_checkin_record`  (
  `id` binary(16) NOT NULL COMMENT '记录ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `points_earned` int NULL DEFAULT 0 COMMENT '获得积分',
  `consecutive_checkin_days` int NULL DEFAULT NULL COMMENT '连续签到天数',
  `total_checkin_days` int NULL DEFAULT NULL COMMENT '累计签到天数',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_date`(`account_id` ASC, `checkin_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签到记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_growth_level
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_growth_level`;
CREATE TABLE `tl_fee_growth_level`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `level` int NOT NULL COMMENT '等级编号',
  `level_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级名称',
  `min_growth_value` int NOT NULL COMMENT '所需成长值',
  `bonus_ai_quota` int NOT NULL DEFAULT 0 COMMENT 'AI配额加成',
  `bonus_points_multiplier` double NOT NULL DEFAULT 1 COMMENT '积分倍率加成',
  `bonus_store_discount` double NOT NULL DEFAULT 1 COMMENT '商城折扣加成',
  `privilege` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '专属特权',
  `is_enabled` int NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level`(`level` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '成长等级配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_growth_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_growth_record`;
CREATE TABLE `tl_fee_growth_record`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录ID',
  `account_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号ID',
  `growth_change` int NOT NULL COMMENT '成长值变化',
  `growth_source` int NOT NULL COMMENT '来源：1-每日登录，2-签到，3-完成任务，4-开通续费，5-手动调整',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `business_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务ID',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `growth_value_after` int NOT NULL COMMENT '变化后成长值',
  `is_deleted` int NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_time`(`account_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员成长值记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_home_banner
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_home_banner`;
CREATE TABLE `tl_fee_home_banner`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '副标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_type` tinyint NOT NULL DEFAULT 0 COMMENT '链接类型：0-无跳转，1-节气详情，2-文章，3-外部链接，4-自定义页面',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接地址',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序（数值越大越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `start_date` datetime NULL DEFAULT NULL COMMENT '生效开始时间',
  `end_date` datetime NULL DEFAULT NULL COMMENT '生效结束时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '首页轮播表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_fee_member_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_member_plan`;
CREATE TABLE `tl_fee_member_plan`  (
  `id` binary(16) NOT NULL COMMENT '套餐ID',
  `plan_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '套餐名称',
  `member_level` int NOT NULL COMMENT '会员等级：1-月卡，2-年卡，3-终身',
  `duration_days` int NOT NULL COMMENT '会员时长（天），0表示永久',
  `original_price` int NOT NULL COMMENT '原价（分）',
  `current_price` int NOT NULL COMMENT '现价（分）',
  `discount_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '折扣标签',
  `ai_daily_quota` int NOT NULL COMMENT '每日AI配额，-1表示无限',
  `max_active_plans` int NOT NULL COMMENT '最大同时进行方案数，-1表示无限',
  `max_cycle_days` int NOT NULL COMMENT '最大方案周期（天）',
  `max_adjustments` int NOT NULL COMMENT '方案AI调整次数限制，-1表示无限',
  `assessment_monthly_quota` int NOT NULL COMMENT '每月体质评估次数，-1表示无限',
  `points_multiplier` int NOT NULL DEFAULT 1 COMMENT '积分获取倍率',
  `store_discount` double NOT NULL DEFAULT 1 COMMENT '商城折扣（0.95=95折）',
  `points_to_yuan_ratio` int NOT NULL DEFAULT 100 COMMENT '积分抵现比例（100积分=1元）',
  `benefits_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权益详情JSON',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `sub_growth_bonus` int NOT NULL DEFAULT 0 COMMENT '开通/续费奖励成长值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_plan_code`(`plan_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员套餐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_pay_order
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_pay_order`;
CREATE TABLE `tl_fee_pay_order`  (
  `id` binary(16) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `member_plan_id` binary(16) NULL DEFAULT NULL COMMENT '会员套餐ID',
  `plan_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `member_level` int NULL DEFAULT NULL COMMENT '会员等级',
  `duration_days` int NULL DEFAULT NULL COMMENT '会员时长（天）',
  `order_amount` int NOT NULL COMMENT '订单金额（分）',
  `pay_amount` int NOT NULL COMMENT '实际支付金额（分）',
  `status` int NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消，3-已退款，4-已关闭',
  `wx_prepay_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信预支付ID',
  `wx_transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信交易单号',
  `wx_openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信OpenID',
  `paid_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '订单过期时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_points_exchange_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_points_exchange_record`;
CREATE TABLE `tl_fee_points_exchange_record`  (
  `id` binary(16) NOT NULL COMMENT '记录ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `goods_id` binary(16) NOT NULL COMMENT '积分商品ID',
  `points_cost` int NOT NULL COMMENT '消耗积分',
  `exchange_value` int NOT NULL COMMENT '兑换值',
  `business_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务ID',
  `status` int NULL DEFAULT 0 COMMENT '状态：0-待处理，1-成功，2-已退还',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分兑换记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_points_goods
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_points_goods`;
CREATE TABLE `tl_fee_points_goods`  (
  `id` binary(16) NOT NULL COMMENT '商品ID',
  `goods_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品编码',
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `goods_type` int NOT NULL COMMENT '商品类型：1-AI问答次数，2-体质评估次数，3-文章解锁，4-商城优惠券',
  `value` int NOT NULL COMMENT '兑换值（如AI次数5）',
  `points_required` int NOT NULL COMMENT '所需积分',
  `daily_limit` int NULL DEFAULT 0 COMMENT '每日限制次数，0不限',
  `total_limit` int NULL DEFAULT 0 COMMENT '总限制次数，0不限',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标URL',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `is_enabled` int NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_goods_code`(`goods_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_points_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_points_record`;
CREATE TABLE `tl_fee_points_record`  (
  `id` binary(16) NOT NULL COMMENT '记录ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `points_change` int NOT NULL COMMENT '积分变化（正数增加，负数减少）',
  `points_type` tinyint NOT NULL COMMENT '积分类型：1-签到，2-健康计划，3-分享，4-消费，5-过期',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型：daily_checkin/health_plan_task/share/redeem',
  `business_id` binary(16) NULL DEFAULT NULL COMMENT '业务ID',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `balance_after` int NOT NULL COMMENT '变化后余额',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `total_points` int NULL DEFAULT NULL COMMENT '总积分',
  `available_point` int NULL DEFAULT NULL COMMENT '可用积分',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_time`(`account_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_points_type`(`points_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户积分记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_fee_points_rule
-- ----------------------------
DROP TABLE IF EXISTS `tl_fee_points_rule`;
CREATE TABLE `tl_fee_points_rule`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `rule_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则编码',
  `rule_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称',
  `points_type` int NOT NULL COMMENT '积分类型：1-签到 2-健康计划 3-消费 4-广告奖励',
  `points` int NOT NULL COMMENT '奖励积分数',
  `condition_type` int NOT NULL DEFAULT 0 COMMENT '条件类型：0-无条件 1-连续天数 2-累计天数',
  `condition_value` int NOT NULL DEFAULT 0 COMMENT '条件阈值',
  `daily_limit` int NOT NULL DEFAULT 0 COMMENT '每日上限次数，0-不限',
  `is_enabled` int NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_code`(`rule_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分规则配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_id_account
-- ----------------------------
DROP TABLE IF EXISTS `tl_id_account`;
CREATE TABLE `tl_id_account`  (
  `id` binary(16) NOT NULL COMMENT '账号ID',
  `account_type` int NOT NULL DEFAULT 1 COMMENT '账号类型：1-微信',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第三方OpenID',
  `unionid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信UnionID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码（加密）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` int NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `member_level` int NULL DEFAULT 0 COMMENT '会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员',
  `member_expire_time` datetime NULL DEFAULT NULL COMMENT '会员过期时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `cancellation_status` int NOT NULL DEFAULT 0 COMMENT '注销状态：0-正常，1-申请中，2-已注销',
  `cancellation_time` datetime NULL DEFAULT NULL COMMENT '注销申请时间',
  `cancellation_schedule` datetime NULL DEFAULT NULL COMMENT '计划注销时间（申请时间+7天）',
  `cancellation_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注销原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `growth_value` int NOT NULL DEFAULT 0 COMMENT '会员成长值',
  `growth_level` int NOT NULL DEFAULT 0 COMMENT '会员成长等级：0-无，1~9对应V1~V9',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_openid_type`(`openid` ASC, `account_type` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_unionid`(`unionid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_id_user
-- ----------------------------
DROP TABLE IF EXISTS `tl_id_user`;
CREATE TABLE `tl_id_user`  (
  `id` binary(16) NOT NULL COMMENT '用户ID',
  `account_id` binary(16) NOT NULL COMMENT '关联账号ID',
  `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区县',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `emergency_contact` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系电话',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_id_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `tl_id_user_follow`;
CREATE TABLE `tl_id_user_follow`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `follower_id` binary(16) NOT NULL COMMENT '关注者',
  `following_id` binary(16) NOT NULL COMMENT '被关注者',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_following`(`following_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_id_user_interaction
-- ----------------------------
DROP TABLE IF EXISTS `tl_id_user_interaction`;
CREATE TABLE `tl_id_user_interaction`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `account_id` binary(16) NOT NULL COMMENT '用户ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-方案广场 2-文章 3-食物 4-运动 5-方案详情',
  `target_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标ID',
  `interaction_type` tinyint NOT NULL COMMENT '互动类型：1-点赞 2-收藏',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account_target`(`account_id` ASC, `target_type` ASC, `target_id` ASC, `interaction_type` ASC) USING BTREE,
  INDEX `idx_account_type`(`account_id` ASC, `interaction_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户互动记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_id_user_preference
-- ----------------------------
DROP TABLE IF EXISTS `tl_id_user_preference`;
CREATE TABLE `tl_id_user_preference`  (
  `id` binary(16) NOT NULL COMMENT '偏好ID（主键）',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `preferred_food_nature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '偏好食物性质JSON',
  `disliked_foods` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '不喜欢食材ID列表JSON',
  `allergic_foods` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过敏食材ID列表JSON',
  `preferred_exercise_type` int NULL DEFAULT NULL COMMENT '偏好运动类型',
  `preferred_exercise_intensity` int NULL DEFAULT NULL COMMENT '偏好运动强度',
  `preferred_exercise_time` int NULL DEFAULT NULL COMMENT '偏好运动时间：1-清晨 2-上午 3-下午 4-傍晚 5-晚上',
  `preferred_acupoints` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '偏好穴位ID列表JSON',
  `food_preference_score` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '食材偏好评分JSON',
  `exercise_preference_score` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '运动偏好评分JSON',
  `total_interactions` int NULL DEFAULT 0 COMMENT '总交互次数',
  `last_learn_time` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  `height` decimal(5, 1) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 1) NULL DEFAULT NULL COMMENT '体重(kg)',
  `blood_type` int NULL DEFAULT NULL COMMENT '血型：1-A 2-B 3-AB 4-O',
  `allergy_history` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过敏史',
  `medical_history` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '病史',
  `preferred_food_texture` int NULL DEFAULT NULL COMMENT '口味偏好：1清淡 2浓郁 3辛辣 4偏甜',
  `dietary_restrictions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '饮食限制标签 JSON List<String> 如[\"vegetarian\",\"low_sugar\"]',
  `preferred_cuisines` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '口味偏好 JSON List<String> 如[\"sichuan\",\"cantonese\"]',
  `daily_water_intake` int NULL DEFAULT NULL COMMENT '目标饮水量(ml)',
  `dietary_goal` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '饮食目标描述',
  `preferred_exercise_duration` int NULL DEFAULT NULL COMMENT '运动时长(分钟)',
  `sleep_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '就寝时间 如23:00',
  `wake_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '起床时间 如07:00',
  `sleep_quality` int NULL DEFAULT NULL COMMENT '睡眠质量：1优 2良 3一般 4差',
  `stress_level` int NULL DEFAULT NULL COMMENT '压力水平：1低 2中 3高',
  `smoking_status` int NULL DEFAULT NULL COMMENT '吸烟状态：0从不 1已戒 2吸烟',
  `drinking_status` int NULL DEFAULT NULL COMMENT '饮酒状态：0从不 1偶尔 2经常',
  `occupation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职业',
  `health_goal_primary` int NULL DEFAULT NULL COMMENT '主要目标：1免疫 2睡眠 3体重 4压力 5消化 6体力',
  `health_goals` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '健康目标标签 JSON List<String> 如[\"immunity\",\"sleep\"]',
  `ai_tone_preference` int NULL DEFAULT NULL COMMENT 'AI回复风格：1专业 2亲切 3简洁',
  `ai_detail_level` int NULL DEFAULT NULL COMMENT 'AI详细程度：1简要 2适中 3详细',
  `preference_completeness` int NULL DEFAULT 0 COMMENT '偏好完善度 0-100',
  `last_user_edit_time` datetime NULL DEFAULT NULL COMMENT '用户最后手动编辑时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account`(`account_id` ASC) USING BTREE,
  INDEX `idx_exercise_type`(`preferred_exercise_type` ASC) USING BTREE,
  INDEX `idx_exercise_intensity`(`preferred_exercise_intensity` ASC) USING BTREE,
  INDEX `idx_exercise_time`(`preferred_exercise_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户偏好学习表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_acupoint_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_acupoint_plan`;
CREATE TABLE `tl_plan_acupoint_plan`  (
  `id` binary(16) NOT NULL COMMENT '方案ID（主键）',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-体质调理，2-季节养生，3-症状调理，4-日常保健',
  `target_constitution_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用体质编码（JSON数组）',
  `target_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用季节（JSON数组：1-春，2-夏，3-秋，4-冬）',
  `target_symptom` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '针对症状',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案描述',
  `acupoint_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '穴位列表JSON',
  `acupoint_focus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '穴位按摩重点',
  `massage_principles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '按摩原则JSON',
  `massage_routine` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '按摩安排JSON',
  `frequency_per_day` int NULL DEFAULT NULL COMMENT '每日建议次数',
  `duration_per_time` int NULL DEFAULT NULL COMMENT '每次按摩时长（分钟）',
  `best_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最佳按摩时间：1-清晨，2-上午，3-下午，4-傍晚，5-晚上',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注意事项',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `user_plan_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案ID',
  `user_history_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案历史ID',
  `generated_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的方案内容（Markdown格式）',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签，JSON格式，如：[\"足三里\",\"关元穴\",\"每日按摩\"]',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_constitution`(`target_constitution_codes` ASC) USING BTREE,
  INDEX `idx_season`(`target_season` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '穴位方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_adjust_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_adjust_record`;
CREATE TABLE `tl_plan_adjust_record`  (
  `id` binary(16) NOT NULL COMMENT '调整记录ID（主键）',
  `user_plan_id` binary(16) NOT NULL COMMENT '用户方案ID',
  `plan_type` int NULL DEFAULT NULL COMMENT '方案类型：1-饮食，2-运动，3-穴位，4-生活',
  `adjustment_type` int NOT NULL COMMENT '调整类型：1-AI自动调整，2-用户手动调整，3-系统调整',
  `adjustment_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调整原因',
  `before_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调整前内容JSON',
  `after_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调整后内容JSON',
  `ai_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AI模型版本',
  `ai_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI提示词',
  `ai_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI响应内容',
  `user_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户反馈',
  `effectiveness_score` int NULL DEFAULT NULL COMMENT '效果评分：1-5分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_confirmed` int NULL DEFAULT 0 COMMENT '是否已确认：0-待确认，1-已确认',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_plan`(`user_plan_id` ASC) USING BTREE,
  INDEX `idx_plan_type`(`plan_type` ASC) USING BTREE,
  INDEX `idx_adjustment_type`(`adjustment_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案调整记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_adjust_suggestion
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_adjust_suggestion`;
CREATE TABLE `tl_plan_adjust_suggestion`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sub_plan_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '子方案ID',
  `plan_type` int NOT NULL COMMENT '1饮食2运动3穴位4经络5生活',
  `suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'JSON数组',
  `suggestions_time` datetime NULL DEFAULT NULL COMMENT '生成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sub_plan`(`sub_plan_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案调整建议标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_comment
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_comment`;
CREATE TABLE `tl_plan_comment`  (
  `id` binary(16) NOT NULL COMMENT '评论ID（主键）',
  `plan_square_id` binary(16) NOT NULL COMMENT '方案广场ID',
  `account_id` binary(16) NOT NULL COMMENT '评论者账号ID',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论者昵称',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论者头像',
  `parent_id` binary(16) NULL DEFAULT NULL COMMENT '父评论ID（回复评论）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL列表JSON',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `reply_count` int NULL DEFAULT 0 COMMENT '回复次数',
  `status` int NULL DEFAULT 1 COMMENT '状态：1-正常 2-隐藏 3-删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_square`(`plan_square_id` ASC) USING BTREE,
  INDEX `idx_account`(`account_id` ASC) USING BTREE,
  INDEX `idx_parent`(`parent_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_cycle_report
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_cycle_report`;
CREATE TABLE `tl_plan_cycle_report`  (
  `id` binary(16) NOT NULL COMMENT '报告ID（主键）',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `plan_id` binary(16) NULL DEFAULT NULL COMMENT '被总结的方案ID',
  `new_plan_id` binary(16) NULL DEFAULT NULL COMMENT '触发总结的新方案ID',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的报告内容（Markdown格式）',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '阅读状态：0-未读，1-已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_account_read`(`account_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案周期报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_detail_relation
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_detail_relation`;
CREATE TABLE `tl_plan_detail_relation`  (
  `id` binary(16) NOT NULL COMMENT '关联ID（主键）',
  `plan_history_id` binary(16) NOT NULL COMMENT '方案历史ID',
  `relation_type` int NOT NULL COMMENT '关联类型：1-食材 2-运动 3-穴位 4-文章',
  `relation_id` binary(16) NOT NULL COMMENT '关联内容ID',
  `relation_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联内容名称',
  `is_collected` int NULL DEFAULT 0 COMMENT '是否收藏：0-否 1-是',
  `view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_history`(`plan_history_id` ASC) USING BTREE,
  INDEX `idx_relation`(`relation_type` ASC, `relation_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案详情关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_exercise_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_exercise_plan`;
CREATE TABLE `tl_plan_exercise_plan`  (
  `id` binary(16) NOT NULL COMMENT '方案ID（主键）',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-体质调理，2-季节养生，3-症状调理，4-日常保健',
  `target_constitution_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用体质编码',
  `target_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用季节：1-春，2-夏，3-秋，4-冬',
  `target_symptom` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '针对症状',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案描述',
  `exercise_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '运动项目列表JSON',
  `frequency_per_week` int NULL DEFAULT NULL COMMENT '每周运动次数',
  `duration_per_time` int NULL DEFAULT NULL COMMENT '每次运动时长（分钟）',
  `best_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最佳运动时间：1-清晨，2-上午，3-下午，4-傍晚，5-晚上',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注意事项',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `user_plan_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案ID',
  `user_history_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案历史ID',
  `generated_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的方案内容（Markdown格式）',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签，JSON格式，如：[\"每日运动\",\"每周3次\",\"清晨锻炼\"]',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_constitution`(`target_constitution_codes` ASC) USING BTREE,
  INDEX `idx_season`(`target_season` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '运动方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_food_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_food_plan`;
CREATE TABLE `tl_plan_food_plan`  (
  `id` binary(16) NOT NULL COMMENT '方案ID（主键）',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-体质调理，2-季节养生，3-症状调理，4-日常保健',
  `target_constitution_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用体质编码（JSON数组）',
  `target_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用季节（JSON数组：1-春，2-夏，3-秋，4-冬）',
  `target_symptom` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '针对症状',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案描述',
  `food_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '食材列表JSON',
  `diet_focus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '饮食重点',
  `diet_principles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '饮食原则JSON',
  `recommended_foods` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '推荐食材JSON',
  `avoid_foods` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌食材JSON',
  `meal_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用餐建议JSON（早餐、午餐、晚餐）',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注意事项',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `user_plan_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案ID',
  `user_history_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案历史ID',
  `generated_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的方案内容（Markdown格式）',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签，JSON格式，如：[\"健脾益气\",\"禁忌：辛辣\"]',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_constitution`(`target_constitution_codes` ASC) USING BTREE,
  INDEX `idx_season`(`target_season` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '食材方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_lifestyle_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_lifestyle_plan`;
CREATE TABLE `tl_plan_lifestyle_plan`  (
  `id` binary(16) NOT NULL COMMENT '方案ID（主键）',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-体质调理，2-季节养生，3-症状调理，4-日常保健',
  `target_constitution_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用体质编码（JSON数组）',
  `target_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用季节（JSON数组：1-春，2-夏，3-秋，4-冬）',
  `target_symptom` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '针对症状',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案描述',
  `sleep_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '睡眠建议',
  `sleep_time` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '睡眠时间建议JSON',
  `emotion_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '情志调节建议',
  `emotion_methods` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '情志调节方法JSON',
  `living_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '起居建议',
  `living_schedule` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '起居时间安排JSON',
  `other_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他建议',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `user_plan_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案ID',
  `user_history_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案历史ID',
  `generated_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的方案内容（Markdown格式）',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签，JSON格式，如：[\"早睡早起\",\"情志调节\",\"每日冥想\"]',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_constitution`(`target_constitution_codes` ASC) USING BTREE,
  INDEX `idx_season`(`target_season` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '生活方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_meridian_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_meridian_plan`;
CREATE TABLE `tl_plan_meridian_plan`  (
  `id` binary(16) NOT NULL COMMENT '方案ID（主键）',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-体质调理，2-季节养生，3-症状调理，4-日常保健',
  `target_constitution_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用体质编码（JSON数组）',
  `target_season` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用季节（JSON数组：1-春，2-夏，3-秋，4-冬）',
  `target_symptom` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '针对症状',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案描述',
  `meridian_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '经络列表JSON',
  `meridian_focus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '经络调理重点',
  `regulation_principles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调理原则JSON',
  `operation_methods` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作方法JSON',
  `frequency_per_day` int NULL DEFAULT NULL COMMENT '每日建议次数',
  `duration_per_time` int NULL DEFAULT NULL COMMENT '每次操作时长（分钟）',
  `best_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最佳操作时间：1-清晨，2-上午，3-下午，4-傍晚，5-晚上',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注意事项',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `user_plan_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案ID',
  `user_history_id` binary(16) NULL DEFAULT NULL COMMENT '用户方案历史ID',
  `generated_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的方案内容（Markdown格式）',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签，JSON格式，如：[\"膀胱经\",\"肺经\",\"每日敲击\"]',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_constitution`(`target_constitution_codes` ASC) USING BTREE,
  INDEX `idx_season`(`target_season` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '经络方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_share_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_share_record`;
CREATE TABLE `tl_plan_share_record`  (
  `id` binary(16) NOT NULL COMMENT '分享记录ID（主键）',
  `plan_history_id` binary(16) NOT NULL COMMENT '方案历史ID',
  `account_id` binary(16) NOT NULL COMMENT '分享者账号ID',
  `share_type` int NOT NULL COMMENT '分享类型：1-朋友圈 2-好友 3-方案广场',
  `share_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分享文案',
  `share_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分享图片URL',
  `view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `points_awarded` int NULL DEFAULT 0 COMMENT '获得积分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_history`(`plan_history_id` ASC) USING BTREE,
  INDEX `idx_account`(`account_id` ASC) USING BTREE,
  INDEX `idx_share_type`(`share_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案分享记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_square
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_square`;
CREATE TABLE `tl_plan_square`  (
  `id` binary(16) NOT NULL COMMENT '方案广场ID（主键）',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `user_plan_id` binary(16) NOT NULL COMMENT '用户方案ID',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `plan_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标题',
  `plan_tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签（JSON数组）',
  `plan_type` int NULL DEFAULT NULL COMMENT '方案类型：1-综合，2-饮食，3-运动，4-穴位，5-生活',
  `constitution_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体质名称',
  `plan_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案摘要',
  `completion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成率',
  `adjustment_count` int NULL DEFAULT 0 COMMENT '调整次数',
  `view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论次数',
  `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，2-隐藏，3-删除',
  `is_official` int NULL DEFAULT 0 COMMENT '是否官方推荐：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account`(`account_id` ASC) USING BTREE,
  INDEX `idx_constitution`(`constitution_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_official`(`is_official` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案广场表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_task
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_task`;
CREATE TABLE `tl_plan_task`  (
  `id` binary(16) NOT NULL COMMENT '任务ID（主键）',
  `user_plan_id` binary(16) NOT NULL COMMENT '用户方案ID',
  `plan_type` int NOT NULL COMMENT '方案类型：1-饮食，2-运动，3-穴位，4-经络，5-生活',
  `task_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `task_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务描述',
  `target_count` int NULL DEFAULT 1 COMMENT '目标次数',
  `completed_count` int NULL DEFAULT 0 COMMENT '已完成次数',
  `task_date` date NULL DEFAULT NULL COMMENT '任务日期',
  `task_category` int NULL DEFAULT NULL COMMENT '任务分类：1-每日必做，2-每周任务，3-自定义',
  `priority` int NULL DEFAULT 2 COMMENT '优先级：1-低，2-中，3-高',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源类型：food, exercise, acupoint, meridian, lifestyle',
  `resource_id` binary(16) NULL DEFAULT NULL COMMENT '资源ID',
  `status` int NULL DEFAULT 1 COMMENT '状态：1-待完成，2-已完成，3-已跳过',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_plan`(`user_plan_id` ASC) USING BTREE,
  INDEX `idx_plan_type`(`plan_type` ASC) USING BTREE,
  INDEX `idx_task_date`(`task_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_plan_user_plan
-- ----------------------------
DROP TABLE IF EXISTS `tl_plan_user_plan`;
CREATE TABLE `tl_plan_user_plan`  (
  `id` binary(16) NOT NULL COMMENT '方案ID（主键）',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `constitution_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体质编码',
  `constitution_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体质名称',
  `season` int NULL DEFAULT NULL COMMENT '季节：1-春，2-夏，3-秋，4-冬',
  `season_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '季节名称',
  `food_plan_id` binary(16) NULL DEFAULT NULL COMMENT '食材方案ID',
  `exercise_plan_id` binary(16) NULL DEFAULT NULL COMMENT '运动方案ID',
  `acupoint_plan_id` binary(16) NULL DEFAULT NULL COMMENT '穴位方案ID',
  `meridian_plan_id` binary(16) NULL DEFAULT NULL COMMENT '经络方案ID',
  `lifestyle_plan_id` binary(16) NULL DEFAULT NULL COMMENT '生活方案ID',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `plan_date` date NULL DEFAULT NULL COMMENT '方案快照日期（方案开始日期）',
  `total_tasks` int NULL DEFAULT 0 COMMENT '总任务数',
  `completed_tasks` int NULL DEFAULT 0 COMMENT '已完成任务数',
  `user_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户反馈',
  `user_rating` int NULL DEFAULT NULL COMMENT '用户评分：1-5星',
  `is_effective` int NULL DEFAULT 0 COMMENT '是否有效：0-未评价 1-有效 2-无效',
  `adjustment_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调整原因',
  `adjustment_count` int NULL DEFAULT 0 COMMENT '调整次数',
  `cycle_days` int NULL DEFAULT NULL COMMENT '方案周期（天）',
  `status` int NULL DEFAULT 1 COMMENT '1-进行中(ACTIVE), 2-已完成(COMPLETED), 3-已过期(EXPIRED), 4-已终止(TERMINATED)',
  `completion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成率',
  `ai_adjustment_count` int NULL DEFAULT 0 COMMENT 'AI调整次数',
  `last_adjustment_time` datetime NULL DEFAULT NULL COMMENT '最后调整时间',
  `user_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户备注',
  `plan_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AI生成的方案标题',
  `plan_tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案标签（JSON数组）',
  `daily_focuses` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '每日焦点/重点 JSON数组，格式：[\"第1天句子\",\"第2天句子\",...]',
  `share_count` int NULL DEFAULT 0 COMMENT '分享次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account`(`account_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户养生方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_sys_agreement
-- ----------------------------
DROP TABLE IF EXISTS `tl_sys_agreement`;
CREATE TABLE `tl_sys_agreement`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '编码：about/agreement/privacy',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正文（HTML）',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统协议与声明表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tl_sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `tl_sys_operation_log`;
CREATE TABLE `tl_sys_operation_log`  (
  `id` binary(16) NOT NULL COMMENT '日志ID',
  `admin_id` binary(16) NOT NULL COMMENT '管理员ID',
  `admin_username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员用户名',
  `operation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作',
  `method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回结果',
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `execution_time` int NULL DEFAULT NULL COMMENT '执行时长(ms)',
  `is_success` int NULL DEFAULT 1 COMMENT '是否成功：0-否，1-是',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `tl_sys_permission`;
CREATE TABLE `tl_sys_permission`  (
  `id` binary(16) NOT NULL COMMENT '权限ID',
  `parent_id` binary(16) NULL DEFAULT NULL COMMENT '父权限ID',
  `permission_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限编码',
  `permission_type` int NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `tl_sys_role`;
CREATE TABLE `tl_sys_role`  (
  `id` binary(16) NOT NULL COMMENT '角色ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `tl_sys_role_permission`;
CREATE TABLE `tl_sys_role_permission`  (
  `id` binary(16) NOT NULL COMMENT 'ID',
  `role_id` binary(16) NOT NULL COMMENT '角色ID',
  `permission_id` binary(16) NOT NULL COMMENT '权限ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `tl_sys_user`;
CREATE TABLE `tl_sys_user`  (
  `id` binary(16) NOT NULL COMMENT '管理员ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role_id` binary(16) NOT NULL COMMENT '角色ID',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_acupoint
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_acupoint`;
CREATE TABLE `tl_wis_acupoint`  (
  `id` binary(16) NOT NULL COMMENT '穴位ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '穴位名称',
  `name_pinyin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拼音',
  `name_abbreviation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩写',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-经穴，2-奇穴，3-阿是穴',
  `meridian_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属经络编码',
  `meridian_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属经络名称',
  `location_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '定位描述',
  `location_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '定位图片',
  `location_video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '定位视频',
  `efficacy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '主治功效',
  `indications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '主治病症',
  `operation_method` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作手法',
  `massage_tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '按摩手法',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `acupoint_combination` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '穴位配伍',
  `daily_health_tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日常养生',
  `view_type` int NULL DEFAULT 1 COMMENT '适用视图：1-正面，2-背面，3-通用',
  `position_x` decimal(8, 4) NULL DEFAULT NULL COMMENT 'X坐标（相对于图片宽度的百分比 0-100）',
  `position_y` decimal(8, 4) NULL DEFAULT NULL COMMENT 'Y坐标（相对于图片高度的百分比 0-100）',
  `marker_type` int NULL DEFAULT 1 COMMENT '标记类型：1-普通穴位，2-重要穴位，3-常用穴位',
  `marker_size` int NULL DEFAULT 12 COMMENT '标记点大小（像素）',
  `click_scale` decimal(3, 1) NULL DEFAULT 2.0 COMMENT '点击后放大倍数',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_meridian_code`(`meridian_code` ASC) USING BTREE,
  INDEX `idx_name_pinyin`(`name_pinyin` ASC) USING BTREE,
  INDEX `idx_view_type`(`view_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '穴位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_acupoint_combo
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_acupoint_combo`;
CREATE TABLE `tl_wis_acupoint_combo`  (
  `id` binary(16) NOT NULL COMMENT '配伍ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配伍名称',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-体质调理，2-症状调理，3-季节养生，4-日常保健',
  `target_constitution_codes` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用体质编码',
  `target_season` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用季节（JSON数组：1-春，2-夏，3-秋，4-冬）',
  `target_symptom` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '针对症状',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配伍描述',
  `acupoint_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '穴位ID列表（JSON数组）',
  `acupoint_names` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '穴位名称列表',
  `sequence` int NULL DEFAULT 1 COMMENT '按摩顺序：1-依次，2-同时',
  `operation_method` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作方法',
  `duration_min` int NULL DEFAULT NULL COMMENT '每次按摩时长（分钟）',
  `frequency_per_day` int NULL DEFAULT NULL COMMENT '每日建议次数',
  `efficacy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '功效说明',
  `indications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '适宜症状',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '示意图',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '演示视频',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_target_constitution`(`target_constitution_codes` ASC) USING BTREE,
  INDEX `idx_target_season`(`target_season` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '穴位配伍表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_article
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_article`;
CREATE TABLE `tl_wis_article`  (
  `id` binary(16) NOT NULL COMMENT '文章ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '副标题',
  `category` int NOT NULL COMMENT '分类：1-养生方法，2-四季养生，3-节气养生，4-食疗方案，5-中医知识',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（JSON数组）',
  `cover_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正文内容',
  `content_type` int NULL DEFAULT 1 COMMENT '内容类型：1-Markdown，2-HTML',
  `author` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `source` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源',
  `related_constitution_codes` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联体质编码',
  `related_season` int NULL DEFAULT NULL COMMENT '关联季节：1-春，2-夏，3-秋，4-冬',
  `related_solar_term` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联节气',
  `read_count` int NULL DEFAULT 0 COMMENT '阅读数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `share_count` int NULL DEFAULT 0 COMMENT '分享数',
  `is_recommended` int NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
  `is_featured` int NULL DEFAULT 0 COMMENT '是否精选：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` int NULL DEFAULT 1 COMMENT '状态：0-草稿，1-已发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_premium` int NULL DEFAULT 0 COMMENT '是否付费文章：0-免费，1-付费',
  `premium_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '付费配置JSON（所需积分等）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  INDEX `idx_is_recommended`(`is_recommended` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '养生知识文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_exercise
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_exercise`;
CREATE TABLE `tl_wis_exercise`  (
  `id` binary(16) NOT NULL COMMENT '运动ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '运动名称',
  `name_pinyin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拼音',
  `category` int NOT NULL COMMENT '运动分类：1-传统功法，2-有氧运动，3-力量训练，4-柔韧训练，5-休闲运动',
  `intensity` int NULL DEFAULT NULL COMMENT '运动强度：1-温和，2-轻度，3-中度，4-重度',
  `target_constitution_codes` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适宜体质编码（JSON数组）',
  `contra_constitution_codes` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '不适宜体质编码（JSON数组）',
  `target_season` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适宜季节（JSON数组：1-春，2-夏，3-秋，4-冬）',
  `target_age_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适宜年龄段：1-青年，2-中年，3-老年',
  `efficacy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '养生功效',
  `indications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '适宜症状',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '运动描述',
  `steps` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '动作步骤',
  `duration_min` int NULL DEFAULT NULL COMMENT '建议时长（分钟）',
  `calories_consumption` int NULL DEFAULT NULL COMMENT '消耗卡路里',
  `difficulty_level` int NULL DEFAULT 1 COMMENT '难度等级：1-简单，2-中等，3-困难',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '演示视频',
  `video_type` tinyint NULL DEFAULT 1 COMMENT '视频类型：1-直链(COS/VOD)，2-B站嵌入，3-其他外链',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_intensity`(`intensity` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '运动项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_fitness_option
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_fitness_option`;
CREATE TABLE `tl_wis_fitness_option`  (
  `id` binary(16) NOT NULL COMMENT '选项ID',
  `question_id` binary(16) NOT NULL COMMENT '题目ID',
  `option_no` int NOT NULL COMMENT '选项编号',
  `option_text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项内容',
  `option_value` int NOT NULL DEFAULT 0 COMMENT '选项分值（1-5）',
  `target_type_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联体质编码',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reverse_value` int NULL DEFAULT NULL COMMENT '反向计分时的分值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体质问卷选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_fitness_question
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_fitness_question`;
CREATE TABLE `tl_wis_fitness_question`  (
  `id` binary(16) NOT NULL COMMENT '题目ID',
  `question_no` int NOT NULL COMMENT '题目编号',
  `question_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `question_text_secondary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '题目补充说明',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属分类',
  `dimension` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维度',
  `answer_type` int NULL DEFAULT 1 COMMENT '答案类型：1-单选，2-多选',
  `is_required` int NULL DEFAULT 1 COMMENT '是否必答：0-否，1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `question_mode` int NULL DEFAULT 1 COMMENT '所属模式：1-简易模式，2-精细模式',
  `weight` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '题目权重',
  `reverse_score` int NULL DEFAULT 0 COMMENT '是否反向计分：0-否，1-是',
  `is_consistency_check` int NULL DEFAULT 0 COMMENT '一致性检验题：0-否，1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_question_no`(`question_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体质问卷题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_fitness_record
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_fitness_record`;
CREATE TABLE `tl_wis_fitness_record`  (
  `id` binary(16) NOT NULL COMMENT '记录ID',
  `account_id` binary(16) NOT NULL COMMENT '账号ID',
  `user_id` binary(16) NULL DEFAULT NULL COMMENT '用户ID（可选）',
  `constitution_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '体质类型编码',
  `constitution_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '体质类型名称',
  `score` decimal(10, 2) NULL DEFAULT NULL COMMENT '体质得分',
  `confidence` decimal(5, 2) NULL DEFAULT NULL COMMENT '置信度',
  `question_answers` json NULL COMMENT '问卷答案JSON',
  `analysis_result` json NULL COMMENT '分析结果JSON',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '完整报告内容',
  `suggestions` json NULL COMMENT '调理建议JSON',
  `is_latest` int NULL DEFAULT 1 COMMENT '是否为最新记录：0-否，1-是',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '测评时间',
  `assessment_mode` int NULL DEFAULT NULL COMMENT '测评模式：1-简易模式，2-精细模式',
  `mixed_types` json NULL COMMENT '兼夹体质JSON数组',
  `tendency_types` json NULL COMMENT '体质倾向JSON数组',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体质测评记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_fitness_type
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_fitness_type`;
CREATE TABLE `tl_wis_fitness_type`  (
  `id` binary(16) NOT NULL COMMENT '体质ID',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '体质编码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '体质名称',
  `name_en` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '体质描述',
  `characteristics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '体质特征',
  `formation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '形成原因',
  `health_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '健康建议',
  `diet_guidance` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '饮食指导',
  `exercise_guidance` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '运动指导',
  `emotion_guidance` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '情志调节',
  `acupoint_guidance` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '穴位保健',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体质类型配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_food
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_food`;
CREATE TABLE `tl_wis_food`  (
  `id` binary(16) NOT NULL COMMENT '食材ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食材名称',
  `name_pinyin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拼音',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-谷物，2-蔬菜，3-水果，4-肉类，5-药材',
  `nature` int NULL DEFAULT NULL COMMENT '性质：1-寒，2-凉，3-平，4-温，5-热',
  `flavor` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '味道',
  `meridian_entry` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '归经',
  `efficacy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '功效',
  `indications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '适宜人群/症状',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌人群',
  `usage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用法用量',
  `recipes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '养生食谱',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_nature`(`nature` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '食材表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_meridian
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_meridian`;
CREATE TABLE `tl_wis_meridian`  (
  `id` binary(16) NOT NULL COMMENT '经络ID',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '经络编码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '经络名称',
  `name_pinyin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拼音',
  `category` int NULL DEFAULT NULL COMMENT '分类：1-十二正经，2-奇经八脉',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '经络描述',
  `path_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '循行路线',
  `main_indications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '主治病候',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经络图',
  `view_type` int NULL DEFAULT 1 COMMENT '适用视图：1-正面，2-背面，3-通用',
  `line_color` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#FF6B6B' COMMENT '经络线条颜色',
  `line_width` decimal(5, 2) NULL DEFAULT 3.00 COMMENT '经络线条宽度',
  `line_style` int NULL DEFAULT 1 COMMENT '线条样式：1-实线，2-虚线',
  `is_visible` int NULL DEFAULT 1 COMMENT '默认是否显示：0-否，1-是',
  `sort_order_visual` int NULL DEFAULT 0 COMMENT '显示排序（决定层级）',
  `acupoint_count` int NULL DEFAULT 0 COMMENT '所属穴位数量',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `is_disabled` int NULL DEFAULT 0 COMMENT '是否禁用：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_view_type`(`view_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '经络表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_solar_term
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_solar_term`;
CREATE TABLE `tl_wis_solar_term`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `term_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节气名称（如：春分、清明）',
  `term_order` int NOT NULL COMMENT '节气序号（1-24，表示二十四节气顺序）',
  `start_month` tinyint NOT NULL COMMENT '开始月份（1-12）',
  `start_day` tinyint NOT NULL COMMENT '开始日期（1-31）',
  `end_month` tinyint NOT NULL COMMENT '结束月份（1-12）',
  `end_day` tinyint NOT NULL COMMENT '结束日期（1-31）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '节气描述',
  `season` tinyint NULL DEFAULT NULL COMMENT '季节：1-春，2-夏，3-秋，4-冬',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列表页封面图',
  `background_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详情页背景大图',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '节气简介（由来、历史）',
  `climate` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '气候特点',
  `health_principles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '养生原则（JSON数组）',
  `customs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '传统习俗（JSON数组）',
  `proverbs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '节气谚语（JSON数组）',
  `diet_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '饮食概要',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_term_order`(`term_order` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '节气基础表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tl_wis_solar_term_card
-- ----------------------------
DROP TABLE IF EXISTS `tl_wis_solar_term_card`;
CREATE TABLE `tl_wis_solar_term_card`  (
  `id` binary(16) NOT NULL COMMENT '主键ID',
  `solar_term_id` binary(16) NOT NULL COMMENT '节气ID（关联tf_solar_term.id）',
  `card_type` tinyint NOT NULL COMMENT '卡片类型（1-饮食，2-运动，3-情志，4-起居，5-其他）',
  `card_order` int NOT NULL DEFAULT 1 COMMENT '卡片序号（同类型卡片内的排序）',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级（数值越大优先级越高，用于首页轮播排序）',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡片标题',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卡片描述',
  `background_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '背景图URL',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_solar_term_id`(`solar_term_id` ASC) USING BTREE,
  INDEX `idx_card_type`(`card_type` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '节气卡片表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
