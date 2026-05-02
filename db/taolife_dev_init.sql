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

 Date: 02/05/2026 13:49:11
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
-- Records of tl_ai_chat_message
-- ----------------------------

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
-- Records of tl_ai_chat_rule
-- ----------------------------

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
-- Records of tl_ai_chat_rule_keyword
-- ----------------------------

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
-- Records of tl_ai_chat_session
-- ----------------------------

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
-- Records of tl_ai_model
-- ----------------------------
INSERT INTO `tl_ai_model` VALUES (0x30393861633764316663376234613330, 0x62326563313833613763393134383066, 'Qwen3-Embedding-8B', 'Qwen3-Embedding-8B', 'embedding', NULL, 0.2, 5000, NULL, 0, 0, 0, '', '', 0, 1, '2026-04-30 17:10:45', '2026-04-30 17:10:45');
INSERT INTO `tl_ai_model` VALUES (0x32386131316366326331356334346438, 0x31313639353964616261313863383733, 'glm-4.7', 'GLM-4.7', 'chat', '{\"temperature\":0.2,\"max_tokens\":5000,\"supports_thinking\":1,\"supports_image\":0}', 0.2, 5000, NULL, 1, 0, 5, '', '', 0, 1, '2026-04-16 22:56:38', '2026-04-30 17:05:28');
INSERT INTO `tl_ai_model` VALUES (0x61303261393962343133353730336634, 0x31313639353964616261313863383733, 'glm-4.7-flash', 'GLM-4.7-Flash', 'chat', '{\"temperature\":0.2,\"max_tokens\":5000,\"supports_thinking\":1,\"supports_image\":0}', 0.2, 5000, NULL, 1, 0, 0, NULL, '{\"streaming\": true, \"function_call\": false}', 0, 1, '2026-04-14 21:30:49', '2026-04-29 17:06:17');
INSERT INTO `tl_ai_model` VALUES (0x65333830363133396162366134333137, 0x62326563313833613763393134383066, 'bge-m3', 'bge-m3', 'embedding', NULL, 0.2, 5000, NULL, 0, 0, 0, '', '', 1, 1, '2026-04-30 17:11:15', '2026-04-30 17:14:58');
INSERT INTO `tl_ai_model` VALUES (0x65363136303335633136306334666534, 0x31356138393866396434633134653662, 'deepseek-v4-flash', 'DeepSeek-V4-Flash', 'chat', NULL, 0.2, 5000, NULL, 1, 0, 5, '', '', 1, 1, '2026-04-30 17:05:27', '2026-04-30 17:05:27');

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
-- Records of tl_ai_prompt_template
-- ----------------------------
INSERT INTO `tl_ai_prompt_template` VALUES (0x30333236306466653263616134663539, 'plan_adjustment', '方案单条调整提示词', 'user_prompt', '你是一位专业的中医养生方案顾问。用户希望调整当前的${planTypeName}方案，请根据用户的需求重新生成该方案内容。\n\n用户体质信息\n${constitutionDetail}\n\n当前${planTypeName}方案内容\n${currentContent}\n\n用户的调整需求\n${adjustmentRequest}\n\n要求\n1. 保持 Markdown 格式输出\n2. 只输出调整后的完整${planTypeName}方案内容\n3. 保留原有方案中合理的部分，只针对用户需求调整\n4. 内容应与中医养生理论一致，注意安全性\n5. 调整后的方案应保持实用性和可操作性', '[\"planTypeName\", \"constitutionDetail\", \"currentContent\"]', 1, 1, '健康方案单条（子方案）调整提示词', '2026-04-16 22:20:08', '2026-04-16 22:28:04');
INSERT INTO `tl_ai_prompt_template` VALUES (0x31326461643664663266303834363636, 'plan_adjustment_suggestion', '标签提示词模板', 'system_prompt', '你是一位中医养生方案顾问。根据用户的方案和上下文，生成4-5条个性化的方案调整建议标签。\n\n上下文\n- 体质：${constitutionDetail}\n- 季节：${seasonName}\n- 任务完成率：${completionRate}%\n- 当前${planTypeName}方案内容：\n${currentContent}\n\n要求\n1. 生成4-5条简短的调整建议（每条不超过15个字）\n2. 建议应结合体质、季节、当前方案内容和完成情况\n3. 完成率低时偏向\"简化\"\"降低难度\"；高时偏向\"进阶\"\"增加强度\"\n4. 只返回JSON数组格式，如：[\"建议1\",\"建议2\",\"建议3\",\"建议4\"]\n5. 不要返回其他内容', '[\"constitutionDetail\", \"seasonName\", \"completionRate\", \"planTypeName\", \"currentContent\"]', 1, 1, '标签提示词模板', '2026-04-16 22:25:32', '2026-04-16 22:27:23');
INSERT INTO `tl_ai_prompt_template` VALUES (0x31633962336230316232633961386263, 'global_context', '全局上下文提示词', 'global_context', '你是一位专业的中医养生顾问，名为\"道养生活\"。你的职责是为用户提供中医养生相关的咨询服务。\n\n【重要规则】\n1. 你只能回答中医养生相关的问题\n2. 严格禁止回答医疗诊断、用药建议等问题\n3. 回答必须基于提供的知识库内容\n4. 必须声明：本建议仅供参考，不作为医疗依据\n\n${userConstitution}\n\n${knowledgeContext}\n\n${conversationHistory}\n\n{{userPreferenceSummary}}\n\n【用户问题】\n${query}', '[\"userConstitution\", \"knowledgeContext\", \"conversationHistory\", \"userPreferenceSummary\", \"query\"]', 1, 1, '全局上下文提示词，自动追加到每条AI消息后面', '2026-04-14 21:30:49', '2026-04-28 13:34:33');
INSERT INTO `tl_ai_prompt_template` VALUES (0x35656161633735373065393931303838, 'cycle_report', '周期报告提示词', 'common_prompt', '你是一位专业的中医养生顾问，请根据用户数据生成简洁温暖的周期报告。\n\n请根据以下数据生成一份健康调理周期报告，使用Markdown格式，语气温暖鼓励。\n\n【方案信息】\n- 体质：${constitutionName}\n- 季节：${seasonName}\n- 周期：${cycleDays}天\n- 完成率：${completionRate}%%\n\n【任务执行情况】\n- 总任务数：${total}\n- 已完成：${completed}\n- 已跳过：${skipped}\n- 未完成：${pending}\n\n【各类型完成情况】\n- 饮食调理：已完成 ${foodCompleted} 项\n- 运动锻炼：已完成 ${exerciseCompleted} 项\n- 穴位按摩：已完成 ${acupointCompleted} 项\n- 经络调理：已完成 ${meridianCompleted} 项\n- 生活方式：已完成 ${lifestyleCompleted} 项\n\n请生成一份简洁的周期报告，包含：\n1. 整体评价（2-3句话）\n2. 各项调理表现分析\n3. 改进建议\n4. 下周期建议\n\n注意：报告不要太长，控制在300字以内。', '[\"constitutionName\", \"seasonName\", \"cycleDays\", \"completionRate\", \"total\", \"completed\", \"skipped\", \"pending\", \"foodCompleted\", \"exerciseCompleted\", \"acupointCompleted\", \"meridianCompleted\", \"lifestyleCompleted\"]', 1, 1, '周期报告提示词', '2026-04-14 21:30:49', '2026-04-14 23:04:10');
INSERT INTO `tl_ai_prompt_template` VALUES (0x38313066396464336531666134613430, 'plan_batch_adjustment', '方案批量调整提示词', 'user_prompt', '你是一位专业的中医养生方案顾问。用户希望调整养生方案，请根据需求判断哪些子方案需要调整，并生成调整后的内容。\n\n用户体质信息\n${constitutionDetail}\n当前季节：${seasonName}\n\n当前方案内容\n饮食方案\n${foodContent}\n运动方案\n${exerciseContent}\n穴位按摩方案\n${acupointContent}\n经络调理方案\n${meridianContent}\n生活起居方案\n${lifestyleContent}\n\n用户的调整需求\n${adjustmentRequest}\n\n要求\n1. 根据用户需求，只调整需要变更的子方案，未涉及的不要输出\n2. 保持 Markdown 格式\n3. 返回JSON格式，只包含需要调整的子方案，例如：\n{\"food\": \"调整后的饮食方案Markdown内容\", \"exercise\": \"调整后的运动方案Markdown内容\"}\n4. 未调整的子方案不要出现在返回中\n5. 内容应与中医养生理论一致，注意安全性\n6. 保留原有方案中合理的部分，只针对用户需求调整', '[\"constitutionDetail\", \"foodContent\", \"exerciseContent\", \"acupointContent\", \"meridianContent\", \"lifestyleContent\", \"adjustmentRequest\"]', 1, 1, '健康方案批量调整提示词', '2026-04-16 22:21:37', '2026-04-16 22:26:57');
INSERT INTO `tl_ai_prompt_template` VALUES (0x38643735363737346138633533663839, 'chat_system', 'AI聊天系统提示词', 'system_prompt', '你是道养生活的AI助手，专门为用户提供中医养生建议和健康咨询。\n\n请基于提供的用户体质信息和知识库内容，为用户提供专业、准确、友好的养生建议。\n\n请使用标准Markdown格式返回回答：\n1. 使用真正的换行符（换行），不要使用字面的\\n字符\n2. 合理使用标题、列表、粗体、斜体等Markdown语法\n3. 回答要简洁明了，重点突出，避免冗长\n\n如果涉及医疗建议，请务必提醒用户仅供参考，不作为医疗依据。', '[\"userConstitution\", \"knowledgeContext\"]', 1, 1, 'AI聊天的系统提示词', '2026-04-14 21:30:49', '2026-04-15 01:15:55');
INSERT INTO `tl_ai_prompt_template` VALUES (0x62306430613866633339616132613239, 'plan_generation', '方案生成提示词', 'common_prompt', '你是一位专业的中医养生方案规划师，名为\"道养生活\"。你的职责是为用户制定一份完整的个性化中医养生方案。\n\n【重要规则】\n1. 你必须基于用户体质和当前季节制定方案\n2. 方案必须专业、实用、可执行\n3. 如果涉及医疗建议，请提醒用户仅供参考\n4. 每个子方案用结构化的Markdown组织，使用清晰的标题和列表\n5. Markdown格式要求：标题从二级（##）开始使用，禁止使用一级标题（#）；段落精简，每行长度不超过20字；列表项内容紧凑，避免冗长描述\n6.请严格输出JSON格式，不要输出其他内容\n\n【用户体质信息】\n${constitutionDetail}\n\n【当前季节】${seasonName}\n\n【相关知识】\n${knowledgeContext}\n\n请为用户制定一份完整的${cycleDays}天养生方案，包含以下所有内容：\n\n【子方案要求】\n1. food（饮食方案）：推荐食材（至少5种）、禁忌食材、饮食原则，三餐建议\n2. exercise（运动方案）：推荐运动项目（至少3种）、每周频率、每次时长，最佳时间、注意事项\n3. acupoint（穴位按摩方案）：推荐穴位（至少3个）、按摩方法、每日次数、每次时长、注意事项\n4. meridian（经络调理方案）：推荐经络（至少2条）、调理方法、每日次数、每次时长、注意事项\n5. lifestyle（起居情志方案）：睡眠建议、情志调节方法、日常起居注意事项、其他养生建议\n\n6. title：整体方案标题，简洁（10-20字），体现体质、季节、调理特色\n7. tags：整体方案标签（3-5个），概括体质类型、季节、调理方向\n8. dailyFocuses：每日焦点（${cycleDays}句），每句不超过20字，体现当天调理重点\n\n【用户特殊需求或偏好】\n${preferences}\n\n请严格输出标准JSON格式：\n{\n  \"food\": {\"content\": \"饮食方案Markdown\", \"tags\": [\"标签1\", \"标签2\"]},\n  \"exercise\": {\"content\": \"运动方案Markdown\", \"tags\": [\"标签1\"]},\n  \"acupoint\": {\"content\": \"穴位方案Markdown\", \"tags\": [\"标签1\"]},\n  \"meridian\": {\"content\": \"经络方案Markdown\", \"tags\": [\"标签1\"]},\n  \"lifestyle\": {\"content\": \"生活方案Markdown\", \"tags\": [\"标签1\"]},\n  \"title\": \"方案标题\",\n  \"tags\": [\"整体标签1\", \"整体标签2\"],\n  \"dailyFocuses\": [\"第1天焦点\", \"第2天焦点\", ...]\n}', '[\"cycleDays\", \"preferences\", \"constitutionDetail\", \"seasonName\", \"knowledgeContext\"]', 1, 1, '方案生成提示词', '2026-04-14 21:30:49', '2026-04-17 15:40:44');
INSERT INTO `tl_ai_prompt_template` VALUES (0x63373035393633396131343962313161, 'forbidden_categories', '禁止分类规则', 'system_prompt', '[\"medical_diagnosis\", \"medication_advice\", \"surgery_advice\", \"emergency_treatment\", \"non_tcm\", \"political\"]', NULL, 1, 1, '禁止的分类编码列表', '2026-04-14 21:30:49', '2026-04-14 21:30:49');
INSERT INTO `tl_ai_prompt_template` VALUES (0x64396536636531316266656538326530, 'constitution_system', '体质评估系统提示词', 'system_prompt', '你是道养生活的中医体质评估助手，专门帮助用户进行中医体质辨识。\n\n你的职责是：\n1. 通过问卷方式评估用户的体质类型\n2. 根据评估结果提供个性化的养生建议\n3. 使用中医理论解释体质特点\n\n【评估规则】\n1. 只进行体质相关的评估咨询\n2. 不提供医疗诊断服务\n3. 建议仅供参考，不作为医疗依据\n4. 评估结果仅供参考，存在个体差异', '[\"constitutionTypes\"]', 1, 1, '体质评估的系统提示词', '2026-04-14 21:30:49', '2026-04-14 21:30:49');

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
-- Records of tl_ai_provider
-- ----------------------------
INSERT INTO `tl_ai_provider` VALUES (0x31313639353964616261313863383733, 'Zhipu', '智谱AI', 'chat', 'https://open.bigmodel.cn/api/paas/v4', 'NlQ73gYL7xlp0B383JKC0lxdNSZrq4Z7r8/ZLjO18/ydqdh1DTA6i5ddN0fEzKS7CKvuZ6NJojJqIYUxu+yJiEDTm4o6KBZXkw1+f5Q=', 1, 0, 0, 1, '智谱AI', '', '2026-04-14 21:30:49', '2026-04-30 16:53:02');
INSERT INTO `tl_ai_provider` VALUES (0x31356138393866396434633134653662, 'DeepSeek', '深度求索', 'chat', 'https://api.deepseek.com', 'NZbYE0l5vd+wnE2/idOisjIPohF8wMOIe/tnuNLT43q3J0lTRIU4nos0bZX6sKOVm9l2SYXoI8EZP4lfXdwM', 1, 1, 0, 1, '', '', '2026-04-30 16:52:32', '2026-04-30 16:52:56');
INSERT INTO `tl_ai_provider` VALUES (0x62326563313833613763393134383066, 'Moark', '魔力方舟', 'embedding', 'https://api.moark.com', 'WxTCj8HfjtwMvhH3vDjfYtP/lgyiOW8a8azSPy7IQ1jgGbgnXf+1o53tbjW4WZqez6mU8hG99wgVe61Otk5Lbe4sF6I=', 1, 1, 0, 1, '', '', '2026-04-30 17:07:15', '2026-04-30 17:41:03');

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
-- Records of tl_ai_provider_endpoint
-- ----------------------------
INSERT INTO `tl_ai_provider_endpoint` VALUES (0x32643438633934653061343234623263, 0x31356138393866396434633134653662, 'model_api', '/chat/completions', 'POST', 300000, 3, 1, '', '', '2026-04-30 17:04:25', '2026-04-30 17:04:25');
INSERT INTO `tl_ai_provider_endpoint` VALUES (0x35643237636466666138623734626334, 0x62326563313833613763393134383066, 'model_api', '/v1/embeddings', 'POST', 30000, 3, 1, '', '', '2026-04-30 17:07:56', '2026-04-30 17:07:56');
INSERT INTO `tl_ai_provider_endpoint` VALUES (0x65303161393962343133353730336634, 0x31313639353964616261313863383733, 'model_api', '/chat/completions', 'POST', 60000, 3, 1, 'GLM同步聊天接口', '', '2026-04-14 21:30:49', '2026-04-30 17:03:23');
INSERT INTO `tl_ai_provider_endpoint` VALUES (0x65303261393962343133353730336634, 0x31313639353964616261313863383733, 'model_api', '/async/chat/completions', 'POST', 120000, 3, 1, 'GLM异步聊天接口', '', '2026-04-14 21:30:49', '2026-04-30 17:03:27');
INSERT INTO `tl_ai_provider_endpoint` VALUES (0x65303361393962343133353730336634, 0x31313639353964616261313863383733, 'realtime_api', '/agent-runtime/chat', 'WSS', 60000, 3, 1, 'GLM实时Agent接口', '', '2026-04-14 21:30:49', '2026-04-30 17:03:30');

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
-- Records of tl_ai_scene_config
-- ----------------------------
INSERT INTO `tl_ai_scene_config` VALUES (0x37396630333964373435623936636133, 'ai_chat', 'AI聊天', 0x65363136303335633136306334666534, 0x38643735363737346138633533663839, '{\"temperature\": 0.2, \"max_tokens\": 5000}', '{\"similarityThreshold\": 0.7, \"topK\": 5, \"historyLimit\": 10}', 0.7, 5, 5, 'REJECT', 1, 'AI聊天的场景配置', '2026-04-14 21:30:49', '2026-04-30 17:12:00');
INSERT INTO `tl_ai_scene_config` VALUES (0x39656333633932316463323564303463, 'constitution_assessment', '体质评估', 0x65363136303335633136306334666534, 0x64396536636531316266656538326530, '{\"temperature\": 0.1, \"max_tokens\": 3000}', '{}', NULL, NULL, NULL, 'REJECT', 1, '体质评估的场景配置', '2026-04-14 21:30:49', '2026-04-30 17:12:05');

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
-- Records of tl_ai_scene_model
-- ----------------------------

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
-- Records of tl_ai_sensitive_word
-- ----------------------------
INSERT INTO `tl_ai_sensitive_word` VALUES (0x32356434323163356665316234336233, '提示词', 3, 2, 1, '', 1, 0, '2026-04-16 20:50:09', '2026-04-16 20:50:09');
INSERT INTO `tl_ai_sensitive_word` VALUES (0x36346534363639376438636634376464, 'Prompt', 3, 2, 1, '', 1, 0, '2026-04-16 20:50:25', '2026-04-16 20:50:30');
INSERT INTO `tl_ai_sensitive_word` VALUES (0x39316636666134373938623834623738, '共产党', 2, 3, 1, '', 1, 0, '2026-04-16 20:50:48', '2026-04-16 20:50:48');
INSERT INTO `tl_ai_sensitive_word` VALUES (0x63303634663238626665613734346664, '自杀', 3, 3, 1, '', 1, 0, '2026-04-16 20:51:34', '2026-04-16 20:51:34');
INSERT INTO `tl_ai_sensitive_word` VALUES (0x65356666306131376166343034363866, '医疗诊断', 1, 3, 1, '', 1, 0, '2026-04-13 17:29:11', '2026-04-16 20:50:15');

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
-- Records of tl_fee_ad_config
-- ----------------------------
INSERT INTO `tl_fee_ad_config` VALUES (0x32313862316337313439396634393634, 'banner_home', 1, 'adunit-placeholder-home', 'pages/index/index', 1, 1, 0, NULL, 1, 0, '2026-04-18 21:27:14', '2026-04-19 00:20:35');
INSERT INTO `tl_fee_ad_config` VALUES (0x36333332333165336439343537333961, 'splash_home', 4, 'adunit-placeholder-splash', 'pages/index/index', 1, 1, 0, NULL, 10, 0, '2026-04-18 21:27:14', '2026-04-19 00:20:18');
INSERT INTO `tl_fee_ad_config` VALUES (0x36613738626635666531376239646632, 'rewarded_video_chat', 3, 'adunit-placeholder-chat', 'pages/ai/chat/index', 1, 1, 0, NULL, 5, 0, '2026-04-18 21:27:14', '2026-04-19 00:20:33');
INSERT INTO `tl_fee_ad_config` VALUES (0x61343266336137333134613961303131, 'interstitial_stats', 2, 'adunit-placeholder-stats', 'pages/identity/checkin/stats/index', 1, 1, 86400, NULL, 3, 0, '2026-04-18 21:27:14', '2026-04-19 00:20:44');
INSERT INTO `tl_fee_ad_config` VALUES (0x64313463376236626532303932623731, 'banner_article', 1, 'adunit-placeholder-article', 'pages/wisdom/article/articleDetail', 1, 1, 0, NULL, 2, 0, '2026-04-18 21:27:14', '2026-04-19 00:20:27');

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
-- Records of tl_fee_ad_display_log
-- ----------------------------

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
-- Records of tl_fee_checkin_record
-- ----------------------------

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
-- Records of tl_fee_growth_level
-- ----------------------------
INSERT INTO `tl_fee_growth_level` VALUES ('33280ba84aad6cfb', 5, '钻石会员', 1600, 16, 1.25, 0.9, '专属标识', 1, 5, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('46a3e927089b698c', 8, '荣耀会员', 5000, 38, 1.5, 0.82, '专属标识+优先客服', 1, 8, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('95d7b04393b8792e', 6, '星耀会员', 2400, 20, 1.3, 0.88, '专属标识', 1, 6, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('9611682fc6dfef76', 9, '传奇会员', 7000, 50, 1.8, 0.8, '专属标识+优先客服+专属活动', 1, 9, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('b61da973558f3c78', 4, '铂金会员', 1000, 12, 1.2, 0.92, '', 1, 4, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('c53bb6e28a702442', 3, '黄金会员', 600, 8, 1.15, 0.94, '', 1, 3, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('d1f8de0d3113c9ca', 7, '王者会员', 3500, 28, 1.4, 0.85, '专属标识+优先客服', 1, 7, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('d6ed34378c89b3d1', 2, '白银会员', 300, 5, 1.1, 0.96, '', 1, 2, '2026-04-20 02:19:47', '2026-04-20 02:19:47');
INSERT INTO `tl_fee_growth_level` VALUES ('fc1f81e962a17d48', 1, '青铜会员', 100, 2, 1.05, 0.98, '', 1, 1, '2026-04-20 02:19:47', '2026-04-20 02:19:47');

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
-- Records of tl_fee_growth_record
-- ----------------------------

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
-- Records of tl_fee_home_banner
-- ----------------------------
INSERT INTO `tl_fee_home_banner` VALUES (0x30343630646666346330356430376131, '寒露 · 露水欲凝', '气温更低，宜防寒保暖', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 17, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x30343932663133396635353664353832, '小满 · 麦穗渐满', '夏熟作物饱满，宜清热利湿', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 8, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x32393537386232643936393334333064, '霜降 · 霜华满地', '开始降霜，宜进补养胃', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 18, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x32663563336131633036373831333065, '处暑 · 暑气渐消', '炎热离开，宜滋阴润肺', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 14, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x33343636383634663332616263303161, '夏至 · 阳盛之极', '白昼最长，宜养心护阳', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 10, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x35313536373335303434353963323763, '立春 · 养生之道', '春季养肝，宜疏肝理气', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 1, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x35333663303636313231353464343232, '立秋 · 秋高气爽', '秋季开始，宜养肺润燥', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 13, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x35336663373163356166646538306365, '惊蛰 · 万物复苏', '惊蛰春雷响，宜养肝护脾', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 3, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x35626464343361386632326230346435, '立冬 · 万物收藏', '冬季开始，宜温补御寒', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 19, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x36633830643630626635376538643563, '芒种 · 忙种忙收', '有芒之谷可种，宜养心护脾', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 9, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x38636136376331663664666538636539, '秋分 · 昼夜等长', '阴阳相半，宜平衡调养', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 16, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x39306639366138643132643837363139, '小雪 · 初雪降临', '开始降雪，宜温肾助阳', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 20, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x39316662336130313164636633336632, '白露 · 露凝而白', '天气转凉，宜温补脾胃', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 15, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x61613433383635343461336365356266, '清明 · 踏青时节', '天气晴朗，宜踏青赏花', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 5, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x62386234393030386161373936373536, '春分 · 阴阳平衡', '昼夜平分，宜调和阴阳', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 4, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x62393962636566653236306132346133, '大暑 · 炎热至极', '一年最热，宜防暑降温', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 12, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x62393963623062323832353236363266, '立夏 · 夏日初长', '万物茂盛，宜养心安神', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 7, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x64313530396334663534626530326564, '小暑 · 天气渐热', '小暑温风至，宜清热消暑', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 11, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x64353231383039643162666230653331, '谷雨 · 春末养生', '雨生百谷，宜健脾祛湿', 'https://YOUR_BUCKET_NAME.cos.ap-hongkong.myqcloud.com/uploads/b6a74a699ff092bc/2026/05/01/flowers-7944288_1777647676987.jpg', 0, '', 100, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');
INSERT INTO `tl_fee_home_banner` VALUES (0x66663637626165313263363835363961, '雨水 · 春雨润物', '雨水时节湿气重，注意保暖', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, NULL, 2, 1, NULL, NULL, 0, '2026-04-20 13:39:33', '2026-04-20 13:39:33');

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
-- Records of tl_fee_member_plan
-- ----------------------------
INSERT INTO `tl_fee_member_plan` VALUES (0x32393166626439323462383934313734, 'quarterly', '季度会员', 1, 90, 7990, 4990, '', 120, 5, 21, 5, 3, 2, 0.95, 50, '', 0, 1, 0, '2026-05-01 15:31:16', '2026-05-01 15:35:30', 50);
INSERT INTO `tl_fee_member_plan` VALUES (0x34383038323539366631373238326632, 'yearly', '年卡会员', 2, 365, 29900, 19900, '限时特惠', 200, 10, 30, -1, -1, 2, 0.9, 50, NULL, 2, 1, 0, '2026-04-18 21:27:20', '2026-05-01 15:24:20', 80);
INSERT INTO `tl_fee_member_plan` VALUES (0x63393436636534643061633561313636, 'monthly', '月卡会员', 1, 30, 2990, 1990, '限时特惠', 50, 3, 21, 5, 3, 2, 0.95, 50, NULL, 1, 1, 0, '2026-04-18 21:27:20', '2026-05-01 15:33:05', 20);
INSERT INTO `tl_fee_member_plan` VALUES (0x66396130306566366534656466316638, 'lifetime', '终身会员', 3, 0, 59900, 39900, '一次购买永久享用', -1, -1, 30, -1, -1, 3, 0.85, 50, NULL, 3, 1, 1, '2026-04-18 21:27:20', '2026-05-01 15:35:10', 300);

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
-- Records of tl_fee_pay_order
-- ----------------------------

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
-- Records of tl_fee_points_exchange_record
-- ----------------------------

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
-- Records of tl_fee_points_goods
-- ----------------------------
INSERT INTO `tl_fee_points_goods` VALUES (0x32666662653730636637623237636332, 'article_unlock', '解锁付费文章24h', 3, 1, 30, 5, 0, NULL, NULL, 4, 1, 0, '2026-04-18 21:27:14', '2026-04-19 00:19:41');
INSERT INTO `tl_fee_points_goods` VALUES (0x63366436306665393930636537636666, 'assessment_1', '体质评估1次', 2, 1, 200, 1, 0, NULL, NULL, 3, 1, 0, '2026-04-18 21:27:14', '2026-04-19 00:19:39');
INSERT INTO `tl_fee_points_goods` VALUES (0x64343066313831363765383034376261, 'ai_quota_5', 'AI问答5次', 1, 5, 50, 3, 0, NULL, NULL, 1, 1, 0, '2026-04-18 21:27:14', '2026-04-19 00:19:51');
INSERT INTO `tl_fee_points_goods` VALUES (0x66373334366637616666343464306161, 'ai_quota_10', 'AI问答10次', 1, 10, 80, 2, 0, NULL, NULL, 2, 1, 0, '2026-04-18 21:27:14', '2026-04-19 00:19:49');

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
-- Records of tl_fee_points_record
-- ----------------------------

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
-- Records of tl_fee_points_rule
-- ----------------------------
INSERT INTO `tl_fee_points_rule` VALUES ('266627781a3e3fd3', 'CONSECUTIVE_3', '连续签到3天', 1, 5, 1, 3, 0, 1, 2, '连续签到3天额外奖励', '2026-04-20 00:17:32', '2026-04-20 00:17:32');
INSERT INTO `tl_fee_points_rule` VALUES ('2755135f477a0649', 'AD_REWARD', '观看广告', 4, 20, 0, 0, 3, 1, 6, '观看广告奖励积分', '2026-04-20 00:17:32', '2026-04-20 00:17:32');
INSERT INTO `tl_fee_points_rule` VALUES ('5871cdc80fad4261', 'CONSECUTIVE_7', '连续签到7天', 1, 15, 1, 7, 0, 1, 3, '连续签到7天额外奖励', '2026-04-20 00:17:32', '2026-04-20 00:17:32');
INSERT INTO `tl_fee_points_rule` VALUES ('b2ae14ab66c77b8c', 'PLAN_TASK', '完成养生方案任务', 2, 5, 0, 0, 0, 1, 5, '完成养生方案中的任务', '2026-04-20 00:17:32', '2026-04-20 00:17:32');
INSERT INTO `tl_fee_points_rule` VALUES ('c7840a99bce3a694', 'DAILY_CHECKIN', '每日签到', 1, 10, 0, 0, 1, 1, 1, '每日签到基础积分', '2026-04-20 00:17:32', '2026-04-20 00:17:32');
INSERT INTO `tl_fee_points_rule` VALUES ('f97b0973ad8f1f2c', 'CONSECUTIVE_30', '连续签到30天', 1, 50, 1, 30, 0, 1, 4, '连续签到30天额外奖励', '2026-04-20 00:17:32', '2026-04-20 00:17:32');
INSERT INTO `tl_fee_points_rule` VALUES ('fe0e30537e3bf2fe', 'PLAN_SHARE', '分享方案', 2, 10, 0, 0, 1, 1, 7, '分享养生方案奖励', '2026-04-20 00:17:32', '2026-04-20 00:17:32');

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
-- Records of tl_id_account
-- ----------------------------

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
-- Records of tl_id_user
-- ----------------------------

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
-- Records of tl_id_user_follow
-- ----------------------------

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
-- Records of tl_id_user_interaction
-- ----------------------------

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
-- Records of tl_id_user_preference
-- ----------------------------

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
-- Records of tl_plan_acupoint_plan
-- ----------------------------

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
-- Records of tl_plan_adjust_record
-- ----------------------------

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
-- Records of tl_plan_adjust_suggestion
-- ----------------------------

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
-- Records of tl_plan_comment
-- ----------------------------

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
-- Records of tl_plan_cycle_report
-- ----------------------------

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
-- Records of tl_plan_detail_relation
-- ----------------------------

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
-- Records of tl_plan_exercise_plan
-- ----------------------------

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
-- Records of tl_plan_food_plan
-- ----------------------------

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
-- Records of tl_plan_lifestyle_plan
-- ----------------------------

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
-- Records of tl_plan_meridian_plan
-- ----------------------------

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
-- Records of tl_plan_share_record
-- ----------------------------

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
-- Records of tl_plan_square
-- ----------------------------

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
-- Records of tl_plan_task
-- ----------------------------

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
-- Records of tl_plan_user_plan
-- ----------------------------

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
-- Records of tl_sys_agreement
-- ----------------------------
INSERT INTO `tl_sys_agreement` VALUES (0x36376231323565306262646334323039, 'agreement', '用户服务协议', '<h3>用户服务协议</h3><p>更新日期：2026年4月28日</p><p>生效日期：2026年4月28日</p><h4>一、总则</h4><p>欢迎使用道养生活小程序。本协议是您与道养生活之间关于使用道养生活服务所订立的协议。</p><h4>二、服务内容</h4><p>道养生活提供健康咨询、体质测评、养生方案、AI智能问答等服务，相关建议仅供参考，不构成医疗诊断。</p><h4>三、用户账号</h4><p>您需通过微信授权登录使用本服务，应妥善保管账号信息。</p><h4>四、免责声明</h4><p>本应用提供的健康建议不替代专业医疗意见。如有健康问题，请及时就医。</p>', 1, 0, '2026-04-28 21:39:06', '2026-04-28 21:39:06');
INSERT INTO `tl_sys_agreement` VALUES (0x39376135333733373533356230663863, 'about', '关于我们', '<h3>道养生活</h3><p>道养生活是一款基于中医养生理念的健康管理应用，致力于为用户提供个性化的养生方案、智能健康问答、体质测评等服务。</p><p>我们相信，传统中医智慧与现代科技的结合，能帮助每个人找到适合自己的健康生活方式。</p>', 1, 0, '2026-04-28 21:39:06', '2026-04-28 21:39:06');
INSERT INTO `tl_sys_agreement` VALUES (0x39386464643734653135363466613233, 'privacy', '隐私保护政策', '<h3>隐私保护政策</h3><p>更新日期：2026年4月28日</p><p>生效日期：2026年4月28日</p><h4>一、信息收集</h4><p>我们仅收集为您提供服务所必要的信息，包括：微信授权信息（昵称、头像）、健康数据（身高、体重等您主动填写的偏好数据）。</p><h4>二、信息使用</h4><p>您的信息仅用于提供个性化健康服务和改善用户体验。</p><h4>三、信息保护</h4><p>我们采用业界标准的安全技术和管理措施保护您的个人信息。</p><h4>四、您的权利</h4><p>您有权查看、修改、删除您的个人信息，也可申请注销账号。</p>', 1, 0, '2026-04-28 21:39:06', '2026-04-28 21:39:06');

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
-- Records of tl_sys_operation_log
-- ----------------------------

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
-- Records of tl_sys_permission
-- ----------------------------
INSERT INTO `tl_sys_permission` VALUES (0x30333732656465643934656434663136, 0x65613834313134626630343434333236, '用户偏好', 'userPreference', 1, '/identity/userPreference', '/identity/userPreference/index.vue', 'icon-park-outline:baby', 2, 0, 0, '2026-04-12 14:24:39', '2026-04-28 21:42:44');
INSERT INTO `tl_sys_permission` VALUES (0x30383266623363346435313234386461, 0x33343638363765303232623534363461, '模型实例管理', 'modelInstance', 1, '/ai/model-instance', '/ai/model-instance/index.vue', 'icon-park-outline:five-ellipses', 3, 0, 0, '2026-04-14 18:24:36', '2026-04-14 23:18:18');
INSERT INTO `tl_sys_permission` VALUES (0x30383933336637386432393334316631, 0x39336563333136393530313234646266, '广告管理', 'ad', 1, '/fee/ad', '/fee/ad/index.vue', 'icon-park-outline:texture-two', 2, 0, 0, '2026-04-18 23:05:30', '2026-04-19 23:49:56');
INSERT INTO `tl_sys_permission` VALUES (0x30646433613230663535643634656433, 0x39336563333136393530313234646266, '签到记录', 'checkinManage', 1, '/fee/checkin', '/fee/checkin/index.vue', 'icon-park-outline:calendar', 7, 0, 0, '2026-04-12 10:27:01', '2026-04-19 23:52:25');
INSERT INTO `tl_sys_permission` VALUES (0x30663332333365346463313434376362, 0x33343638363765303232623534363461, '提示词模板管理', 'promptTemplate', 1, '/ai/prompt-template', '/ai/prompt-template/index.vue', 'icon-park-outline:hippo', 4, 0, 0, '2026-04-14 18:26:20', '2026-04-14 23:18:29');
INSERT INTO `tl_sys_permission` VALUES (0x31346434373363313163373963316239, 0x61336639363363613435303637643537, '监控页', 'monitor', 1, '/system/monitor', '/system/monitor/index.vue', 'icon-park-outline:owl', 99, 0, 0, '2026-04-11 20:56:13', '2026-04-13 23:44:08');
INSERT INTO `tl_sys_permission` VALUES (0x33343638363765303232623534363461, NULL, 'AI管理', 'ai', 1, '/ai', '', 'icon-park-outline:baby-app', 6, 0, 0, '2026-04-12 22:55:45', '2026-04-13 22:23:45');
INSERT INTO `tl_sys_permission` VALUES (0x33626131656462323062643334383634, NULL, '知识管理', 'wisdom', 1, '/wisdom', '', 'icon-park-outline:halo', 5, 0, 0, '2026-04-12 14:53:24', '2026-04-13 19:53:13');
INSERT INTO `tl_sys_permission` VALUES (0x34303939326266313939396134383666, 0x62333265376465366434663334303666, '评论管理', 'healthComment', 1, '/health/comment', '/health/comment/index.vue', 'icon-park-outline:game', 0, 0, 1, '2026-04-12 14:17:38', '2026-04-13 17:07:37');
INSERT INTO `tl_sys_permission` VALUES (0x34363637656566383532636434643936, 0x33626131656462323062643334383634, '食材管理', 'foodManage', 1, '/wisdom/food', '/wisdom/food/index.vue', 'icon-park-outline:pineapple', 0, 0, 0, '2026-04-12 10:20:44', '2026-04-12 15:01:29');
INSERT INTO `tl_sys_permission` VALUES (0x34636365646665373737636134353061, 0x39336563333136393530313234646266, '积分兑换记录', 'pointsExchange', 1, '/fee/pointsExchange', '/fee/pointsExchange/index.vue', 'icon-park-outline:off-screen-two', 6, 0, 0, '2026-04-18 23:15:05', '2026-04-19 23:52:21');
INSERT INTO `tl_sys_permission` VALUES (0x34646237633332633834633634316664, 0x33343638363765303232623534363461, '问题规则', 'ruleManage', 1, '/ai/question-rule', '/ai/question-rule/index.vue', 'icon-park-outline:egg', 6, 0, 0, '2026-04-12 23:01:51', '2026-04-14 23:18:54');
INSERT INTO `tl_sys_permission` VALUES (0x35383739643566393533383034663236, 0x33626131656462323062643334383634, '文章管理', 'articleManage', 1, '/wisdom/article', '/wisdom/article/index.vue', 'icon-park-outline:file-hash', 0, 0, 0, '2026-04-12 10:25:00', '2026-04-13 22:19:34');
INSERT INTO `tl_sys_permission` VALUES (0x36316337623232333433646334393464, 0x33343638363765303232623534363461, '场景配置', 'sceneConfig', 1, '/ai/scene-config', '/ai/scene-config/index.vue', 'icon-park-outline:nested-arrows', 5, 0, 0, '2026-04-14 18:27:55', '2026-04-14 23:18:36');
INSERT INTO `tl_sys_permission` VALUES (0x36633365393966376334613034613236, 0x33343638363765303232623534363461, '敏感词管理', 'sensitiveManage', 1, '/ai/sensitive', '/ai/sensitive/index.vue', 'icon-park-outline:worker', 8, 0, 0, '2026-04-12 23:03:03', '2026-04-14 23:19:05');
INSERT INTO `tl_sys_permission` VALUES (0x36643237316639643037626434636534, 0x33626131656462323062643334383634, '经络管理', 'meridianManage', 1, '/wisdom/meridian', '/wisdom/meridian/index.vue', 'icon-park-outline:parking', 0, 0, 0, '2026-04-12 10:21:59', '2026-04-12 15:01:24');
INSERT INTO `tl_sys_permission` VALUES (0x37313761396364376364633534316436, 0x39336563333136393530313234646266, '会员成长记录', 'growthRecord', 1, '/fee/growthRecord', '/fee/growthRecord/index.vue', 'icon-park-outline:people-upload', 9, 0, 0, '2026-04-20 01:45:24', '2026-04-20 15:21:02');
INSERT INTO `tl_sys_permission` VALUES (0x37323231633332316437383534613130, 0x65613834313134626630343434333236, '协议管理', 'agreement', 1, '/identity/agreement', '/identity/agreement/index.vue', 'icon-park-outline:document-folder', 3, 0, 0, '2026-04-28 21:42:37', NULL);
INSERT INTO `tl_sys_permission` VALUES (0x38373530666663346666623334333666, 0x33626131656462323062643334383634, '体质管理', 'constitutionManage', 1, '/wisdom/constitution', '/wisdom/constitution/index.vue', 'icon-park-outline:people-safe-one', 10, 0, 0, '2026-04-12 22:57:57', '2026-04-14 23:55:53');
INSERT INTO `tl_sys_permission` VALUES (0x38653530356666333066363434643332, 0x62333265376465366434663334303666, '周期报告', 'cycleReport', 1, '/health/cycleReport/', '/health/cycleReport/index.vue', 'icon-park-outline:bubble-chart', 0, 0, 1, '2026-04-12 14:23:44', '2026-04-12 15:17:30');
INSERT INTO `tl_sys_permission` VALUES (0x39336563333136393530313234646266, NULL, '付费管理', 'fee', 1, '/fee', '', 'icon-park-outline:financing-one', 3, 0, 0, '2026-04-18 23:02:02', NULL);
INSERT INTO `tl_sys_permission` VALUES (0x39613161323061623165333134343632, 0x61336639363363613435303637643537, '角色管理', 'roleManage', 1, '/system/role', '/system/role/index.vue', 'carbon:collaborate', 3, 0, 0, '2026-04-12 01:10:03', '2026-04-13 22:22:28');
INSERT INTO `tl_sys_permission` VALUES (0x39643231393435366430643434613634, 0x65613834313134626630343434333236, '账号管理', 'accountManage', 1, '/identity/account', '/identity/account/index.vue', 'icon-park-outline:women', 1, 0, 0, '2026-04-13 19:55:26', '2026-04-28 21:42:47');
INSERT INTO `tl_sys_permission` VALUES (0x39646331636530343334613834653330, 0x33343638363765303232623534363461, '文档库管理', 'docManage', 1, '/ai/doc', '/ai/doc/index.vue', 'icon-park-outline:shorts', 9, 0, 0, '2026-04-12 22:58:58', '2026-04-15 19:53:16');
INSERT INTO `tl_sys_permission` VALUES (0x39656339303962373366373034346531, 0x33343638363765303232623534363461, '会话管理', 'sessionManage', 1, '/ai/session', '/ai/session/index.vue', 'icon-park-outline:rattle-one', 7, 0, 0, '2026-04-12 23:03:46', '2026-04-14 23:19:00');
INSERT INTO `tl_sys_permission` VALUES (0x61313938386330633732326562626133, NULL, '工作台', 'workbench', 1, '/workbench', '/workbench/index.vue', 'icon-park-outline:handle-round', 1, 0, 0, '2026-04-11 20:56:13', '2026-04-13 21:43:09');
INSERT INTO `tl_sys_permission` VALUES (0x61336561663665623061633739313733, 0x61336639363363613435303637643537, '用户管理', 'accountSetting', 1, '/system/account', '/system/account/index.vue', 'icon-park-outline:every-user', 2, 0, 0, '2026-04-11 20:56:13', '2026-04-13 21:42:05');
INSERT INTO `tl_sys_permission` VALUES (0x61336639363363613435303637643537, NULL, '系统设置', 'system', 1, '/system', '', 'icon-park-outline:setting', 99, 0, 0, '2026-04-11 20:56:13', '2026-04-13 21:41:11');
INSERT INTO `tl_sys_permission` VALUES (0x61623261653736343737626134313130, 0x33626131656462323062643334383634, '运动管理', 'exerciseManage', 1, '/wisdom/exercise', '/wisdom/exercise/index.vue', 'icon-park-outline:baby-one', 0, 0, 0, '2026-04-12 14:55:38', '2026-04-12 14:56:51');
INSERT INTO `tl_sys_permission` VALUES (0x62303262323065323432666234656336, 0x33626131656462323062643334383634, '节气管理', 'solarManage', 1, '/wisdom/solarterm', '/wisdom/solarterm/index.vue', 'icon-park-outline:graphic-stitching-three', 0, 0, 0, '2026-04-12 23:05:05', '2026-04-14 23:54:53');
INSERT INTO `tl_sys_permission` VALUES (0x62333265376465366434663334303666, NULL, '方案管理', 'plan', 1, '/plan', '', 'icon-park-outline:health', 3, 0, 0, '2026-04-12 10:20:07', '2026-04-13 23:40:30');
INSERT INTO `tl_sys_permission` VALUES (0x62343064356539383062386334353034, 0x39336563333136393530313234646266, '签到奖励', 'checkinReward', 1, '/fee/checkinReward', '/fee/checkinReward/index.vue', 'icon-park-outline:mark', 0, 0, 1, '2026-04-12 14:21:02', '2026-04-19 23:49:02');
INSERT INTO `tl_sys_permission` VALUES (0x62363034656662656338663934313039, 0x33626131656462323062643334383634, '穴位管理', 'acupointManage', 1, '/wisdom/acupoint', '/wisdom/acupoint/index.vue', 'carbon:heat-map-02', 0, 0, 0, '2026-04-12 10:23:30', '2026-04-12 15:01:20');
INSERT INTO `tl_sys_permission` VALUES (0x62366264373731626539613134333235, 0x39336563333136393530313234646266, '会员成长配置', 'growthLevel', 1, '/fee/growthLevel', '/fee/growthLevel/index.vue', 'icon-park-outline:user-to-user-transmission', 8, 0, 0, '2026-04-20 15:11:27', '2026-04-20 15:21:15');
INSERT INTO `tl_sys_permission` VALUES (0x62383064366466656131326334393739, 0x62333265376465366434663334303666, '分享记录', 'shareRecord', 1, '/health/shareRecord/', '/health/shareRecord/index.vue', 'icon-park-outline:cycle-one', 0, 0, 1, '2026-04-12 14:24:10', '2026-04-12 15:17:30');
INSERT INTO `tl_sys_permission` VALUES (0x62623537326632653965393663363333, 0x61336639363363613435303637643537, '菜单设置', 'menuManage', 1, '/system/menu', '/system/menu/index.vue', 'icon-park-outline:application-menu', 4, 0, 0, '2026-04-11 20:56:13', '2026-04-13 22:22:15');
INSERT INTO `tl_sys_permission` VALUES (0x63323864613937376464613334373232, 0x39336563333136393530313234646266, '积分记录', 'pointsManage', 1, '/fee/points', '/fee/points/index.vue', 'icon-park-outline:wallet-two', 4, 0, 0, '2026-04-12 14:18:50', '2026-04-19 23:52:11');
INSERT INTO `tl_sys_permission` VALUES (0x63326639623538313231393534376535, 0x39336563333136393530313234646266, '会员套餐', 'member', 1, '/fee/member', '/fee/member/index.vue', 'icon-park-outline:user-to-user-transmission', 1, 0, 0, '2026-04-18 23:13:27', '2026-04-20 00:25:36');
INSERT INTO `tl_sys_permission` VALUES (0x63333561316464613831343034623039, 0x39336563333136393530313234646266, '首页轮播', 'homeBanner', 1, '/fee/banner', '/fee/banner/index.vue', 'icon-park-outline:inclusive-gateway', 10, 0, 0, '2026-05-01 21:18:27', '2026-05-01 21:18:34');
INSERT INTO `tl_sys_permission` VALUES (0x63346437343738633365323734373436, 0x62333265376465366434663334303666, '方案管理', 'planManage', 1, '/plan/plan', '/plan/plan/index.vue', 'icon-park-outline:graphic-stitching', 0, 0, 0, '2026-04-12 10:25:55', '2026-04-13 23:41:34');
INSERT INTO `tl_sys_permission` VALUES (0x63393730656630636364383534333063, 0x62333265376465366434663334303666, '调整记录', 'adjustmentManage', 1, '/health/adjustment/', '/health/adjustment/index.vue', 'icon-park-outline:loading', 0, 0, 1, '2026-04-12 14:23:16', '2026-04-12 15:17:30');
INSERT INTO `tl_sys_permission` VALUES (0x63613163396436333362383734633331, 0x33343638363765303232623534363461, '模型提供商管理', 'modelProvider', 1, '/ai/model-provider', '/ai/model-provider/index.vue', 'icon-park-outline:outbound', 1, 0, 0, '2026-04-14 18:25:28', '2026-04-14 23:17:55');
INSERT INTO `tl_sys_permission` VALUES (0x64303732303262383630623034393834, 0x62333265376465366434663334303666, '方案任务', 'taskManage', 1, '/health/task', '/health/task/index.vue', 'icon-park-outline:radar-two', 0, 0, 1, '2026-04-12 14:22:10', '2026-04-12 15:17:30');
INSERT INTO `tl_sys_permission` VALUES (0x64323062326663326661613234393964, 0x33626131656462323062643334383634, '穴位组合', 'acupointCombine', 1, '/wisdom/acupointCombine', '/wisdom/acupointCombine/index.vue', 'icon-park-outline:api-app', 0, 0, 0, '2026-04-12 15:05:35', '2026-04-13 00:20:29');
INSERT INTO `tl_sys_permission` VALUES (0x64656664313839383966393034373964, 0x39336563333136393530313234646266, '积分规则', 'pointsRule', 1, '/fee/pointsRule', '/fee/pointsRule/index.vue', 'icon-park-outline:financing-one', 3, 0, 0, '2026-04-19 23:51:58', NULL);
INSERT INTO `tl_sys_permission` VALUES (0x65613834313134626630343434333236, NULL, '身份管理', 'identity', 1, '/identity', '', 'icon-park-outline:avatar', 2, 0, 0, '2026-04-13 19:52:27', '2026-04-13 21:50:09');
INSERT INTO `tl_sys_permission` VALUES (0x66313763336532626535353934393661, 0x39336563333136393530313234646266, '积分商品', 'pointsGoods', 1, '/fee/pointsGoods', '/fee/pointsGoods/index.vue', 'icon-park-outline:game-emoji', 5, 0, 0, '2026-04-18 23:14:24', '2026-04-19 23:52:17');
INSERT INTO `tl_sys_permission` VALUES (0x66313931393033383932326434373265, 0x62333265376465366434663334303666, '方案广场', 'squareManage', 1, '/plan/square', '/plan/square/index.vue', 'icon-park-outline:pure-natural', 0, 0, 0, '2026-04-12 10:29:47', '2026-04-13 23:41:15');
INSERT INTO `tl_sys_permission` VALUES (0x66616465333132306435313734643530, 0x33343638363765303232623534363461, '端点配置', 'providerEndpoint', 1, '/ai/provider-endpoint', '/ai/provider-endpoint/index.vue', 'icon-park-outline:camera-four', 2, 0, 0, '2026-04-14 18:44:47', '2026-04-14 23:18:00');

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
-- Records of tl_sys_role
-- ----------------------------
INSERT INTO `tl_sys_role` VALUES (0x33623464633265356432326134323364, '测试角色1', 'test1', '测试角色1', 0, 0, '2026-04-12 01:23:33', '2026-04-13 17:10:23');
INSERT INTO `tl_sys_role` VALUES (0x35313464376262373462646365633233, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 0, 0, '2026-04-11 01:17:43', '2026-04-13 17:20:53');

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
-- Records of tl_sys_role_permission
-- ----------------------------
INSERT INTO `tl_sys_role_permission` VALUES (0x30303636313064613937333934373865, 0x35313464376262373462646365633233, 0x38653530356666333066363434643332, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x30313639313666393037393434643265, 0x35313464376262373462646365633233, 0x61313938386330633732326562626133, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x30633863626635643562393134376166, 0x35313464376262373462646365633233, 0x62303262323065323432666234656336, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x30653538363563323530383034323034, 0x35313464376262373462646365633233, 0x61336639363363613435303637643537, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x31303064633031303032666534356238, 0x35313464376262373462646365633233, 0x33626131656462323062643334383634, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x31306638386565643136343834303161, 0x35313464376262373462646365633233, 0x62366264373731626539613134333235, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x31613335643533616661623634653134, 0x35313464376262373462646365633233, 0x39643231393435366430643434613634, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x31666131653662383532663234613135, 0x35313464376262373462646365633233, 0x62623537326632653965393663363333, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x32353836613035613366373034366261, 0x35313464376262373462646365633233, 0x64323062326663326661613234393964, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x32623666653066343364316534303531, 0x35313464376262373462646365633233, 0x62363034656662656338663934313039, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x32653664653835656632323934323433, 0x35313464376262373462646365633233, 0x37323231633332316437383534613130, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x33363336613461636436393034316637, 0x35313464376262373462646365633233, 0x63346437343738633365323734373436, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x34363337623465666265366534383166, 0x35313464376262373462646365633233, 0x33343638363765303232623534363461, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x35363831353439343936643034346233, 0x35313464376262373462646365633233, 0x30383933336637386432393334316631, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x35653465643134356465616234656635, 0x35313464376262373462646365633233, 0x30383266623363346435313234386461, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x37353765643135363433343634613036, 0x35313464376262373462646365633233, 0x66313931393033383932326434373265, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x37386163313939393132643034326262, 0x35313464376262373462646365633233, 0x62376233653233383635316435363163, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x37623430666463343635383534346334, 0x35313464376262373462646365633233, 0x34363637656566383532636434643936, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x38313263353761653735613334613036, 0x35313464376262373462646365633233, 0x36633365393966376334613034613236, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x38343434646635653838613934666461, 0x35313464376262373462646365633233, 0x66313763336532626535353934393661, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x38383232393137306261393434333035, 0x35313464376262373462646365633233, 0x65613834313134626630343434333236, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x38613033643931633665303834393830, 0x35313464376262373462646365633233, 0x34303939326266313939396134383666, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x38656561376561333437663234643532, 0x35313464376262373462646365633233, 0x62343064356539383062386334353034, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x38663639303137663539636134383937, 0x35313464376262373462646365633233, 0x66616465333132306435313734643530, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39303462373831623738646634653366, 0x35313464376262373462646365633233, 0x39656339303962373366373034346531, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39303861616565626335393434636439, 0x35313464376262373462646365633233, 0x63613163396436333362383734633331, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39343964626234336162633634613832, 0x35313464376262373462646365633233, 0x30646433613230663535643634656433, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39373033346266663332636534643835, 0x35313464376262373462646365633233, 0x64303732303262383630623034393834, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39393235313833646361333734353033, 0x35313464376262373462646365633233, 0x64656664313839383966393034373964, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39633261643432656633636234366163, 0x35313464376262373462646365633233, 0x31346434373363313163373963316239, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39636162343363643235616534336361, 0x35313464376262373462646365633233, 0x34646237633332633834633634316664, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x39636638646633306335633234613135, 0x35313464376262373462646365633233, 0x63333561316464613831343034623039, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x61383133363532316335303034363762, 0x35313464376262373462646365633233, 0x63323864613937376464613334373232, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x62326537333166623532373934306235, 0x35313464376262373462646365633233, 0x36643237316639643037626434636534, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x62653630623234666131383534373738, 0x35313464376262373462646365633233, 0x38373530666663346666623334333666, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x62663135356238373965363834326436, 0x35313464376262373462646365633233, 0x30333732656465643934656434663136, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x63346431653664633734306334373739, 0x35313464376262373462646365633233, 0x63393730656630636364383534333063, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x63653836633561313264343134636434, 0x35313464376262373462646365633233, 0x63326639623538313231393534376535, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x64313038363765363161333634373261, 0x35313464376262373462646365633233, 0x62333265376465366434663334303666, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x64363830376663303766643334303732, 0x35313464376262373462646365633233, 0x39646331636530343334613834653330, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x64396162643765333433663334623038, 0x35313464376262373462646365633233, 0x37313761396364376364633534316436, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x64613866613037363264343234623366, 0x35313464376262373462646365633233, 0x34636365646665373737636134353061, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x64623961333130356437366334303863, 0x35313464376262373462646365633233, 0x36316337623232333433646334393464, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x65316539636536636536316334376438, 0x35313464376262373462646365633233, 0x35383739643566393533383034663236, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x65323461363736306237636434333432, 0x35313464376262373462646365633233, 0x61336561663665623061633739313733, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x65326663363061653136343434343763, 0x35313464376262373462646365633233, 0x62383064366466656131326334393739, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x65363066333135663264653634636431, 0x35313464376262373462646365633233, 0x39613161323061623165333134343632, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x65656332333137383130313334636134, 0x35313464376262373462646365633233, 0x39336563333136393530313234646266, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x66333464373235363764343534373539, 0x35313464376262373462646365633233, 0x61623261653736343737626134313130, '2026-05-01 21:30:35');
INSERT INTO `tl_sys_role_permission` VALUES (0x66373733616238303334376334326565, 0x35313464376262373462646365633233, 0x30663332333365346463313434376362, '2026-05-01 21:30:35');

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
-- Records of tl_sys_user
-- ----------------------------
INSERT INTO `tl_sys_user` VALUES (0x62366137346136393966663039326263, 'admin', '$2b$10$Sac3v4pmQOjMt7Y2QUXC8eHRvKK0FELk/d0OUFD07L9LGcWLbD74q', '系统管理员', NULL, NULL, 0x35313464376262373462646365633233, NULL, '2026-05-01 20:50:54', '0:0:0:0:0:0:0:1', 0, 0, '2026-04-11 01:17:43', '2026-04-11 01:19:34');
INSERT INTO `tl_sys_user` VALUES (0x65616665346630613164353234653564, 'test123', '$2a$10$4gmOFfwQkvQeewSNRLMqTeNINocdswmgKkc1EjooMriMTn2RY5mDa', '', '', '', 0x33623464633265356432326134323364, '', '2026-04-12 01:28:04', '10.10.0.100', 0, 0, '2026-04-12 00:29:21', '2026-04-12 01:27:57');

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
-- Records of tl_wis_acupoint
-- ----------------------------
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303031, '中府', 'zhongfu', NULL, 1, 'hand_taiyin', '手太阴肺经', '胸部，锁骨下，云门下1寸，平第1肋间隙', NULL, NULL, '宣肺理气，止咳平喘', '咳嗽，气喘、胸满痛、肩背痛', '向外斜刺0.5-0.8寸', '用拇指指腹按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303032, '云门', 'yunmen', NULL, 1, 'hand_taiyin', '手太阴肺经', '胸部，锁骨下窝中央，肩胛骨喙突内缘', NULL, NULL, '清肺理气，止咳平喘', '咳嗽，气喘、胸痛、肩痛', '向外斜刺0.5-0.8寸', '按揉或艾灸，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303033, '天府', 'tianfu', NULL, 1, 'hand_taiyin', '手太阴肺经', '上臂内侧，肱二头肌桡侧缘', NULL, NULL, '调肺气，清热凉血', '气喘、鼻衄、上臂痛', '直刺0.5-1寸', '按揉1-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303034, '侠白', 'xiabai', NULL, 1, 'hand_taiyin', '手太阴肺经', '上臂内侧，肱二头肌桡侧缘，天府下1寸', NULL, NULL, '理肺和胃，调气止痛', '咳嗽、气短、干呕、上臂内侧痛', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303035, '尺泽', 'chize', NULL, 1, 'hand_taiyin', '手太阴肺经', '肘横纹中，肱二头肌腱桡侧凹陷处', NULL, NULL, '清肺泻火，止咳平喘', '咳嗽、咳血、咽喉肿痛、肘臂痛', '直刺0.8-1.2寸，或点刺放血', '掐按或艾灸，每次3-5分钟', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303036, '孔最', 'kongzui', NULL, 1, 'hand_taiyin', '手太阴肺经', '前臂掌面桡侧，尺泽与太渊连线上', NULL, NULL, '清热解表，润肺止血', '咳嗽、咳血、鼻衄、痔疾', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303037, '列缺', 'lieque', NULL, 1, 'hand_taiyin', '手太阴肺经', '前臂桡侧缘，桡骨茎突上方', NULL, NULL, '宣肺解表，通经活络', '头痛、咳嗽、咽喉痛、手腕痛', '向上斜刺0.5-0.8寸', '掐按或艾灸，每次3-5分钟', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303038, '经渠', 'jingqu', NULL, 1, 'hand_taiyin', '手太阴肺经', '前臂掌面桡侧，桡骨茎突与腕横纹之间', NULL, NULL, '宣肺解表，止咳平喘', '咳嗽、咽喉肿痛、手腕痛', '直刺0.3-0.5寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303039, '太渊', 'taiyuan', NULL, 1, 'hand_taiyin', '手太阴肺经', '腕掌侧横纹桡侧，桡动脉搏动处', NULL, NULL, '补肺益气，止咳化痰', '咳嗽，气喘、咽喉痛、手腕痛', '直刺0.3-0.5寸', '艾灸或按揉，每次3-5分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303061, '鱼际', 'yuji', NULL, 1, 'hand_taiyin', '手太阴肺经', '手拇指本节后凹陷处，约当第1掌骨中点', NULL, NULL, '清肺泻火，利咽止咳', '咳嗽、咳血、咽喉肿痛、发热', '直刺0.5-0.8寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303062, '少商', 'shaoshang', NULL, 1, 'hand_taiyin', '手太阴肺经', '拇指末节桡侧，指甲角旁开0.1寸', NULL, NULL, '清肺利咽，开窍醒神', '咽喉肿痛、咳嗽、鼻衄、发热', '浅刺0.1寸，或点刺放血', '掐按或放血，每次1-2分钟', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 11, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303063, '商阳', 'shangyang', NULL, 1, 'hand_yangming', '手阳明大肠经', '食指末节桡侧，指甲角旁开0.1寸', NULL, NULL, '清热泻火，开窍醒神', '咽喉肿痛、发热、牙痛、手指麻木', '浅刺0.1寸，或点刺放血', '掐按或放血，每次1-2分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303064, '二间', 'erjian', NULL, 1, 'hand_yangming', '手阳明大肠经', '微握拳，食指本节前桡侧凹陷处', NULL, NULL, '清热泻火，疏风利窍', '牙痛、咽喉肿痛、鼻衄、目昏', '直刺0.2-0.3寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303065, '三间', 'sanjian', NULL, 1, 'hand_yangming', '手阳明大肠经', '微握拳，食指本节后凹陷处', NULL, NULL, '清热泻火，利咽明目', '牙痛、咽喉肿痛、腹胀、手指痛', '直刺0.5-0.8寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303066, '合谷', 'hegu', NULL, 1, 'hand_yangming', '手阳明大肠经', '手背，第一、二掌骨之间，约平第二掌骨中点', NULL, NULL, '疏风解表，通络镇痛', '头痛、牙痛、目赤肿痛、咽喉痛、面瘫', '直刺0.5-1寸', '按揉3-5分钟，力度适中（孕妇禁按）', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303130, '阳溪', 'yangxi', NULL, 1, 'hand_yangming', '手阳明大肠经', '腕背横纹桡侧，手拇指向上翘起时凹陷处', NULL, NULL, '清热散风，舒筋活络', '头痛、牙痛、目赤肿痛、手腕痛', '直刺0.3-0.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303131, '偏历', 'pianli', NULL, 1, 'hand_yangming', '手阳明大肠经', '前臂，阳溪与曲池连线上', NULL, NULL, '清热利尿，通经活络', '耳鸣、鼻衄、牙痛、喉痛、上肢痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303132, '温溜', 'wenliu', NULL, 1, 'hand_yangming', '手阳明大肠经', '前臂，阳溪与曲池连线上', NULL, NULL, '清热解毒，调理肠腑', '头痛、咽喉肿痛、肠鸣腹痛', '直刺0.5-0.8寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303133, '下廉', 'xialian', NULL, 1, 'hand_yangming', '手阳明大肠经', '前臂，阳溪与曲池连线上', NULL, NULL, '疏经通络，调理肠胃', '头痛、眩晕、腹痛、肘臂痛', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303134, '上廉', 'shanglian', NULL, 1, 'hand_yangming', '手阳明大肠经', '前臂，阳溪与曲池连线上', NULL, NULL, '疏经通络，调理肠胃', '头痛、肠鸣腹痛、上肢麻木', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303135, '手三里', 'shousanli', NULL, 1, 'hand_yangming', '手阳明大肠经', '前臂，阳溪与曲池连线上', NULL, NULL, '通经活络，调理脾胃', '腹痛、腹泻、牙痛、上肢麻痛', '直刺0.8-1.2寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303136, '曲池', 'quchi', NULL, 1, 'hand_yangming', '手阳明大肠经', '肘横纹外侧端，屈肘时肘横纹头凹陷处', NULL, NULL, '清热解表，调和气血', '发热、高血压、皮肤病、肘痛', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 11, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303137, '肘髎', 'zhouliao', NULL, 1, 'hand_yangming', '手阳明大肠经', '肘外侧，肱骨外上髁上缘', NULL, NULL, '舒筋活络，消肿止痛', '肘臂痛、网球肘', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 12, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303138, '手五里', 'shouwuli', NULL, 1, 'hand_yangming', '手阳明大肠经', '上臂，曲池上3寸', NULL, NULL, '疏经活络，散结消肿', '肘臂痛、瘰疬', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 13, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303139, '臂臑', 'binao', NULL, 1, 'hand_yangming', '手阳明大肠经', '上臂，曲池上7寸，三角肌下端', NULL, NULL, '通络明目，散结消肿', '肩臂痛、瘰疬、目疾', '直刺0.5-1寸，或向上斜刺', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303161, '肩髃', 'jianyu', NULL, 1, 'hand_yangming', '手阳明大肠经', '肩部，三角肌上，肩峰与肱骨大结节之间', NULL, NULL, '疏经活络，散风止痛', '肩臂痛、上肢不遂、瘾疹', '直刺0.8-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 15, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303162, '巨骨', 'jugu', NULL, 1, 'hand_yangming', '手阳明大肠经', '肩上部，锁骨肩峰端与肩胛冈之间', NULL, NULL, '通经活络，散结消肿', '肩臂痛、上肢不遂', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 16, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303163, '天鼎', 'tianding', NULL, 1, 'hand_yangming', '手阳明大肠经', '颈部，扶突穴直下，胸锁乳突肌后缘', NULL, NULL, '清热利咽，理气散结', '咽喉肿痛、暴喑、瘰疬', '直刺0.3-0.5寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 17, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303164, '扶突', 'futu', NULL, 1, 'hand_yangming', '手阳明大肠经', '颈外侧，结喉旁开3寸', NULL, NULL, '清热利咽，理气降逆', '咳嗽，气喘、咽喉肿痛、暴喑', '直刺0.5-0.8寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 18, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303165, '口禾髎', 'kouheliao', NULL, 1, 'hand_yangming', '手阳明大肠经', '面部，鼻翼外缘直下，平鼻唇沟', NULL, NULL, '祛风开窍，疏经活络', '鼻塞、鼻衄、口歪', '直刺0.3-0.5寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 19, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303166, '迎香', 'yingxiang', NULL, 1, 'hand_yangming', '手阳明大肠经', '面部，鼻翼外缘中点旁开0.5寸', NULL, NULL, '祛风通窍，疏经活络', '鼻塞、鼻衄、口歪、面痒', '直刺0.3-0.5寸，或斜刺', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 20, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303230, '承泣', 'chengqi', NULL, 1, 'foot_yangming', '足阳明胃经', '面部，眼球与眶下缘之间', NULL, NULL, '散风清热，明目止泪', '目赤肿痛，流泪、夜盲、口眼歪斜', '直刺0.3-0.5寸', '按揉1-2分钟，力度轻柔', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303231, '四白', 'sibai', NULL, 1, 'foot_yangming', '足阳明胃经', '面部，眶下孔凹陷处', NULL, NULL, '祛风明目，疏经活络', '目赤肿痛、目眩、口眼歪斜', '直刺0.2-0.3寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303232, '地仓', 'dicang', NULL, 1, 'foot_yangming', '足阳明胃经', '面部，口角旁开0.4寸', NULL, NULL, '祛风通络，活血止涎', '口歪，流涎、牙痛', '斜刺或平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303233, '颊车', 'jiache', NULL, 1, 'foot_yangming', '足阳明胃经', '面部，咀嚼时肌肉隆起处', NULL, NULL, '祛风清热，舒筋活络', '牙痛、颊肿、口歪、面痛', '直刺0.3-0.5寸，或平刺', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303234, '下关', 'xiaguan', NULL, 1, 'foot_yangming', '足阳明胃经', '面部，颧弓与下颌切迹之间', NULL, NULL, '清热消肿，止痛聪耳', '牙痛、颊肿、口歪、耳聋', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303235, '头维', 'touwei', NULL, 1, 'foot_yangming', '足阳明胃经', '额角发际上0.5寸', NULL, NULL, '祛风泄火，止痛明目', '头痛、目眩、口痛', '平刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303236, '人迎', 'renying', NULL, 1, 'foot_yangming', '足阳明胃经', '颈部，平喉结旁开1.5寸', NULL, NULL, '清热利咽，理气降逆', '咽喉肿痛，气喘、瘰疬', '直刺0.3-0.5寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303237, '乳根', 'rugen', NULL, 1, 'foot_yangming', '足阳明胃经', '胸部，第5肋间隙', NULL, NULL, '宣肺理气，通乳化瘀', '咳嗽、胸痛、乳少', '斜刺或平刺0.5-0.8寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 18, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303238, '天枢', 'tianshu', NULL, 1, 'foot_yangming', '足阳明胃经', '腹部，脐旁开2寸', NULL, NULL, '调理肠胃，止泻通便', '腹痛、腹胀、便秘、泄泻', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 25, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303239, '水道', 'shuidao', NULL, 1, 'foot_yangming', '足阳明胃经', '下腹部，脐下3寸旁开2寸', NULL, NULL, '通利水道，调理下焦', '小腹胀满、小便不利、痛经', '直刺1-1.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 28, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303261, '归来', 'guilai', NULL, 1, 'foot_yangming', '足阳明胃经', '下腹部，脐下4寸旁开2寸', NULL, NULL, '调经止带，止痛升提', '小腹痛、疝气、闭经', '直刺1-1.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 29, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303262, '足三里', 'zusanli', NULL, 1, 'foot_yangming', '足阳明胃经', '小腿外侧，犊鼻下3寸，胫骨前嵴外一横指', NULL, NULL, '调理脾胃，补中益气', '胃痛、腹胀、呕吐、体虚乏力', '直刺1-2寸', '按揉3-5分钟，或艾灸', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 36, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303263, '上巨虚', 'shangjuxu', NULL, 1, 'foot_yangming', '足阳明胃经', '小腿外侧，犊鼻下6寸', NULL, NULL, '调理肠胃，通络止痛', '肠鸣、腹痛、泄泻、下肢痿痹', '直刺1-1.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 37, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303264, '条口', 'tiaokou', NULL, 1, 'foot_yangming', '足阳明胃经', '小腿外侧，犊鼻下8寸', NULL, NULL, '舒筋活络，调理气血', '膝痛、下肢痿痹', '直刺1-1.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 38, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303265, '下巨虚', 'xiajuxu', NULL, 1, 'foot_yangming', '足阳明胃经', '小腿外侧，犊鼻下9寸', NULL, NULL, '调理肠胃，通络止痛', '小腹痛、泄泻、下肢痿痹', '直刺1-1.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 39, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303266, '丰隆', 'fenglong', NULL, 1, 'foot_yangming', '足阳明胃经', '小腿外侧，外踝尖上8寸', NULL, NULL, '化痰止咳，和胃降逆', '头痛、眩晕、咳嗽痰多', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 40, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303330, '解溪', 'jiexi', NULL, 1, 'foot_yangming', '足阳明胃经', '足背，踝关节横纹中央', NULL, NULL, '清胃降火，舒筋活络', '头痛、眩晕、腹胀、便秘', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 41, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303331, '内庭', 'neiting', NULL, 1, 'foot_yangming', '足阳明胃经', '足背，第二、三趾间缝纹端', NULL, NULL, '清胃泻火，消肿止痛', '牙痛、咽喉肿痛、鼻衄、腹胀', '直刺0.3-0.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 44, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303332, '厉兑', 'lidui', NULL, 1, 'foot_yangming', '足阳明胃经', '足趾，第二趾末节外侧', NULL, NULL, '清热泻火，安神开窍', '牙痛、咽喉肿痛、鼻衄、梦魇', '浅刺0.1寸', '掐按1分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 45, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303333, '隐白', 'yinbai', NULL, 1, 'foot_taiyin', '足太阴脾经', '足大趾末节内侧，趾甲角旁开0.1寸', NULL, NULL, '健脾止血，宁神开窍', '腹胀、便血、崩漏、梦魇', '浅刺0.1寸，或艾灸', '掐按或艾灸，每次3-5分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303334, '公孙', 'gongsun', NULL, 1, 'foot_taiyin', '足太阴脾经', '足内侧，第一跖骨基底前下方', NULL, NULL, '健脾化湿，和胃止痛', '胃痛、呕吐、腹痛、泄泻', '直刺0.6-1.2寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303335, '三阴交', 'sanyinjiao', NULL, 1, 'foot_taiyin', '足太阴脾经', '内踝尖上3寸，胫骨内侧后缘', NULL, NULL, '健脾益血，调补肝肾', '妇科病、男科病、失眠、腹胀', '直刺1-1.5寸', '按揉3-5分钟（孕妇禁按）', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303336, '地机', 'diji', NULL, 1, 'foot_taiyin', '足太阴脾经', '内踝尖与阴陵泉连线上', NULL, NULL, '健脾利湿，调经止痛', '腹痛、泄泻、月经不调、痛经', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303337, '阴陵泉', 'yinlingquan', NULL, 1, 'foot_taiyin', '足太阴脾经', '胫骨内侧髁后下方凹陷处', NULL, NULL, '健脾利湿，通络止痛', '腹胀、泄泻、水肿、小便不利', '直刺1-2寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303338, '血海', 'xuehai', NULL, 1, 'foot_taiyin', '足太阴脾经', '屈膝，髌骨内上缘上2寸', NULL, NULL, '健脾化湿，调经止血', '月经不调、崩漏、湿疹、股内侧痛', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303339, '大横', 'daheng', NULL, 1, 'foot_taiyin', '足太阴脾经', '腹部，脐旁开4寸', NULL, NULL, '调理肠胃，止痛通便', '腹痛、泄泻、便秘', '直刺1-1.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 15, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303361, '大包', 'dabao', NULL, 1, 'foot_taiyin', '足太阴脾经', '侧胸部，腋中线上第6肋间隙', NULL, NULL, '宽胸利胁，健脾益气', '胸胁痛、咳嗽、气喘、全身疼痛', '斜刺或平刺0.5-0.8寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 21, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303362, '极泉', 'jiquan', NULL, 1, 'hand_shaoyin', '手少阴心经', '腋窝中央，腋动脉搏动处', NULL, NULL, '宽胸理气，通络止痛', '心痛、胁痛、咽干、肘臂冷痛', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303363, '少海', 'shaohai', NULL, 1, 'hand_shaoyin', '手少阴心经', '肘横纹内侧端，肱骨内上髁前缘', NULL, NULL, '清心泻火，舒筋止痛', '心痛、肘臂痛、瘰疬', '直刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303364, '通里', 'tongli', NULL, 1, 'hand_shaoyin', '手少阴心经', '前臂掌侧，尺侧腕屈肌腱桡侧', NULL, NULL, '宁心安神，通窍利音', '心悸、暴喑、舌强不语', '直刺0.3-0.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303365, '阴郄', 'yinxi', NULL, 1, 'hand_shaoyin', '手少阴心经', '前臂掌侧，尺侧腕屈肌腱桡侧', NULL, NULL, '宁心安神，固表止汗', '心痛、盗汗、吐血、鼻衄', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303366, '神门', 'shenmen', NULL, 1, 'hand_shaoyin', '手少阴心经', '腕掌侧横纹尺侧端', NULL, NULL, '宁心安神，通络止痛', '心悸、失眠、健忘、痴呆', '直刺0.3-0.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303430, '少府', 'shaofu', NULL, 1, 'hand_shaoyin', '手少阴心经', '手掌面，第4、5掌骨之间', NULL, NULL, '清心泻火，调理心肾', '心悸、胸痛、小便不利、阴痒', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303431, '少冲', 'shaochong', NULL, 1, 'hand_shaoyin', '手少阴心经', '小指桡侧，指甲角旁开0.1寸', NULL, NULL, '泻热醒神，开窍醒神', '心悸、心痛、昏迷、热病', '浅刺0.1寸，或点刺放血', '掐按或放血，每次1-2分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303432, '少泽', 'shaoze', NULL, 1, 'hand_taiyang', '手太阳小肠经', '小指尺侧，指甲角旁开0.1寸', NULL, NULL, '清热醒神，利咽通乳', '头痛、咽喉肿痛、乳少、热病', '浅刺0.1寸，或点刺放血', '掐按或放血，每次1-2分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303433, '后溪', 'houxi', NULL, 1, 'hand_taiyang', '手太阳小肠经', '手尺侧，小指本节后凹陷处', NULL, NULL, '清心安神，通络止痛', '头项强痛、腰背痛、耳聋、疟疾', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303434, '腕骨', 'wangu', NULL, 1, 'hand_taiyang', '手太阳小肠经', '手尺侧，腕背横纹上', NULL, NULL, '清心安神，散风通络', '头项强痛、耳鸣、黄疸、指腕痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303435, '养老', 'yanglao', NULL, 1, 'hand_taiyang', '手太阳小肠经', '前臂背面尺侧，尺骨小头近端', NULL, NULL, '清肝明目，舒筋活络', '目视不明、肩背肘臂痛', '直刺0.5-0.8寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303436, '小海', 'xiaohai', NULL, 1, 'hand_taiyang', '手太阳小肠经', '肘内侧，肱骨内上髁与尺骨鹰嘴之间', NULL, NULL, '清心安神，舒筋止痛', '肘臂痛、头痛、癫痫', '直刺0.3-0.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303437, '肩贞', 'jianzhen', NULL, 1, 'hand_taiyang', '手太阳小肠经', '肩关节后下方，腋后纹头上1寸', NULL, NULL, '通络止痛，散结消肿', '肩臂痛、上肢不遂、耳鸣', '直刺1-1.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303438, '天宗', 'tianzong', NULL, 1, 'hand_taiyang', '手太阳小肠经', '肩胛部，冈下窝中央', NULL, NULL, '理气散结，舒筋活络', '肩胛痛、气喘、乳痈', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 12, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303439, '秉风', 'bingfeng', NULL, 1, 'hand_taiyang', '手太阳小肠经', '肩胛部，冈上窝中央', NULL, NULL, '舒筋活络，散风止痛', '肩胛痛、上肢不遂', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 13, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303461, '肩外俞', 'jianwaishu', NULL, 1, 'hand_taiyang', '手太阳小肠经', '背部，第1胸椎棘突下旁开3寸', NULL, NULL, '舒筋活络，散寒止痛', '肩背痛、咳嗽、气喘', '斜刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 15, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303462, '肩中俞', 'jianzhongshu', NULL, 1, 'hand_taiyang', '手太阳小肠经', '背部，第7颈椎棘突下旁开2寸', NULL, NULL, '宣肺解表，舒筋活络', '咳嗽、气喘、肩背痛', '斜刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 16, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303463, '天窗', 'tianchuang', NULL, 1, 'hand_taiyang', '手太阳小肠经', '颈外侧，胸锁乳突肌后缘', NULL, NULL, '清热利咽，通窍聪耳', '咽喉肿痛、暴喑、耳鸣', '直刺0.5-1寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 17, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303464, '天容', 'tianrong', NULL, 1, 'hand_taiyang', '手太阳小肠经', '颈外侧，胸锁乳突肌前缘', NULL, NULL, '清热利咽，消肿止痛', '咽喉肿痛、耳鸣、耳聋', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 18, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303465, '颧髎', 'quanliao', NULL, 1, 'hand_taiyang', '手太阳小肠经', '面部，目外眦直下，颧骨下缘', NULL, NULL, '清热消肿，止痛散风', '口眼歪斜、牙痛、颊肿', '直刺0.3-0.5寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 19, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303466, '听宫', 'tinggong', NULL, 1, 'hand_taiyang', '手太阳小肠经', '面部，耳屏前，张口时凹陷处', NULL, NULL, '聪耳开窍，安神止痛', '耳鸣、耳聋、牙痛', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 20, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303530, '涌泉', 'yongquan', NULL, 1, 'foot_shaoyin', '足少阴肾经', '足底，屈足卷趾时足底前部凹陷处', NULL, NULL, '滋阴降火，开窍醒神', '头痛、眩晕、失眠、便秘', '直刺0.5-1寸', '搓揉3-5分钟，或艾灸', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303531, '然谷', 'rangu', NULL, 1, 'foot_shaoyin', '足少阴肾经', '足内侧，足舟骨粗隆下方', NULL, NULL, '滋阴降火，清热利湿', '月经不调、遗精、咽喉肿痛', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303532, '太溪', 'taixi', NULL, 1, 'foot_shaoyin', '足少阴肾经', '足内侧，内踝后方凹陷处', NULL, NULL, '滋阴补肾，强腰聪耳', '头痛、眩晕、失眠、月经不调', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303533, '大钟', 'dazhong', NULL, 1, 'foot_shaoyin', '足少阴肾经', '足内侧，内踝后下方', NULL, NULL, '补肾安神，清热利尿', '咳嗽、气喘、便秘、痴呆', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303534, '照海', 'zhaohai', NULL, 1, 'foot_shaoyin', '足少阴肾经', '足内侧，内踝尖下方凹陷处', NULL, NULL, '滋阴清热，安神利咽', '失眠、咽喉肿痛、月经不调', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303535, '复溜', 'fuliu', NULL, 1, 'foot_shaoyin', '足少阴肾经', '小腿内侧，太溪上2寸', NULL, NULL, '补肾益阴，利水消肿', '水肿、盗汗、泄泻、腰膝痛', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303536, '阴谷', 'yingu', NULL, 1, 'foot_shaoyin', '足少阴肾经', '膝腘窝，半腱肌与半膜肌之间', NULL, NULL, '补肾培元，调经止带', '阳痿、崩漏、月经不调', '直刺1-1.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303537, '天池', 'tianchi', NULL, 1, 'hand_jueyin', '手厥阴心包经', '胸部，第4肋间隙，乳头外1寸', NULL, NULL, '宽胸理气，散结消肿', '胸闷、胁痛、咳嗽、气喘', '斜刺或平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303538, '天泉', 'tianquan', NULL, 1, 'hand_jueyin', '手厥阴心包经', '上臂内侧，肱二头肌中间', NULL, NULL, '宽胸理气，止痛安神', '心痛、咳嗽、胸胁胀痛', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303539, '曲泽', 'quze', NULL, 1, 'hand_jueyin', '手厥阴心包经', '肘横纹中，肱二头肌腱的尺侧', NULL, NULL, '清心泻火，除烦止呕', '心痛、胃痛、呕吐、热病', '直刺1-1.5寸，或点刺', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303561, '郄门', 'ximen', NULL, 1, 'hand_jueyin', '手厥阴心包经', '前臂掌侧，曲泽与大陵连线上', NULL, NULL, '宁心安神，清热止血', '心痛、呕吐、疔疮', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303562, '间使', 'jianshi', NULL, 1, 'hand_jueyin', '手厥阴心包经', '前臂掌侧，大陵上3寸', NULL, NULL, '宽胸理气，安神止呕', '心痛、呕吐、热病、癫痫', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303563, '内关', 'neiguan', NULL, 1, 'hand_jueyin', '手厥阴心包经', '前臂掌侧，腕横纹上2寸', NULL, NULL, '宁心安神，理气止痛', '心悸、失眠、胃痛、呕吐', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303564, '大陵', 'daling', NULL, 1, 'hand_jueyin', '手厥阴心包经', '腕掌侧横纹中央', NULL, NULL, '宁心安神，宽胸止痛', '心悸、失眠、胸胁痛、呕吐', '直刺0.3-0.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303565, '劳宫', 'laogong', NULL, 1, 'hand_jueyin', '手厥阴心包经', '手掌心，第二、三掌骨之间', NULL, NULL, '清心泻火，安神止呕', '心悸、失眠、中风、呕吐', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303566, '中冲', 'zhongchong', NULL, 1, 'hand_jueyin', '手厥阴心包经', '中指尖端中央', NULL, NULL, '开窍醒神，清心泻火', '昏迷、发热、中风、舌强不语', '浅刺0.1寸，或点刺放血', '掐按或放血，每次1-2分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303630, '关冲', 'guanchong', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '无名指尺侧，指甲角旁开0.1寸', NULL, NULL, '清热泻火，开窍醒神', '头痛、咽喉肿痛、热病', '浅刺0.1寸，或点刺放血', '掐按，每次1-2分钟', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303631, '液门', 'yemen', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '手背部，第四、五指间', NULL, NULL, '清热泻火，聪耳明目', '头痛、咽喉肿痛、疟疾', '直刺0.3-0.5寸', '按揉1-2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303632, '中渚', 'zhongzhu', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '手背部，无名指本节后凹陷处', NULL, NULL, '清热泻火，聪耳明目', '头痛、耳鸣、咽痛、肩背痛', '直刺0.3-0.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303633, '阳池', 'yangchi', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '腕背横纹中，指伸肌腱尺侧', NULL, NULL, '清热通络，调理三焦', '腕痛、耳鸣、消渴', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303634, '外关', 'waiguan', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '前臂背侧，腕背横纹上2寸', NULL, NULL, '清热解表，通络止痛', '头痛、耳鸣、发热、胁痛', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303635, '支沟', 'zhigou', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '前臂背侧，腕背横纹上3寸', NULL, NULL, '清热通便，疏肝利胁', '便秘、胁痛、热病、耳鸣', '直刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303636, '天井', 'tianjing', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '肘尖后上方1寸凹陷处', NULL, NULL, '清热化痰，聪耳止痛', '偏头痛、胁痛、颈项痛', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303637, '肩髎', 'jianliao', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '肩部，肩峰与肱骨大结节之间', NULL, NULL, '祛风除湿，舒筋活络', '肩臂痛、上肢不遂', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303638, '翳风', 'yifeng', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '耳垂后方，乳突与下颌角之间', NULL, NULL, '聪耳消肿，祛风通络', '耳鸣、耳聋、口眼歪斜', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 17, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303639, '角孙', 'jiaosun', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '头部，折耳郭向前，耳尖直上入发际', NULL, NULL, '清热消肿，散风止痛', '偏头痛、牙痛、耳鸣', '平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 20, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303661, '耳门', 'ermen', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '面部，耳屏上切迹前方', NULL, NULL, '聪耳开窍，消肿止痛', '耳鸣、耳聋、牙痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 21, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303662, '丝竹空', 'sizhukong', NULL, 1, 'hand_shaoyang', '手少阳三焦经', '面部，眉梢凹陷处', NULL, NULL, '清头明目，散风止痛', '头痛、眩晕、目赤肿痛', '平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 23, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303663, '听会', 'tinghui', NULL, 1, 'foot_shaoyang', '足少阳胆经', '面部，耳屏间切迹前方', NULL, NULL, '聪耳开窍，通络止痛', '耳鸣、耳聋、牙痛、口歪', '直刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303664, '上关', 'shangguan', NULL, 1, 'foot_shaoyang', '足少阳胆经', '面部，颧弓上缘', NULL, NULL, '聪耳开窍，止痛镇痉', '头痛、耳鸣、牙痛、口歪', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303665, '率谷', 'shuaigu', NULL, 1, 'foot_shaoyang', '足少阳胆经', '头部，耳尖直上入发际1.5寸', NULL, NULL, '清热息风，止痛镇痉', '头痛、眩晕、呕吐', '平刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303666, '阳白', 'yangbai', NULL, 1, 'foot_shaoyang', '足少阳胆经', '面部，眉上1寸', NULL, NULL, '清头明目，祛风泄火', '头痛、眩晕、目痛', '平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303730, '风池', 'fengchi', NULL, 1, 'foot_shaoyang', '足少阳胆经', '枕骨下，斜方肌上端凹陷处', NULL, NULL, '祛风解表，清头明目', '头痛、眩晕、失眠、鼻塞', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 20, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303731, '肩井', 'jianjing', NULL, 1, 'foot_shaoyang', '足少阳胆经', '肩部，大椎与肩峰连线中点', NULL, NULL, '祛风活络，消肿止痛', '肩背痛、颈项强痛、哺乳困难', '直刺0.5-0.8寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 21, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303732, '日月', 'riyu', NULL, 1, 'foot_shaoyang', '足少阳胆经', '胸部，乳头下方，第七肋间隙', NULL, NULL, '疏肝利胆，和胃降逆', '胸胁痛、呕吐、吞酸', '斜刺0.5-0.8寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 24, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303733, '带脉', 'daimai', NULL, 1, 'foot_shaoyang', '足少阳胆经', '侧腹部，第十一肋骨游离端直下', NULL, NULL, '调经止带，健脾利湿', '腹痛、疝气、月经不调', '直刺1-1.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 26, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303734, '环跳', 'huantiao', NULL, 1, 'foot_shaoyang', '足少阳胆经', '侧卧屈股，股骨大转子最高点', NULL, NULL, '祛风除湿，舒筋活络', '腰痛、下肢痿痹、坐骨神经痛', '直刺2-3寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 30, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303735, '风市', 'fengshi', NULL, 1, 'foot_shaoyang', '足少阳胆经', '大腿外侧，腘横纹上7寸', NULL, NULL, '祛风除湿，舒筋活络', '下肢痿痹、遍身瘙痒', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 31, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303736, '阳陵泉', 'yanglingquan', NULL, 1, 'foot_shaoyang', '足少阳胆经', '小腿外侧，腓骨小头前下方', NULL, NULL, '疏肝利胆，舒筋活络', '胁痛、下肢痿痹、呕吐', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 34, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303737, '光明', 'guangming', NULL, 1, 'foot_shaoyang', '足少阳胆经', '小腿外侧，外踝尖上5寸', NULL, NULL, '清肝明目，通络止痛', '目痛、夜盲、下肢痿痹', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 37, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303738, '悬钟', 'xuanzhong', NULL, 1, 'foot_shaoyang', '足少阳胆经', '小腿外侧，外踝尖上3寸', NULL, NULL, '清热泻火，舒筋活络', '胁痛、落枕、踝痛', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 39, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303739, '丘墟', 'qiuxu', NULL, 1, 'foot_shaoyang', '足少阳胆经', '足外踝前下方凹陷处', NULL, NULL, '清肝明目，舒筋活络', '胸胁痛、下肢痿痹', '直刺0.5-0.8寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 40, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303761, '足临泣', 'zulinqi', NULL, 1, 'foot_shaoyang', '足少阳胆经', '足背，第四、五跖骨结合部', NULL, NULL, '清肝明目，舒筋活络', '头痛、眩晕、胸胁痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 41, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303762, '侠溪', 'xiaxi', NULL, 1, 'foot_shaoyang', '足少阳胆经', '足背，第四、五趾缝间', NULL, NULL, '清肝泻火，聪耳明目', '头痛、眩晕、胁痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 43, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303763, '睛明', 'jingming', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '面部，内眦角内上方', NULL, NULL, '清热明目，祛风止泪', '目赤肿痛、近视、夜盲', '直刺0.3-0.5寸', '按揉1-2分钟，力度轻柔', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303764, '攒竹', 'cuanzhu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '面部，眉头凹陷处', NULL, NULL, '清热明目，祛风止痛', '头痛、眉棱骨痛、目疾', '平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303765, '天柱', 'tianzhu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '颈部，后发际上0.5寸，斜方肌外缘', NULL, NULL, '清头明目，祛风止痛', '头痛、项强、鼻塞、肩背痛', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303766, '大杼', 'dazhu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第一胸椎棘突下旁开1.5寸', NULL, NULL, '清热解表，舒筋活络', '咳嗽、发热、项强、肩背痛', '斜刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 11, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303830, '风门', 'fengmen', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第二胸椎棘突下旁开1.5寸', NULL, NULL, '祛风解表，宣肺止咳', '感冒、咳嗽、发热', '斜刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 12, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303831, '肺俞', 'feishu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第三胸椎棘突下旁开1.5寸', NULL, NULL, '调肺益气，止咳化痰', '咳嗽、气喘、咯血', '斜刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 13, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303832, '心俞', 'xinshu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第五胸椎棘突下旁开1.5寸', NULL, NULL, '调心安神，宽胸止痛', '心悸、失眠、健忘', '斜刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303833, '膈俞', 'geshu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第七胸椎棘突下旁开1.5寸', NULL, NULL, '宽胸理气，止血化瘀', '呕吐、呃逆、咳嗽', '斜刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 17, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303834, '肝俞', 'ganshu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第九胸椎棘突下旁开1.5寸', NULL, NULL, '疏肝理气，养血明目', '胁痛、黄疸、目疾', '斜刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 18, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303835, '胆俞', 'danshu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第十胸椎棘突下旁开1.5寸', NULL, NULL, '疏肝利胆，清热化湿', '胁痛、黄疸、口苦', '斜刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 19, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303836, '脾俞', 'pishu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第十一胸椎棘突下旁开1.5寸', NULL, NULL, '健脾利湿，益气统血', '腹胀、泄泻、呕吐', '斜刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 20, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303837, '胃俞', 'weishu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '背部，第十二胸椎棘突下旁开1.5寸', NULL, NULL, '和胃降逆，健脾消食', '胃痛、腹胀、呕吐', '斜刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 21, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303838, '肾俞', 'shenshu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '腰部，第二腰椎棘突下旁开1.5寸', NULL, NULL, '补肾益精，壮腰聪耳', '腰痛、遗精、阳痿、月经不调', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 23, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303839, '大肠俞', 'dachangshu', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '腰部，第四腰椎棘突下旁开1.5寸', NULL, NULL, '调理肠胃，壮腰止痛', '腰痛、腹胀、泄泻', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 25, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303861, '委中', 'weizhong', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '膝腘窝，腘横纹中点', NULL, NULL, '舒筋活络，清热泻火', '腰痛、下肢痿痹、腹痛', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 40, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303862, '承山', 'chengshan', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '小腿后侧，腓肠肌两肌腹之间', NULL, NULL, '舒筋活络，通络止痛', '腰痛、腿痛、痔疾', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 57, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303863, '昆仑', 'kunlun', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '足外踝后方，跟腱前方凹陷处', NULL, NULL, '舒筋活络，通络止痛', '头痛、项强、腰痛、踝痛', '直刺0.5-1寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 60, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303864, '至阴', 'zhiyin', NULL, 1, 'foot_taiyang', '足太阳膀胱经', '足小趾外侧，趾甲角旁开0.1寸', NULL, NULL, '清热泻火，调理胎位', '头痛、鼻塞、胎位不正', '浅刺0.1寸，或艾灸', '艾灸或按揉，每次3-5分钟', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 67, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303865, '大敦', 'dadun', NULL, 1, 'foot_jueyin', '足厥阴肝经', '足大趾外侧，趾甲角旁开0.1寸', NULL, NULL, '疏肝理气，调经止崩', '疝气、遗尿、崩漏', '浅刺0.1寸，或艾灸', '艾灸或按揉，每次3-5分钟', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303866, '行间', 'xingjian', NULL, 1, 'foot_jueyin', '足厥阴肝经', '足背，第一、二趾间缝纹端', NULL, NULL, '清肝泻火，凉血止血', '头痛、眩晕、目赤肿痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303930, '太冲', 'taichong', NULL, 1, 'foot_jueyin', '足厥阴肝经', '足背，第一、二跖骨间', NULL, NULL, '疏肝理气，平肝息风', '头痛、眩晕、胁痛、足痛', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303931, '蠡沟', 'ligou', NULL, 1, 'foot_jueyin', '足厥阴肝经', '小腿内侧，足内踝上5寸', NULL, NULL, '疏肝理气，调经止带', '小便不利、疝气、月经不调', '直刺0.5-1寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303932, '曲泉', 'ququan', NULL, 1, 'foot_jueyin', '足厥阴肝经', '膝内侧横纹头上方', NULL, NULL, '清肝泻火，调经止带', '小便不利、遗精、阴痒', '直刺1-1.5寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303933, '章门', 'zhangmen', NULL, 1, 'foot_jueyin', '足厥阴肝经', '侧腹部，第十一肋游离端', NULL, NULL, '疏肝健脾，消痞散结', '腹痛、腹胀、胁痛', '斜刺0.5-1寸', '按揉2-3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 13, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303934, '期门', 'qimen', NULL, 1, 'foot_jueyin', '足厥阴肝经', '胸部，乳头直下，第六肋间隙', NULL, NULL, '疏肝理气，健脾和胃', '胸胁胀痛、呕吐、乳痈', '斜刺0.5-0.8寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303935, '关元', 'guanyuan', NULL, 1, 'ren', '任脉', '腹部，脐下3寸', NULL, NULL, '补肾固本，调经止带', '虚劳冷惫、羸瘦无力', '直刺1-2寸', '按揉5-10分钟，或艾灸', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303936, '气海', 'qihai', NULL, 1, 'ren', '任脉', '腹部，脐下1.5寸', NULL, NULL, '调气补虚，固本培元', '虚脱、乏力、腹痛', '直刺1-1.5寸', '按揉3-5分钟，或艾灸', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303937, '神阙', 'shenque', NULL, 1, 'ren', '任脉', '腹部，脐中央', NULL, NULL, '温阳救逆，健脾止泻', '虚脱、腹痛、泄泻', '艾灸（慎按）', '艾灸为主，慎按摩', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303938, '中脘', 'zhongwan', NULL, 1, 'ren', '任脉', '腹部，脐上4寸', NULL, NULL, '和胃降逆，健脾消食', '胃痛、呕吐、腹胀、泄泻', '直刺1-1.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 12, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303939, '膻中', 'danzhong', NULL, 1, 'ren', '任脉', '胸部，平第四肋间隙', NULL, NULL, '宽胸理气，止咳平喘', '气喘、胸痛、心悸', '平刺0.3-0.5寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 17, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303961, '天突', 'tiantu', NULL, 1, 'ren', '任脉', '颈部，胸骨上窝中央', NULL, NULL, '宣肺降逆，止咳化痰', '咳嗽、气喘、咽喉肿痛', '先直刺0.2寸，再向下斜刺', '按揉（轻），力度轻柔', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 22, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303962, '承浆', 'chengjiang', NULL, 1, 'ren', '任脉', '面部，颏唇沟正中凹陷处', NULL, NULL, '祛风通络，固齿止泻', '口歪、齿龈肿痛、腰痛', '直刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 24, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303963, '腰阳关', 'yaoyangguan', NULL, 1, 'du', '督脉', '腰部，第四腰椎棘突下', NULL, NULL, '强腰补肾，舒筋活络', '腰痛、下肢痿痹、遗精', '直刺1-1.5寸', '按揉3分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303964, '命门', 'mingmen', NULL, 1, 'du', '督脉', '腰部，第二腰椎棘突下', NULL, NULL, '温肾固本，强腰聪耳', '腰痛、遗精、阳痿', '直刺1-1.5寸', '按揉3-5分钟，或艾灸', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303965, '大椎', 'dazhui', NULL, 1, 'du', '督脉', '第七颈椎棘突下', NULL, NULL, '清热解表，益气固表', '发热、感冒、咳嗽', '直刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030303966, '风府', 'fengfu', NULL, 1, 'du', '督脉', '后发际正中入发际1寸', NULL, NULL, '祛风开窍，醒神止痛', '头痛、眩晕、中风', '慎按', '慎按', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 16, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030306130, '百会', 'baihui', NULL, 1, 'du', '督脉', '头顶正中央，两耳尖连线中点', NULL, NULL, '升阳固脱，醒脑开窍', '头痛、眩晕、失眠、中风', '平刺0.5-1寸', '按揉3-5分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 3, 12, 2.0, 0, 0, 20, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030306131, '上星', 'shangxing', NULL, 1, 'du', '督脉', '前发际上1寸', NULL, NULL, '清热止血，通鼻开窍', '头痛、眩晕、鼻渊', '平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 24, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030306132, '神庭', 'shenting', NULL, 1, 'du', '督脉', '前发际上0.5寸', NULL, NULL, '宁心安神，醒脑开窍', '头痛、眩晕、失眠', '平刺0.3-0.5寸', '按揉2分钟，力度适中', NULL, NULL, NULL, 1, NULL, NULL, 1, 12, 2.0, 0, 0, 25, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_acupoint` VALUES (0x30303030303030653030303030306133, '水沟', 'shuigou', NULL, 1, 'du', '督脉', '人中沟上1/3处', NULL, NULL, '醒神开窍，息风止痛', '昏迷、晕厥、腰脊强痛', '向上斜刺0.3-0.5寸', '按揉（急救）', NULL, NULL, NULL, 1, NULL, NULL, 2, 12, 2.0, 0, 0, 26, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');

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
-- Records of tl_wis_acupoint_combo
-- ----------------------------
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303031, '补气养血组合', 1, '[\"qixu\",\"yangxu\"]', NULL, NULL, '补气养血常用穴位组合', '[\"0000000e00000096\",\"0000000e00000095\",\"0000000e0000002b\"]', '[\"气海\",\"关元\",\"足三里\"]', 1, NULL, 10, 2, '补气养血，增强体质', NULL, NULL, NULL, NULL, 0, 0, 1, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303032, '滋阴清热组合', 1, '[\"yinxu\",\"shire\"]', NULL, NULL, '滋阴清热常用穴位组合', '[\"0000000e00000035\",\"0000000e00000052\",\"0000000e00000050\"]', '[\"三阴交\",\"太溪\",\"涌泉\"]', 1, NULL, 10, 2, '滋阴清热，降火', NULL, NULL, NULL, NULL, 0, 0, 2, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303033, '化痰祛湿组合', 1, '[\"tanshi\"]', NULL, NULL, '化痰祛湿常用穴位组合', '[\"0000000e0000002f\",\"0000000e0000002b\",\"0000000e00000037\"]', '[\"丰隆\",\"足三里\",\"阴陵泉\"]', 1, NULL, 10, 2, '化痰祛湿，减肥', NULL, NULL, NULL, NULL, 0, 0, 3, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303034, '活血化瘀组合', 1, '[\"yuxu\"]', NULL, NULL, '活血化瘀常用穴位组合', '[\"0000000e00000038\",\"0000000e00000035\",\"0000000e00000083\"]', '[\"血海\",\"三阴交\",\"膈俞\"]', 1, NULL, 10, 2, '活血化瘀，通络', NULL, NULL, NULL, NULL, 0, 0, 4, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303035, '疏肝解郁组合', 1, '[\"qiyu\"]', NULL, NULL, '疏肝解郁常用穴位组合', '[\"0000000e00000090\",\"0000000e00000084\",\"0000000e00000099\"]', '[\"太冲\",\"肝俞\",\"膻中\"]', 1, NULL, 10, 2, '疏肝理气，缓解抑郁', NULL, NULL, NULL, NULL, 0, 0, 5, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303036, '晨起提神组合', 4, NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '晨起按摩提神醒脑', '[\"0000000e000000a0\",\"0000000e00000070\"]', '[\"百会\",\"风池\"]', 1, NULL, 5, 1, '提神醒脑，振奋精神', NULL, NULL, NULL, NULL, 0, 0, 6, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303037, '安神助眠组合', 4, NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '晚间按摩助眠', '[\"0000000e00000052\",\"0000000e00000050\",\"0000000e0000003f\"]', '[\"太溪\",\"涌泉\",\"神门\"]', 1, NULL, 15, 1, '安神助眠，改善睡眠', NULL, NULL, NULL, NULL, 0, 0, 7, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303038, '健脾养胃组合', 4, NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '调理脾胃穴位组合', '[\"0000000e0000002b\",\"0000000e00000098\",\"0000000e00000028\"]', '[\"足三里\",\"中脘\",\"天枢\"]', 1, NULL, 10, 2, '健脾养胃，促进消化', NULL, NULL, NULL, NULL, 0, 0, 8, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303039, '增强免疫力组合', 4, NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '增强抵抗力穴位组合', '[\"0000000e0000002b\",\"0000000e00000095\",\"0000000e00000081\"]', '[\"足三里\",\"关元\",\"肺俞\"]', 1, NULL, 10, 1, '调节免疫，增强体质', NULL, NULL, NULL, NULL, 0, 0, 9, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303061, '补肾强腰组合', 1, '[\"yangxu\"]', '[\"4\"]', NULL, '温补肾阳，强健腰膝', '[\"0000000e00000095\",\"0000000e00000088\",\"0000000e0000009d\"]', '[\"关元\",\"肾俞\",\"命门\"]', 1, NULL, 10, 2, '补肾壮阳，强腰健膝', NULL, NULL, NULL, NULL, 0, 0, 10, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');
INSERT INTO `tl_wis_acupoint_combo` VALUES (0x30303030303030663030303030303062, '清热泻火组合', 1, '[\"shire\"]', '[\"2\",\"3\"]', NULL, '清泻肝火，降火解毒', '[\"0000000e00000016\",\"0000000e00000037\",\"0000000e00000031\"]', '[\"曲池\",\"阴陵泉\",\"内庭\"]', 1, NULL, 10, 2, '清热泻火，凉血解毒', NULL, NULL, NULL, NULL, 0, 0, 11, 0, 0, '2026-03-24 22:43:19', '2026-03-24 22:43:19');

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
-- Records of tl_wis_article
-- ----------------------------
INSERT INTO `tl_wis_article` VALUES (0x31323334353637383930616263646566, '春天湿气重，祛湿养脾正当时', '四季养生', 2, '[\"祛湿\", \"健脾\", \"春季养生\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '春季天气潮湿，湿气容易侵袭人体，导致困倦、乏力、食欲不振等症状。', '# 春天湿气重\r\n\r\n## 湿气的危害\r\n\r\n湿气是\"六邪\"之一，湿气重会导致：\r\n- 困倦乏力\r\n- 食欲不振\r\n- 身体沉重\r\n- 舌苔厚腻\r\n- 大便粘滞\r\n\r\n## 祛湿健脾方法\r\n\r\n### 1. 饮食祛湿\r\n推荐食物：薏米、红豆、冬瓜、山药、茯苓\r\n\r\n### 2. 运动祛湿\r\n推荐运动：快走、游泳、瑜伽、八段锦\r\n\r\n### 3. 穴位祛湿\r\n阴陵泉穴：健脾祛湿\r\n足三里穴：调理脾胃', 1, '道养生活', '原创', 'tanshi,shire,yangxu', 1, '春分', 14300, 945, 412, 198, 1, 0, 0, '2026-03-20 09:00:00', 10, 1, '2026-03-20 09:00:00', '2026-03-21 19:05:14', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x31633361653431343964623431316431, '中医体质辨识：你是哪种体质？', '中医知识', 5, '[\"体质辨识\", \"中医\", \"九种体质\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '中医将人体体质分为九种类型，了解自己的体质，才能更好地进行养生调理。', '# 中医体质辨识\r\n\r\n## 什么是体质\r\n\r\n体质是人体在先天禀赋和后天调养基础上形成的相对稳定的生理特性。\r\n\r\n## 九种体质类型\r\n\r\n1. 平和质 - 面色润泽，精力充沛\r\n2. 气虚质 - 容易疲乏，气短懒言\r\n3. 阳虚质 - 畏寒怕冷，手足不温\r\n4. 阴虚质 - 口燥咽干，手足心热\r\n5. 痰湿质 - 形体肥胖，口黏苔腻\r\n6. 湿热质 - 面垢油光，口苦苔黄\r\n7. 血瘀质 - 肤色晦黯，易见瘀斑\r\n8. 气郁质 - 情志抑郁，忧虑脆弱\r\n9. 特禀质 - 过敏体质，先天失常', 1, '道养生活', '原创', 'pinghe,qixu,yangxu,yinxu,tanshi,shire,yuxu,qiyu,tebing', 1, NULL, 23500, 1567, 678, 345, 0, 1, 0, '2026-03-10 10:00:00', 5, 1, '2026-03-10 10:00:00', '2026-04-17 01:08:07', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x35353065383430306532396234316434, '为什么春天容易困倦？中医教你按压这两个穴位', '春季养生指南', 5, '[\"经络\", \"穴位\", \"春季养生\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '春天是万物复苏的季节，但很多人却感到困倦乏力。中医认为这是因为肝气郁结所致，通过按摩特定穴位可以有效缓解春困症状。', '# 为什么春天容易困倦？中医教你按压这两个穴位\r\n\r\n## 春季困倦的成因\r\n\r\n春天是万物复苏的季节，但很多人却感到困倦乏力、精神不振。这种现象在中医称为\"春困\"，主要与以下因素有关：\r\n\r\n1. **肝气郁结**：春季对应肝脏，肝气旺盛但易郁结\r\n2. **阳气生发**：人体阳气开始外散，需要适应\r\n3. **湿气困脾**：春季潮湿，脾虚易困\r\n\r\n## 按摩这两个穴位轻松解困\r\n\r\n### 1. 百会穴\r\n\r\n**位置**：头顶正中心，两耳尖直上连线中点\r\n\r\n**功效**：\r\n- 提神醒脑\r\n- 升举阳气\r\n- 缓解头痛\r\n\r\n**按摩方法**：\r\n- 用拇指指腹轻轻按揉\r\n- 每次3-5分钟\r\n- 早晚各一次\r\n\r\n### 2. 风池穴\r\n\r\n**位置**：枕骨下，胸锁乳突肌与斜方肌上端之间的凹陷处\r\n\r\n**功效**：\r\n- 祛风散寒\r\n- 缓解疲劳\r\n- 改善大脑供血\r\n\r\n**按摩方法**：\r\n- 用双手拇指分别按揉两侧风池穴\r\n- 每次2-3分钟\r\n- 力度适中，以酸胀为度\r\n\r\n## 春季养生小贴士\r\n\r\n1. **起居有常**：保证充足睡眠，早睡早起\r\n2. **适度运动**：多到户外活动，舒展筋骨\r\n3. **饮食调理**：多吃清淡易消化的食物\r\n4. **情志调节**：保持心情愉悦，戒骄戒躁\r\n\r\n通过坚持按摩这两个穴位，配合良好的生活习惯，就能有效缓解春困，让你在春天精力充沛！', 1, '道养生活', '原创', NULL, 1, '春分', 12500, 856, 324, 156, 0, 1, 0, '2026-03-15 10:00:00', 1, 1, '2026-03-15 10:00:00', '2026-04-17 01:08:09', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x36626137623831303964616431316431, '药食同源：春天里最该喝的这一杯茶', '春季养生茶饮', 4, '[\"药食同源\", \"茶饮\", \"春季养生\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '春季养生茶饮推荐，枸杞菊花茶清肝明目，玫瑰花茶疏肝解郁，这些茶饮不仅口感好，还有很好的养生功效。', '# 药食同源：春天里最该喝的这一杯茶\r\n\r\n## 春季养生茶饮的重要性\r\n\r\n中医讲究\"药食同源\"，很多食材既是食物也是药物。春季养生，适当饮用一些茶饮，可以起到很好的调理作用。\r\n\r\n## 春季推荐茶饮\r\n\r\n### 1. 枸杞菊花茶\r\n\r\n**原料**：\r\n- 枸杞子 10克\r\n- 菊花 5克\r\n- 沸水 300ml\r\n\r\n**功效**：\r\n- 清肝明目\r\n- 滋补肝肾\r\n- 缓解眼睛疲劳\r\n\r\n**饮用方法**：\r\n- 用沸水冲泡，加盖焖5分钟\r\n- 每日1-2杯\r\n- 饭后饮用效果更佳\r\n\r\n### 2. 玫瑰花茶\r\n\r\n**原料**：\r\n- 玫瑰花 5-6朵\r\n- 蜂蜜适量\r\n- 沸水 200ml\r\n\r\n**功效**：\r\n- 疏肝解郁\r\n- 活血止痛\r\n- 美容养颜\r\n\r\n### 3. 茉莉花茶\r\n\r\n**原料**：\r\n- 茉莉花 3克\r\n- 绿茶 3克\r\n- 沸水 250ml\r\n\r\n**功效**：\r\n- 理气开郁\r\n- 提神醒脑\r\n- 消除春困', 1, '道养生活', '原创', NULL, 1, '春分', 8900, 623, 287, 98, 0, 0, 0, '2026-03-16 14:30:00', 2, 1, '2026-03-16 14:30:00', '2026-04-17 01:08:10', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x37643434343834303964633031316431, '春分时节养肝黄金期，中医教你如何调理', '四季养生', 2, '[\"养肝\", \"春分\", \"四季养生\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '春分是春季的中点，也是养肝的黄金时期。中医认为春属木，对应肝脏，此时调理肝脏事半功倍。', '# 春分时节养肝黄金期\r\n\r\n## 为什么春分要养肝\r\n\r\n春分时节，昼夜平分，阴阳平衡，是春季养生的重要节点。中医认为：\r\n- 春属木，木对应肝\r\n- 春分是阴阳平衡点\r\n- 肝气旺于春季\r\n\r\n## 养肝调理方法\r\n\r\n### 1. 情志调理\r\n保持心情愉悦，避免情绪波动，多接触大自然。\r\n\r\n### 2. 作息调整\r\n早睡早起（22:00-6:00），睡好子午觉。\r\n\r\n### 3. 饮食调理\r\n多吃青色食物、酸味食物、富含维生素A的食物。', 1, '道养生活', '原创', NULL, 1, '春分', 15600, 1024, 456, 234, 0, 0, 0, '2026-03-18 09:00:00', 3, 1, '2026-03-18 09:00:00', '2026-04-17 01:08:11', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x38663361326231633765366439306100, '八段锦：千年导引术，养生强身宝', '养生方法', 1, '[\"八段锦\", \"导引术\", \"传统功法\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '八段锦是中医传统导引术的一种，历史悠久，动作简单，功效显著。每天练习可以强身健体，延年益寿。', '# 八段锦：千年导引术\r\n\r\n## 什么是八段锦\r\n\r\n八段锦是中国传统导引术的一种，起源于宋代。\r\n\r\n## 八段锦的八大功效\r\n\r\n1. 舒筋活络\r\n2. 调理气血\r\n3. 增强体质\r\n4. 延缓衰老\r\n5. 改善睡眠\r\n6. 缓解压力\r\n7. 调节脏腑\r\n8. 预防疾病\r\n\r\n## 八式动作\r\n\r\n1. 两手托天理三焦\r\n2. 左右开弓似射雕\r\n3. 调理脾胃须单举\r\n4. 五劳七伤往后瞧\r\n5. 摇头摆尾去心火\r\n6. 两手攀足固肾腰\r\n7. 攒拳怒目增气力\r\n8. 背后七颠百病消', 1, '道养生活', '原创', NULL, 1, NULL, 18900, 1234, 567, 289, 0, 0, 0, '2026-03-12 06:00:00', 6, 1, '2026-03-12 06:00:00', '2026-04-17 01:08:12', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x39633165306535303964623331316431, '惊蛰养生：顺应节气，百虫苏醒的养生之道', '节气养生', 3, '[\"惊蛰\", \"节气养生\", \"防虫\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '惊蛰是二十四节气中的第三个节气，标志着仲春时节的开始。此时春雷响动，万物复苏，也是养生保健的重要时期。', '# 惊蛰养生\r\n\r\n## 什么是惊蛰\r\n\r\n惊蛰，意为春雷惊醒蛰伏的昆虫，是春季的第三个节气。\r\n\r\n## 惊蛰养生特点\r\n\r\n### 1. 穿衣要\"春捂\"\r\n早晚添衣，中午可适当减少。\r\n\r\n### 2. 饮食要清淡\r\n推荐梨、菠菜、蜂蜜、银耳等食物。\r\n\r\n### 3. 起居要规律\r\n早睡早起，适当运动，保持室内通风。', 1, '道养生活', '原创', NULL, 1, '惊蛰', 9800, 567, 234, 123, 0, 0, 0, '2026-03-05 08:00:00', 4, 1, '2026-03-05 08:00:00', '2026-04-17 01:08:20', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x61316232633364346535663630373100, '春季养生粥：健脾养胃，补气养血', '食疗方案', 4, '[\"养生粥\", \"食疗\", \"健脾养胃\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '春季养生粥是很好的食疗方式，既能健脾养胃，又能补气养血。', '# 春季养生粥\r\n\r\n## 为什么春季宜喝养生粥\r\n\r\n春季气温变化大，人体脾胃功能相对较弱。粥类食物易于消化吸收。\r\n\r\n## 养生粥配方\r\n\r\n### 1. 红枣山药粥\r\n原料：红枣10颗，山药50克，大米100克\r\n功效：补气养血，健脾益胃\r\n\r\n### 2. 枸杞菊花粥\r\n原料：枸杞子15克，菊花5克，大米100克\r\n功效：清肝明目，滋补肝肾\r\n\r\n### 3. 薏米红豆粥\r\n原料：薏米30克，红豆30克，大米50克\r\n功效：健脾祛湿，利水消肿', 1, '道养生活', '原创', NULL, 1, '春分', 11200, 789, 345, 167, 1, 0, 0, '2026-03-14 07:00:00', 7, 1, '2026-03-14 07:00:00', '2026-03-21 19:05:14', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x64346535663661376238633930313200, '春季养肝正当时，这些穴位要常按', '经络养生', 5, '[\"养肝\", \"穴位\", \"经络\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '春季是养肝的黄金时期，中医认为肝主疏泄，调畅情志。经常按摩一些养肝穴位，可以起到很好的保健作用。', '# 春季养肝正当时\r\n\r\n## 肝脏的功能\r\n\r\n中医认为肝脏：\r\n- 主疏泄：调畅气机、情志\r\n- 主藏血：调节血量、储藏血液\r\n- 开窍于目：与眼睛健康相关\r\n- 在体合筋：与筋脉弹性相关\r\n\r\n## 养肝穴位\r\n\r\n### 1. 太冲穴\r\n位置：足背，第一、二跖骨间凹陷处\r\n功效：清肝火，疏肝气\r\n\r\n### 2. 期门穴\r\n位置：胸部，乳头直下，第6肋间隙\r\n功效：疏肝理气，健脾和胃\r\n\r\n### 3. 肝俞穴\r\n位置：背部，第9胸椎棘突下，旁开1.5寸\r\n功效：养肝明目，调理气血', 1, '道养生活', '原创', 'qixu,yangxu,yinxu,qiyu', 1, '春分', 16800, 1123, 478, 234, 1, 1, 0, '2026-03-19 10:30:00', 9, 1, '2026-03-19 10:30:00', '2026-03-21 19:05:14', 0, NULL);
INSERT INTO `tl_wis_article` VALUES (0x66316532643363346235613639373800, '经络不通百病生，一分钟学会自我疏通', '中医知识', 5, '[\"经络\", \"疏通\", \"中医\"]', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d987331657394669a41871b634b4c8fd.jpg', '中医认为\"通则不痛，痛则不通\"，经络是气血运行的通道，经络不通会导致各种疾病。', '# 经络不通百病生\r\n\r\n## 什么是经络\r\n\r\n经络是中医理论中气血运行的通道，包括十二正经和奇经八脉。\r\n\r\n## 经络不通的表现\r\n\r\n1. 疼痛：肩背痛、腰痛、关节痛\r\n2. 麻木：手脚麻木、感觉迟钝\r\n3. 发凉：四肢冰凉、畏寒\r\n4. 肿胀：局部肿胀、水肿\r\n5. 色斑：面色晦暗、色斑沉着\r\n\r\n## 简单经络疏通法\r\n\r\n### 1. 敲胆经\r\n从臀部敲到膝盖，促进胆汁分泌。\r\n\r\n### 2. 揉心包经\r\n从肩膀到手指，养护心脏。\r\n\r\n### 3. 拍八虚\r\n拍打两肘窝、两腋窝、两腘窝、两腹股沟。', 1, '道养生活', '原创', NULL, 1, NULL, 21000, 1456, 623, 312, 1, 0, 0, '2026-03-17 15:00:00', 8, 1, '2026-03-17 15:00:00', '2026-03-21 19:06:14', 0, NULL);

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
-- Records of tl_wis_exercise
-- ----------------------------
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303031, '太极拳', 'taiji quan', 1, 1, '[\"qixu\",\"yangxu\",\"qiyu\",\"yuxu\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '调理气血，舒缓情绪，增强平衡能力', NULL, NULL, NULL, NULL, 30, NULL, 2, NULL, 1, NULL, 0, 0, 1, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303032, '八段锦', 'baduan jin', 1, 1, '[\"qixu\",\"yangxu\",\"yinxu\",\"tanshi\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '调理脏腑，疏通经络，增强体质', NULL, NULL, NULL, NULL, 15, NULL, 1, NULL, 1, NULL, 0, 0, 2, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303033, '五禽戏', 'wuqin xi', 1, 1, '[\"qixu\",\"yangxu\",\"shire\"]', NULL, '[\"1\",\"2\"]', NULL, '活动全身，调和气血，增强免疫力', NULL, NULL, NULL, NULL, 20, NULL, 2, NULL, 1, NULL, 0, 0, 3, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303034, '六字诀', 'liuzi jue', 1, 1, '[\"qixu\",\"yinxu\",\"qiyu\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '调理五脏，安定情绪', NULL, NULL, NULL, NULL, 15, NULL, 1, NULL, 1, NULL, 0, 0, 4, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303035, '易筋经', 'yijin jing', 1, 2, '[\"tanshi\",\"shire\"]', NULL, '[\"1\",\"3\",\"4\"]', NULL, '拉伸筋骨，增强力量', NULL, NULL, NULL, NULL, 25, NULL, 3, NULL, 1, NULL, 0, 0, 5, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303036, '散步', 'san bu', 2, 1, '[\"pinghe\",\"qixu\",\"yangxu\",\"yinxu\",\"qiyu\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '舒缓身心，促进消化', NULL, NULL, NULL, NULL, 30, NULL, 1, NULL, 1, NULL, 0, 0, 6, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303037, '慢跑', 'man pao', 2, 2, '[\"pinghe\",\"tanshi\",\"shire\"]', NULL, '[\"2\",\"3\"]', NULL, '增强心肺功能，减肥', NULL, NULL, NULL, NULL, 30, NULL, 2, NULL, 1, NULL, 0, 0, 7, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303038, '游泳', 'you yong', 2, 2, '[\"tanshi\",\"shire\"]', NULL, '[\"2\",\"3\"]', NULL, '全身运动，增强心肺', NULL, NULL, NULL, NULL, 45, NULL, 2, NULL, 1, NULL, 0, 0, 8, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303039, '瑜伽', 'yu jia', 4, 1, '[\"qiyu\",\"yuxu\",\"yinxu\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '柔韧身心，舒缓压力', NULL, NULL, NULL, NULL, 45, NULL, 2, NULL, 1, NULL, 0, 0, 9, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303061, '站桩', 'zhan zhuang', 1, 1, '[\"qixu\",\"yangxu\",\"yuxu\"]', NULL, '[\"1\",\"3\",\"4\"]', NULL, '培养元气，安定心神', NULL, NULL, NULL, NULL, 20, NULL, 1, NULL, 1, NULL, 0, 0, 10, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303062, '静坐', 'jing zuo', 1, 1, '[\"yinxu\",\"qiyu\",\"shire\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '静心养神，调和气血', NULL, NULL, NULL, NULL, 20, NULL, 1, NULL, 1, NULL, 0, 0, 11, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');
INSERT INTO `tl_wis_exercise` VALUES (0x30303030303030653030303030303063, '导引术', 'dao yin shu', 1, 1, '[\"qixu\",\"tanshi\",\"yuxu\"]', NULL, '[\"1\",\"2\",\"3\",\"4\"]', NULL, '疏通经络，调和脏腑', NULL, NULL, NULL, NULL, 20, NULL, 1, NULL, 1, NULL, 0, 0, 12, 0, 0, '2026-03-24 22:43:18', '2026-03-24 22:43:18');

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
-- Records of tl_wis_fitness_option
-- ----------------------------
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303031, 0x30303030303030623030303030303031, 1, '18-30岁', 1, NULL, 1, '2026-04-03 17:24:09', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303032, 0x30303030303030623030303030303031, 2, '31-45岁', 2, NULL, 2, '2026-04-03 17:24:09', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303033, 0x30303030303030623030303030303031, 3, '46-60岁', 3, NULL, 3, '2026-04-03 17:24:09', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303034, 0x30303030303030623030303030303031, 4, '60岁以上', 4, NULL, 4, '2026-04-03 17:24:09', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303035, 0x30303030303030623030303030303032, 1, '男性', 1, NULL, 1, '2026-04-03 17:24:09', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303036, 0x30303030303030623030303030303032, 2, '女性', 2, NULL, 2, '2026-04-03 17:24:09', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303037, 0x30303030303030623030303030303033, 1, '从不', 1, 'pinghe', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303038, 0x30303030303030623030303030303033, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303039, 0x30303030303030623030303030303033, 3, '有时', 3, 'pinghe', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303061, 0x30303030303030623030303030303033, 4, '经常', 4, 'pinghe', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303062, 0x30303030303030623030303030303033, 5, '总是', 5, 'pinghe', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303063, 0x30303030303030623030303030303034, 1, '从不', 1, 'qixu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303064, 0x30303030303030623030303030303034, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303065, 0x30303030303030623030303030303034, 3, '有时', 3, 'qixu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303066, 0x30303030303030623030303030303034, 4, '经常', 4, 'qixu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303130, 0x30303030303030623030303030303034, 5, '总是', 5, 'qixu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303131, 0x30303030303030623030303030303035, 1, '从不', 1, 'yangxu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303132, 0x30303030303030623030303030303035, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303133, 0x30303030303030623030303030303035, 3, '有时', 3, 'yangxu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303134, 0x30303030303030623030303030303035, 4, '经常', 4, 'yangxu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303135, 0x30303030303030623030303030303035, 5, '总是', 5, 'yangxu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303136, 0x30303030303030623030303030303036, 1, '从不', 1, 'yinxu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303137, 0x30303030303030623030303030303036, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303138, 0x30303030303030623030303030303036, 3, '有时', 3, 'yinxu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303139, 0x30303030303030623030303030303036, 4, '经常', 4, 'yinxu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303161, 0x30303030303030623030303030303036, 5, '总是', 5, 'yinxu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303162, 0x30303030303030623030303030303037, 1, '不是', 1, 'tanshi', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303163, 0x30303030303030623030303030303037, 2, '轻微', 2, 'tanshi', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303164, 0x30303030303030623030303030303037, 3, '一般', 3, 'tanshi', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303165, 0x30303030303030623030303030303037, 4, '比较', 4, 'tanshi', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303166, 0x30303030303030623030303030303037, 5, '非常', 5, 'tanshi', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303230, 0x30303030303030623030303030303038, 1, '从不', 1, 'shire', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303231, 0x30303030303030623030303030303038, 2, '偶尔', 2, 'shire', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303232, 0x30303030303030623030303030303038, 3, '有时', 3, 'shire', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303233, 0x30303030303030623030303030303038, 4, '经常', 4, 'shire', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303234, 0x30303030303030623030303030303038, 5, '总是', 5, 'shire', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303235, 0x30303030303030623030303030303039, 1, '从不', 1, 'yuxu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303236, 0x30303030303030623030303030303039, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303237, 0x30303030303030623030303030303039, 3, '有时', 3, 'yuxu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303238, 0x30303030303030623030303030303039, 4, '经常', 4, 'yuxu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303239, 0x30303030303030623030303030303039, 5, '总是', 5, 'yuxu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303261, 0x30303030303030623030303030303061, 1, '从不', 1, 'qiyu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303262, 0x30303030303030623030303030303061, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303263, 0x30303030303030623030303030303061, 3, '有时', 3, 'qiyu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303264, 0x30303030303030623030303030303061, 4, '经常', 4, 'qiyu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303265, 0x30303030303030623030303030303061, 5, '总是', 5, 'qiyu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303266, 0x30303030303030623030303030303062, 1, '没有', 1, 'tebing', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303330, 0x30303030303030623030303030303062, 2, '轻微', 2, 'tebing', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303331, 0x30303030303030623030303030303062, 3, '一般', 3, 'tebing', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303332, 0x30303030303030623030303030303062, 4, '比较', 4, 'tebing', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303333, 0x30303030303030623030303030303062, 5, '严重', 5, 'tebing', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303334, 0x30303030303030623030303030303063, 1, '从不', 1, 'qixu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303335, 0x30303030303030623030303030303063, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303336, 0x30303030303030623030303030303063, 3, '有时', 3, 'qixu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303337, 0x30303030303030623030303030303063, 4, '经常', 4, 'qixu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303338, 0x30303030303030623030303030303063, 5, '总是', 5, 'qixu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303339, 0x30303030303030623030303030303064, 1, '不是', 1, 'yangxu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303361, 0x30303030303030623030303030303064, 2, '轻微', 2, 'yangxu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303362, 0x30303030303030623030303030303064, 3, '一般', 3, 'yangxu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303363, 0x30303030303030623030303030303064, 4, '比较', 4, 'yangxu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303364, 0x30303030303030623030303030303064, 5, '非常', 5, 'yangxu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303365, 0x30303030303030623030303030303065, 1, '从不', 1, 'yinxu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303366, 0x30303030303030623030303030303065, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303430, 0x30303030303030623030303030303065, 3, '有时', 3, 'yinxu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303431, 0x30303030303030623030303030303065, 4, '经常', 4, 'yinxu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303432, 0x30303030303030623030303030303065, 5, '总是', 5, 'yinxu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303433, 0x30303030303030623030303030303066, 1, '从不', 1, 'tanshi', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303434, 0x30303030303030623030303030303066, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303435, 0x30303030303030623030303030303066, 3, '有时', 3, 'tanshi', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303436, 0x30303030303030623030303030303066, 4, '经常', 4, 'tanshi', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303437, 0x30303030303030623030303030303066, 5, '总是', 5, 'tanshi', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303438, 0x30303030303030623030303030303130, 1, '从不', 1, 'shire', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303439, 0x30303030303030623030303030303130, 2, '偶尔', 2, 'shire', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303461, 0x30303030303030623030303030303130, 3, '有时', 3, 'shire', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303462, 0x30303030303030623030303030303130, 4, '经常', 4, 'shire', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303463, 0x30303030303030623030303030303130, 5, '总是', 5, 'shire', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303464, 0x30303030303030623030303030303131, 1, '从不', 1, 'yuxu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303465, 0x30303030303030623030303030303131, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303466, 0x30303030303030623030303030303131, 3, '有时', 3, 'yuxu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303530, 0x30303030303030623030303030303131, 4, '经常', 4, 'yuxu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303531, 0x30303030303030623030303030303131, 5, '总是', 5, 'yuxu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303532, 0x30303030303030623030303030303132, 1, '从不', 1, 'qiyu', 1, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303533, 0x30303030303030623030303030303132, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303534, 0x30303030303030623030303030303132, 3, '有时', 3, 'qiyu', 3, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303535, 0x30303030303030623030303030303132, 4, '经常', 4, 'qiyu', 4, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030303030303536, 0x30303030303030623030303030303132, 5, '总是', 5, 'qiyu', 5, '2026-04-03 18:34:49', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303031, 0x30303030303030623030303030313033, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303032, 0x30303030303030623030303030313033, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303033, 0x30303030303030623030303030313033, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303034, 0x30303030303030623030303030313033, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303035, 0x30303030303030623030303030313033, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303036, 0x30303030303030623030303030313034, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303037, 0x30303030303030623030303030313034, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303038, 0x30303030303030623030303030313034, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303039, 0x30303030303030623030303030313034, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303061, 0x30303030303030623030303030313034, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303062, 0x30303030303030623030303030313035, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303063, 0x30303030303030623030303030313035, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303064, 0x30303030303030623030303030313035, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303065, 0x30303030303030623030303030313035, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303066, 0x30303030303030623030303030313035, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303130, 0x30303030303030623030303030313036, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303131, 0x30303030303030623030303030313036, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303132, 0x30303030303030623030303030313036, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303133, 0x30303030303030623030303030313036, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303134, 0x30303030303030623030303030313036, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303135, 0x30303030303030623030303030313037, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303136, 0x30303030303030623030303030313037, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303137, 0x30303030303030623030303030313037, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303138, 0x30303030303030623030303030313037, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303139, 0x30303030303030623030303030313037, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303161, 0x30303030303030623030303030313038, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303162, 0x30303030303030623030303030313038, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303163, 0x30303030303030623030303030313038, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303164, 0x30303030303030623030303030313038, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303165, 0x30303030303030623030303030313038, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303166, 0x30303030303030623030303030313039, 1, '从不', 1, 'pinghe', 1, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303230, 0x30303030303030623030303030313039, 2, '偶尔', 2, 'pinghe', 2, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303231, 0x30303030303030623030303030313039, 3, '有时', 3, 'pinghe', 3, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303232, 0x30303030303030623030303030313039, 4, '经常', 4, 'pinghe', 4, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030313030303233, 0x30303030303030623030303030313039, 5, '总是', 5, 'pinghe', 5, '2026-04-03 17:07:42', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303031, 0x30303030303030623030303030313130, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303032, 0x30303030303030623030303030313130, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303033, 0x30303030303030623030303030313130, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303034, 0x30303030303030623030303030313130, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303035, 0x30303030303030623030303030313130, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303036, 0x30303030303030623030303030313131, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303037, 0x30303030303030623030303030313131, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303038, 0x30303030303030623030303030313131, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303039, 0x30303030303030623030303030313131, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303061, 0x30303030303030623030303030313131, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303062, 0x30303030303030623030303030313132, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303063, 0x30303030303030623030303030313132, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303064, 0x30303030303030623030303030313132, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303065, 0x30303030303030623030303030313132, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303066, 0x30303030303030623030303030313132, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303130, 0x30303030303030623030303030313133, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303131, 0x30303030303030623030303030313133, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303132, 0x30303030303030623030303030313133, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303133, 0x30303030303030623030303030313133, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303134, 0x30303030303030623030303030313133, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303135, 0x30303030303030623030303030313134, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303136, 0x30303030303030623030303030313134, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303137, 0x30303030303030623030303030313134, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303138, 0x30303030303030623030303030313134, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303139, 0x30303030303030623030303030313134, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303161, 0x30303030303030623030303030313135, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303162, 0x30303030303030623030303030313135, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303163, 0x30303030303030623030303030313135, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303164, 0x30303030303030623030303030313135, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303165, 0x30303030303030623030303030313135, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303166, 0x30303030303030623030303030313136, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303230, 0x30303030303030623030303030313136, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303231, 0x30303030303030623030303030313136, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303232, 0x30303030303030623030303030313136, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303233, 0x30303030303030623030303030313136, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303234, 0x30303030303030623030303030313137, 1, '从不', 1, 'qixu', 1, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303235, 0x30303030303030623030303030313137, 2, '偶尔', 2, 'qixu', 2, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303236, 0x30303030303030623030303030313137, 3, '有时', 3, 'qixu', 3, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303237, 0x30303030303030623030303030313137, 4, '经常', 4, 'qixu', 4, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030323030303238, 0x30303030303030623030303030313137, 5, '总是', 5, 'qixu', 5, '2026-04-03 17:08:13', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303031, 0x30303030303030623030303030313138, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303032, 0x30303030303030623030303030313138, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303033, 0x30303030303030623030303030313138, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303034, 0x30303030303030623030303030313138, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303035, 0x30303030303030623030303030313138, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303036, 0x30303030303030623030303030313139, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303037, 0x30303030303030623030303030313139, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303038, 0x30303030303030623030303030313139, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303039, 0x30303030303030623030303030313139, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303061, 0x30303030303030623030303030313139, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303062, 0x30303030303030623030303030313230, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303063, 0x30303030303030623030303030313230, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303064, 0x30303030303030623030303030313230, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303065, 0x30303030303030623030303030313230, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303066, 0x30303030303030623030303030313230, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303130, 0x30303030303030623030303030313231, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303131, 0x30303030303030623030303030313231, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303132, 0x30303030303030623030303030313231, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303133, 0x30303030303030623030303030313231, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303134, 0x30303030303030623030303030313231, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303135, 0x30303030303030623030303030313232, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303136, 0x30303030303030623030303030313232, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303137, 0x30303030303030623030303030313232, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303138, 0x30303030303030623030303030313232, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303139, 0x30303030303030623030303030313232, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303161, 0x30303030303030623030303030313233, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303162, 0x30303030303030623030303030313233, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303163, 0x30303030303030623030303030313233, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303164, 0x30303030303030623030303030313233, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303165, 0x30303030303030623030303030313233, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303166, 0x30303030303030623030303030313234, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303230, 0x30303030303030623030303030313234, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303231, 0x30303030303030623030303030313234, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303232, 0x30303030303030623030303030313234, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303233, 0x30303030303030623030303030313234, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303234, 0x30303030303030623030303030313235, 1, '从不', 1, 'yangxu', 1, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303235, 0x30303030303030623030303030313235, 2, '偶尔', 2, 'yangxu', 2, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303236, 0x30303030303030623030303030313235, 3, '有时', 3, 'yangxu', 3, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303237, 0x30303030303030623030303030313235, 4, '经常', 4, 'yangxu', 4, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030333030303238, 0x30303030303030623030303030313235, 5, '总是', 5, 'yangxu', 5, '2026-04-03 17:08:29', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303031, 0x30303030303030623030303030313236, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303032, 0x30303030303030623030303030313236, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303033, 0x30303030303030623030303030313236, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303034, 0x30303030303030623030303030313236, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303035, 0x30303030303030623030303030313236, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303036, 0x30303030303030623030303030313237, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303037, 0x30303030303030623030303030313237, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303038, 0x30303030303030623030303030313237, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303039, 0x30303030303030623030303030313237, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303061, 0x30303030303030623030303030313237, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303062, 0x30303030303030623030303030313238, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303063, 0x30303030303030623030303030313238, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303064, 0x30303030303030623030303030313238, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303065, 0x30303030303030623030303030313238, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303066, 0x30303030303030623030303030313238, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303130, 0x30303030303030623030303030313239, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303131, 0x30303030303030623030303030313239, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303132, 0x30303030303030623030303030313239, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303133, 0x30303030303030623030303030313239, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303134, 0x30303030303030623030303030313239, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303135, 0x30303030303030623030303030313330, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303136, 0x30303030303030623030303030313330, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303137, 0x30303030303030623030303030313330, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303138, 0x30303030303030623030303030313330, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303139, 0x30303030303030623030303030313330, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303161, 0x30303030303030623030303030313331, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303162, 0x30303030303030623030303030313331, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303163, 0x30303030303030623030303030313331, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303164, 0x30303030303030623030303030313331, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303165, 0x30303030303030623030303030313331, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303166, 0x30303030303030623030303030313332, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303230, 0x30303030303030623030303030313332, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303231, 0x30303030303030623030303030313332, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303232, 0x30303030303030623030303030313332, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303233, 0x30303030303030623030303030313332, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303234, 0x30303030303030623030303030313333, 1, '从不', 1, 'yinxu', 1, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303235, 0x30303030303030623030303030313333, 2, '偶尔', 2, 'yinxu', 2, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303236, 0x30303030303030623030303030313333, 3, '有时', 3, 'yinxu', 3, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303237, 0x30303030303030623030303030313333, 4, '经常', 4, 'yinxu', 4, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030343030303238, 0x30303030303030623030303030313333, 5, '总是', 5, 'yinxu', 5, '2026-04-03 17:09:26', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303031, 0x30303030303030623030303030313334, 1, '不是', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303032, 0x30303030303030623030303030313334, 2, '轻微', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303033, 0x30303030303030623030303030313334, 3, '一般', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303034, 0x30303030303030623030303030313334, 4, '比较', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303035, 0x30303030303030623030303030313334, 5, '非常', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303036, 0x30303030303030623030303030313335, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303037, 0x30303030303030623030303030313335, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303038, 0x30303030303030623030303030313335, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303039, 0x30303030303030623030303030313335, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303061, 0x30303030303030623030303030313335, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303062, 0x30303030303030623030303030313336, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303063, 0x30303030303030623030303030313336, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303064, 0x30303030303030623030303030313336, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303065, 0x30303030303030623030303030313336, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303066, 0x30303030303030623030303030313336, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303130, 0x30303030303030623030303030313337, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303131, 0x30303030303030623030303030313337, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303132, 0x30303030303030623030303030313337, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303133, 0x30303030303030623030303030313337, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303134, 0x30303030303030623030303030313337, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303135, 0x30303030303030623030303030313338, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303136, 0x30303030303030623030303030313338, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303137, 0x30303030303030623030303030313338, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303138, 0x30303030303030623030303030313338, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303139, 0x30303030303030623030303030313338, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303161, 0x30303030303030623030303030313339, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303162, 0x30303030303030623030303030313339, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303163, 0x30303030303030623030303030313339, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303164, 0x30303030303030623030303030313339, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303165, 0x30303030303030623030303030313339, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303166, 0x30303030303030623030303030313430, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303230, 0x30303030303030623030303030313430, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303231, 0x30303030303030623030303030313430, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303232, 0x30303030303030623030303030313430, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303233, 0x30303030303030623030303030313430, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303234, 0x30303030303030623030303030313431, 1, '从不', 1, 'tanshi', 1, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303235, 0x30303030303030623030303030313431, 2, '偶尔', 2, 'tanshi', 2, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303236, 0x30303030303030623030303030313431, 3, '有时', 3, 'tanshi', 3, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303237, 0x30303030303030623030303030313431, 4, '经常', 4, 'tanshi', 4, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030353030303238, 0x30303030303030623030303030313431, 5, '总是', 5, 'tanshi', 5, '2026-04-03 17:09:45', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303031, 0x30303030303030623030303030313432, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303032, 0x30303030303030623030303030313432, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303033, 0x30303030303030623030303030313432, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303034, 0x30303030303030623030303030313432, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303035, 0x30303030303030623030303030313432, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303036, 0x30303030303030623030303030313433, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303037, 0x30303030303030623030303030313433, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303038, 0x30303030303030623030303030313433, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303039, 0x30303030303030623030303030313433, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303061, 0x30303030303030623030303030313433, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303062, 0x30303030303030623030303030313434, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303063, 0x30303030303030623030303030313434, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303064, 0x30303030303030623030303030313434, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303065, 0x30303030303030623030303030313434, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303066, 0x30303030303030623030303030313434, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303130, 0x30303030303030623030303030313435, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303131, 0x30303030303030623030303030313435, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303132, 0x30303030303030623030303030313435, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303133, 0x30303030303030623030303030313435, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303134, 0x30303030303030623030303030313435, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303135, 0x30303030303030623030303030313436, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303136, 0x30303030303030623030303030313436, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303137, 0x30303030303030623030303030313436, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303138, 0x30303030303030623030303030313436, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303139, 0x30303030303030623030303030313436, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303161, 0x30303030303030623030303030313437, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303162, 0x30303030303030623030303030313437, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303163, 0x30303030303030623030303030313437, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303164, 0x30303030303030623030303030313437, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303165, 0x30303030303030623030303030313437, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303166, 0x30303030303030623030303030313438, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303230, 0x30303030303030623030303030313438, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303231, 0x30303030303030623030303030313438, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303232, 0x30303030303030623030303030313438, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303233, 0x30303030303030623030303030313438, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303234, 0x30303030303030623030303030313439, 1, '从不', 1, 'shire', 1, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303235, 0x30303030303030623030303030313439, 2, '偶尔', 2, 'shire', 2, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303236, 0x30303030303030623030303030313439, 3, '有时', 3, 'shire', 3, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303237, 0x30303030303030623030303030313439, 4, '经常', 4, 'shire', 4, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030363030303238, 0x30303030303030623030303030313439, 5, '总是', 5, 'shire', 5, '2026-04-03 17:10:11', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303031, 0x30303030303030623030303030313530, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303032, 0x30303030303030623030303030313530, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303033, 0x30303030303030623030303030313530, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303034, 0x30303030303030623030303030313530, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303035, 0x30303030303030623030303030313530, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303036, 0x30303030303030623030303030313531, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303037, 0x30303030303030623030303030313531, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303038, 0x30303030303030623030303030313531, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303039, 0x30303030303030623030303030313531, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303061, 0x30303030303030623030303030313531, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303062, 0x30303030303030623030303030313532, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303063, 0x30303030303030623030303030313532, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303064, 0x30303030303030623030303030313532, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303065, 0x30303030303030623030303030313532, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303066, 0x30303030303030623030303030313532, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303130, 0x30303030303030623030303030313533, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303131, 0x30303030303030623030303030313533, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303132, 0x30303030303030623030303030313533, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303133, 0x30303030303030623030303030313533, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303134, 0x30303030303030623030303030313533, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303135, 0x30303030303030623030303030313534, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303136, 0x30303030303030623030303030313534, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303137, 0x30303030303030623030303030313534, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303138, 0x30303030303030623030303030313534, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303139, 0x30303030303030623030303030313534, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303161, 0x30303030303030623030303030313535, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303162, 0x30303030303030623030303030313535, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303163, 0x30303030303030623030303030313535, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303164, 0x30303030303030623030303030313535, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303165, 0x30303030303030623030303030313535, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303166, 0x30303030303030623030303030313536, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303230, 0x30303030303030623030303030313536, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303231, 0x30303030303030623030303030313536, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303232, 0x30303030303030623030303030313536, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303233, 0x30303030303030623030303030313536, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303234, 0x30303030303030623030303030313537, 1, '从不', 1, 'yuxu', 1, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303235, 0x30303030303030623030303030313537, 2, '偶尔', 2, 'yuxu', 2, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303236, 0x30303030303030623030303030313537, 3, '有时', 3, 'yuxu', 3, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303237, 0x30303030303030623030303030313537, 4, '经常', 4, 'yuxu', 4, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030373030303238, 0x30303030303030623030303030313537, 5, '总是', 5, 'yuxu', 5, '2026-04-03 17:10:28', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303031, 0x30303030303030623030303030313538, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303032, 0x30303030303030623030303030313538, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303033, 0x30303030303030623030303030313538, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303034, 0x30303030303030623030303030313538, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303035, 0x30303030303030623030303030313538, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303036, 0x30303030303030623030303030313539, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303037, 0x30303030303030623030303030313539, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303038, 0x30303030303030623030303030313539, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303039, 0x30303030303030623030303030313539, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303061, 0x30303030303030623030303030313539, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303062, 0x30303030303030623030303030313630, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303063, 0x30303030303030623030303030313630, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303064, 0x30303030303030623030303030313630, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303065, 0x30303030303030623030303030313630, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303066, 0x30303030303030623030303030313630, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303130, 0x30303030303030623030303030313631, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303131, 0x30303030303030623030303030313631, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303132, 0x30303030303030623030303030313631, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303133, 0x30303030303030623030303030313631, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303134, 0x30303030303030623030303030313631, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303135, 0x30303030303030623030303030313632, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303136, 0x30303030303030623030303030313632, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303137, 0x30303030303030623030303030313632, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303138, 0x30303030303030623030303030313632, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303139, 0x30303030303030623030303030313632, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303161, 0x30303030303030623030303030313633, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303162, 0x30303030303030623030303030313633, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303163, 0x30303030303030623030303030313633, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303164, 0x30303030303030623030303030313633, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303165, 0x30303030303030623030303030313633, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303166, 0x30303030303030623030303030313634, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303230, 0x30303030303030623030303030313634, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303231, 0x30303030303030623030303030313634, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303232, 0x30303030303030623030303030313634, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303233, 0x30303030303030623030303030313634, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303234, 0x30303030303030623030303030313635, 1, '从不', 1, 'qiyu', 1, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303235, 0x30303030303030623030303030313635, 2, '偶尔', 2, 'qiyu', 2, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303236, 0x30303030303030623030303030313635, 3, '有时', 3, 'qiyu', 3, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303237, 0x30303030303030623030303030313635, 4, '经常', 4, 'qiyu', 4, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030383030303238, 0x30303030303030623030303030313635, 5, '总是', 5, 'qiyu', 5, '2026-04-03 17:11:33', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303031, 0x30303030303030623030303030313636, 1, '没有', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303032, 0x30303030303030623030303030313636, 2, '轻微', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303033, 0x30303030303030623030303030313636, 3, '一般', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303034, 0x30303030303030623030303030313636, 4, '比较', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303035, 0x30303030303030623030303030313636, 5, '严重', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303036, 0x30303030303030623030303030313637, 1, '从不', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303037, 0x30303030303030623030303030313637, 2, '偶尔', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303038, 0x30303030303030623030303030313637, 3, '有时', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303039, 0x30303030303030623030303030313637, 4, '经常', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303061, 0x30303030303030623030303030313637, 5, '总是', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303062, 0x30303030303030623030303030313638, 1, '从不', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303063, 0x30303030303030623030303030313638, 2, '偶尔', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303064, 0x30303030303030623030303030313638, 3, '有时', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303065, 0x30303030303030623030303030313638, 4, '经常', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303066, 0x30303030303030623030303030313638, 5, '总是', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303130, 0x30303030303030623030303030313639, 1, '从不', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303131, 0x30303030303030623030303030313639, 2, '偶尔', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303132, 0x30303030303030623030303030313639, 3, '有时', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303133, 0x30303030303030623030303030313639, 4, '经常', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303134, 0x30303030303030623030303030313639, 5, '总是', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303135, 0x30303030303030623030303030313730, 1, '从不', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303136, 0x30303030303030623030303030313730, 2, '偶尔', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303137, 0x30303030303030623030303030313730, 3, '有时', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303138, 0x30303030303030623030303030313730, 4, '经常', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303139, 0x30303030303030623030303030313730, 5, '总是', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303161, 0x30303030303030623030303030313731, 1, '从不', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303162, 0x30303030303030623030303030313731, 2, '偶尔', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303163, 0x30303030303030623030303030313731, 3, '有时', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303164, 0x30303030303030623030303030313731, 4, '经常', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303165, 0x30303030303030623030303030313731, 5, '总是', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303166, 0x30303030303030623030303030313732, 1, '从不', 1, 'tebing', 1, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303230, 0x30303030303030623030303030313732, 2, '偶尔', 2, 'tebing', 2, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303231, 0x30303030303030623030303030313732, 3, '有时', 3, 'tebing', 3, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303232, 0x30303030303030623030303030313732, 4, '经常', 4, 'tebing', 4, '2026-04-03 17:11:57', NULL);
INSERT INTO `tl_wis_fitness_option` VALUES (0x30303030303030633030393030303233, 0x30303030303030623030303030313732, 5, '总是', 5, 'tebing', 5, '2026-04-03 17:11:57', NULL);

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
-- Records of tl_wis_fitness_question
-- ----------------------------
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303031, 1, '您的年龄范围是？', NULL, '基础信息', '基本信息', 1, 1, 1, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303032, 2, '您的性别是？', NULL, '基础信息', '基本信息', 1, 1, 2, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303033, 3, '您是否经常感到精力充沛？', '指身体充满力量，不容易疲劳', '平和质', '精神状态', 1, 1, 3, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303034, 4, '您是否容易疲乏无力？', '容易感到疲劳，没有力气', '气虚质', '生理表现', 1, 1, 4, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303035, 5, '您是否怕冷，手脚经常冰凉？', '畏寒怕冷，四肢不温', '阳虚质', '生理表现', 1, 1, 5, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303036, 6, '您是否经常感到口干咽燥？', '口干想喝水，喉咙干燥', '阴虚质', '生理表现', 1, 1, 6, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303037, 7, '您的体型是否偏胖，腹部肥满？', '腹部松软肥满', '痰湿质', '形体特征', 1, 1, 7, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303038, 8, '您是否容易脸上出油、长痘？', '面部油腻，易生痤疮', '湿热质', '形体特征', 1, 1, 8, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303039, 9, '您的皮肤是否偏暗，有色斑或瘀青不易消退？', '肤色晦暗，有色素沉着', '血瘀质', '形体特征', 1, 1, 9, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303061, 10, '您是否经常感到情绪低落、忧郁？', '情志抑郁，多愁善感', '气郁质', '心理特征', 1, 1, 10, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303062, 11, '您是否有过敏史（鼻炎、皮肤过敏、食物过敏）？', '过敏性鼻炎、荨麻疹等', '特禀质', '发病倾向', 1, 1, 11, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303063, 12, '您是否容易出汗，动则汗出？', '稍微活动就出汗较多', '气虚质', '生理表现', 1, 1, 12, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303064, 13, '您是否喜欢温暖的环境，畏寒喜暖？', NULL, '阳虚质', '生理表现', 1, 1, 13, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303065, 14, '您的手足心是否经常发热？', NULL, '阴虚质', '生理表现', 1, 1, 14, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303066, 15, '您是否口中黏腻，舌苔厚腻？', NULL, '痰湿质', '生理表现', 1, 1, 15, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303130, 16, '您是否口苦，舌苔发黄？', NULL, '湿热质', '生理表现', 1, 1, 16, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303131, 17, '您是否容易健忘，记忆力下降？', NULL, '血瘀质', '精神状态', 1, 1, 17, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303132, 18, '您是否对事过于敏感，易焦虑不安？', NULL, '气郁质', '心理特征', 1, 1, 18, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23', 1, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303230, 1, '您的年龄范围是？', NULL, '基础信息', '基本信息', 1, 1, 1, 0, 0, '2026-04-03 14:09:43', '2026-04-03 14:09:43', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303231, 2, '您的性别是？', NULL, '基础信息', '基本信息', 1, 1, 2, 0, 0, '2026-04-03 14:09:43', '2026-04-03 14:09:43', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030303232, 3, '您的体型属于哪种？', NULL, '基础信息', '基本信息', 1, 1, 3, 0, 0, '2026-04-03 14:09:43', '2026-04-03 14:09:43', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313033, 3, '您是否经常感到精力充沛？', '指身体充满力量，不容易疲劳', '平和质', '精神状态', 1, 1, 3, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313034, 4, '您的面色是否红润有光泽？', '指面部肤色健康，有光泽', '平和质', '形体特征', 1, 1, 4, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313035, 5, '您的食欲是否良好，进食正常？', '指胃口好，饮食规律', '平和质', '生理表现', 1, 1, 5, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313036, 6, '您的睡眠质量如何？', '指入睡快，睡眠安稳，醒后精神好', '平和质', '生理表现', 1, 1, 6, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313037, 7, '您对自然环境和社会环境的适应能力如何？', '指对气候变化、环境改变适应良好', '平和质', '适应能力', 1, 1, 7, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313038, 8, '您是否经常情绪不稳定，容易烦躁？', NULL, '平和质', '心理特征', 1, 1, 8, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 1, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313039, 9, '您的舌象是否正常？', '指舌质淡红，舌苔薄白', '平和质', '形体特征', 1, 1, 9, 0, 0, '2026-04-03 17:07:25', '2026-04-03 17:07:25', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313130, 10, '您是否容易疲乏无力？', '容易感到疲劳，没有力气', '气虚质', '生理表现', 1, 1, 10, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313131, 11, '您是否气短懒言，不想说话？', '指说话声音小，不想多说话', '气虚质', '生理表现', 1, 1, 11, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313132, 12, '您是否容易出汗，动则汗出？', '稍微活动就出汗较多', '气虚质', '生理表现', 1, 1, 12, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313133, 13, '您是否容易感冒？', '指每年感冒次数较多', '气虚质', '发病倾向', 1, 1, 13, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313134, 14, '您的声音是否低弱无力？', '指说话声音小，没有力气', '气虚质', '生理表现', 1, 1, 14, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313135, 15, '您的肌肉是否松软不实？', '指肌肉不结实，力量不足', '气虚质', '形体特征', 1, 1, 15, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313136, 16, '您的大便是否溏薄不成形？', '指大便稀软，不成形', '气虚质', '生理表现', 1, 1, 16, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313137, 17, '您是否经常精神不振，容易疲劳？', NULL, '气虚质', '精神状态', 1, 1, 17, 0, 0, '2026-04-03 17:08:03', '2026-04-03 17:08:03', 2, 1.00, 1, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313138, 18, '您是否怕冷，手脚经常冰凉？', '畏寒怕冷，四肢不温', '阳虚质', '生理表现', 1, 1, 18, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313139, 19, '您是否喜欢温暖的环境，畏寒喜暖？', NULL, '阳虚质', '生理表现', 1, 1, 19, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313230, 20, '您是否喜欢热饮热食？', '指喜欢吃热的食物和饮料', '阳虚质', '生理表现', 1, 1, 20, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313231, 21, '您是否经常精神萎靡不振？', '指精神状态不佳，缺乏活力', '阳虚质', '精神状态', 1, 1, 21, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313232, 22, '您的大便是否稀溏，容易腹泻？', '指大便稀软，容易腹泻', '阳虚质', '生理表现', 1, 1, 22, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313233, 23, '您的小便是否清长量多？', '指小便颜色清，量多', '阳虚质', '生理表现', 1, 1, 23, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313234, 24, '您的性欲是否减退？', '指性欲较低', '阳虚质', '生理表现', 1, 1, 24, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313235, 25, '您是否经常腰膝酸软？', '指腰部和膝盖酸软无力', '阳虚质', '生理表现', 1, 1, 25, 0, 0, '2026-04-03 17:08:21', '2026-04-03 17:08:21', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313236, 26, '您是否经常感到口干咽燥？', '口干想喝水，喉咙干燥', '阴虚质', '生理表现', 1, 1, 26, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313237, 27, '您的手足心是否经常发热？', NULL, '阴虚质', '生理表现', 1, 1, 27, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313238, 28, '您是否容易盗汗？', '指夜间睡眠时出汗', '阴虚质', '生理表现', 1, 1, 28, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313239, 29, '您是否容易心烦易怒？', '指情绪不稳定，容易发火', '阴虚质', '心理特征', 1, 1, 29, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313330, 30, '您的大便是否干结难解？', '指大便干燥，排便困难', '阴虚质', '生理表现', 1, 1, 30, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313331, 31, '您的小便是否短少色黄？', '指小便量少，颜色偏黄', '阴虚质', '生理表现', 1, 1, 31, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313332, 32, '您的两颧是否经常潮红？', '指脸颊部位经常发红', '阴虚质', '形体特征', 1, 1, 32, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313333, 33, '您的形体是否偏瘦？', '指体型偏瘦，肌肉较少', '阴虚质', '形体特征', 1, 1, 33, 0, 0, '2026-04-03 17:09:16', '2026-04-03 17:09:16', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313334, 34, '您的体型是否偏胖，腹部肥满？', '腹部松软肥满', '痰湿质', '形体特征', 1, 1, 34, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313335, 35, '您是否口中黏腻，舌苔厚腻？', NULL, '痰湿质', '生理表现', 1, 1, 35, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313336, 36, '您是否胸闷痰多？', '指胸部闷胀，痰多', '痰湿质', '生理表现', 1, 1, 36, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313337, 37, '您是否经常感到身重不爽？', '指身体沉重，感觉不舒适', '痰湿质', '生理表现', 1, 1, 37, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313338, 38, '您的眼胞是否微浮？', '指眼睑轻微浮肿', '痰湿质', '形体特征', 1, 1, 38, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313339, 39, '您的舌苔是否厚腻？', '指舌苔厚而油腻', '痰湿质', '形体特征', 1, 1, 39, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313430, 40, '您是否嗜食肥甘厚味？', '指喜欢吃油腻、甜食', '痰湿质', '生理表现', 1, 1, 40, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313431, 41, '您是否容易困倦，精神不振？', NULL, '痰湿质', '精神状态', 1, 1, 41, 0, 0, '2026-04-03 17:09:35', '2026-04-03 17:09:35', 2, 1.00, 1, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313432, 42, '您是否容易脸上出油、长痘？', '面部油腻，易生痤疮', '湿热质', '形体特征', 1, 1, 42, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313433, 43, '您是否口苦，舌苔发黄？', NULL, '湿热质', '生理表现', 1, 1, 43, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313434, 44, '您是否经常感到身重困倦？', '指身体沉重，容易疲倦', '湿热质', '生理表现', 1, 1, 44, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313435, 45, '您的大便是否黏滞不爽？', '指大便黏腻，排便不爽', '湿热质', '生理表现', 1, 1, 45, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313436, 46, '您的小便是否短少色赤？', '指小便量少，颜色偏红', '湿热质', '生理表现', 1, 1, 46, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313437, 47, '女性：您的带下是否量多色黄？男性：您的阴囊是否潮湿？', '指女性白带量多色黄，男性阴囊潮湿', '湿热质', '生理表现', 1, 1, 47, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313438, 48, '您是否容易皮肤瘙痒？', '指皮肤容易发痒', '湿热质', '生理表现', 1, 1, 48, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313439, 49, '您是否容易患疮疖、痤疮？', NULL, '湿热质', '发病倾向', 1, 1, 49, 0, 0, '2026-04-03 17:10:02', '2026-04-03 17:10:02', 2, 1.00, 1, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313530, 50, '您的皮肤是否偏暗，有色斑或瘀青不易消退？', '肤色晦暗，有色素沉着', '血瘀质', '形体特征', 1, 1, 50, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313531, 51, '您是否容易健忘，记忆力下降？', NULL, '血瘀质', '精神状态', 1, 1, 51, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313532, 52, '女性：您是否痛经？男性：您是否有固定部位的疼痛？', '指女性经期腹痛，男性有固定疼痛', '血瘀质', '生理表现', 1, 1, 52, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313533, 53, '您的舌质是否紫黯或有瘀斑？', '指舌色发暗，有瘀点瘀斑', '血瘀质', '形体特征', 1, 1, 53, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313534, 54, '您的肌肤是否粗糙如鳞甲？', '指皮肤粗糙，像鱼鳞', '血瘀质', '形体特征', 1, 1, 54, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313535, 55, '您是否有固定部位的刺痛？', '指疼痛部位固定，如针刺', '血瘀质', '生理表现', 1, 1, 55, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313536, 56, '您的面色是否晦黯？', '指面色发暗，没有光泽', '血瘀质', '形体特征', 1, 1, 56, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313537, 57, '您是否容易有瘀斑、瘀点？', NULL, '血瘀质', '形体特征', 1, 1, 57, 0, 0, '2026-04-03 17:10:18', '2026-04-03 17:10:18', 2, 1.00, 1, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313538, 58, '您是否经常感到情绪低落、忧郁？', '情志抑郁，多愁善感', '气郁质', '心理特征', 1, 1, 58, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313539, 59, '您是否对事过于敏感，易焦虑不安？', NULL, '气郁质', '心理特征', 1, 1, 59, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313630, 60, '您是否经常善太息（叹气）？', '指经常叹气，感觉胸闷', '气郁质', '生理表现', 1, 1, 60, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313631, 61, '您是否经常胸胁胀满？', '指胸部和肋部感觉胀满', '气郁质', '生理表现', 1, 1, 61, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313632, 62, '您是否经常感觉咽喉有异物感？', '指喉咙感觉有东西，吞不下吐不出', '气郁质', '生理表现', 1, 1, 62, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313633, 63, '您的睡眠是否不安，多梦？', '指睡眠质量差，容易做梦', '气郁质', '生理表现', 1, 1, 63, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313634, 64, '您是否多愁善感，容易伤感？', '指容易情绪低落，感伤', '气郁质', '心理特征', 1, 1, 64, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313635, 65, '您是否性格内向，不愿与人交往？', NULL, '气郁质', '心理特征', 1, 1, 65, 0, 0, '2026-04-03 17:11:23', '2026-04-03 17:11:23', 2, 1.00, 1, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313636, 66, '您是否有过敏史（鼻炎、皮肤过敏、食物过敏）？', '过敏性鼻炎、荨麻疹等', '特禀质', '发病倾向', 1, 1, 66, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313637, 67, '您是否容易起荨麻疹？', '指皮肤容易起风团', '特禀质', '发病倾向', 1, 1, 67, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313638, 68, '您是否容易鼻塞、喷嚏？', '指过敏性鼻炎症状', '特禀质', '发病倾向', 1, 1, 68, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313639, 69, '您是否容易哮喘？', '指支气管哮喘', '特禀质', '发病倾向', 1, 1, 69, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313730, 70, '您的皮肤是否有划痕阳性？', '指皮肤划过后出现红肿隆起', '特禀质', '发病倾向', 1, 1, 70, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313731, 71, '您是否容易药物过敏？', '指对某些药物过敏', '特禀质', '发病倾向', 1, 1, 71, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 0, 0);
INSERT INTO `tl_wis_fitness_question` VALUES (0x30303030303030623030303030313732, 72, '您的家族是否有过敏史？', '指父母或兄弟姐妹有过敏史', '特禀质', '发病倾向', 1, 1, 72, 0, 0, '2026-04-03 17:11:44', '2026-04-03 17:11:44', 2, 1.00, 1, 0);

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
-- Records of tl_wis_fitness_record
-- ----------------------------

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
-- Records of tl_wis_fitness_type
-- ----------------------------
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303031, 'pinghe', '平和质', 'Balanced Constitution', '阴阳气血调和，体态适中，面色润泽，精力充沛', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303032, 'qixu', '气虚质', 'Qi Deficiency', '元气不足，容易疲乏，气短懒言，易出汗', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303033, 'yangxu', '阳虚质', 'Yang Deficiency', '阳气不足，畏寒怕冷，手足不温，精神不振', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 3, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303034, 'yinxu', '阴虚质', 'Yin Deficiency', '阴液亏少，口燥咽干，手足心热，形体偏瘦', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 4, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303035, 'tanshi', '痰湿质', 'Phlegm-Dampness', '痰湿凝聚，形体肥胖，腹部肥满，口黏苔腻', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 5, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303036, 'shire', '湿热质', 'Damp-Heat', '湿热内蕴，面垢油光，口苦苔黄，易生痤疮', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 6, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303037, 'yuxu', '血瘀质', 'Blood Stasis', '血行不畅，肤色晦黯，色素沉着，易见瘀斑', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 7, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303038, 'qiyu', '气郁质', 'Qi Stagnation', '气机郁滞，情志抑郁，忧虑脆弱，性格内向', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 8, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');
INSERT INTO `tl_wis_fitness_type` VALUES (0x30303030303030613030303030303039, 'tebing', '特禀质', 'Special Constitution', '先天失常，以过敏性疾病为主', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 9, 0, 0, '2026-03-22 17:33:23', '2026-03-22 17:33:23');

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
-- Records of tl_wis_food
-- ----------------------------
INSERT INTO `tl_wis_food` VALUES (0x38316439643831653333326434393862, '枸杞子', 'gou qi zi', 5, 3, '甘、平', '归肝、肾经', '滋补肝肾', NULL, NULL, NULL, NULL, 'https://modao.cc/agent-py/media/generated_images/2026-03-19/d362c3036e7c4995ac1af6ed687c80e3.jpg', 0, 0, 1, 0, 0, '2026-03-20 13:04:21', '2026-03-20 13:07:13');
INSERT INTO `tl_wis_food` VALUES (0x38316439646566626362326262303533, '人参', 'ren shen', 5, 4, '甘、微苦，温', '归脾、肺、心经', '大补元气', NULL, NULL, NULL, NULL, 'https://modao.cc/agent-py/media/generated_images/2026-03-19/346ac8aae56546f6acda29a88e103ee8.jpg', 0, 0, 2, 0, 0, '2026-03-20 13:04:21', '2026-03-20 13:07:20');
INSERT INTO `tl_wis_food` VALUES (0x38316439653361663063646233323565, '茯苓', 'fu ling', 5, 3, '甘、淡，平', '归心、肺、脾、肾经', '利水渗湿', NULL, NULL, NULL, NULL, 'https://modao.cc/agent-py/media/generated_images/2026-03-19/65b8f535059840558e83abce930e6825.jpg', 0, 0, 3, 0, 0, '2026-03-20 13:04:21', '2026-03-20 13:07:28');
INSERT INTO `tl_wis_food` VALUES (0x38316439656263363962383366613663, '大枣', 'da zao', 5, 4, '甘、温', '归脾、胃、心经', '补中益气', NULL, NULL, NULL, NULL, 'https://modao.cc/agent-py/media/generated_images/2026-03-19/3e3dd23c56274672bfd2cd71358bc77a.jpg', 0, 0, 4, 0, 0, '2026-03-20 13:04:21', '2026-03-20 13:07:36');
INSERT INTO `tl_wis_food` VALUES (0x38316461303736383861666636373561, '山药', 'shan yao', 5, 3, '甘，平', '归脾、肺、肾经', '益气养阴', NULL, NULL, NULL, NULL, 'https://modao.cc/agent-py/media/generated_images/2026-03-19/f6a5e813f0974256ba7f1278fe74432a.jpg', 0, 0, 5, 0, 0, '2026-03-20 13:04:21', '2026-03-20 13:07:44');
INSERT INTO `tl_wis_food` VALUES (0x38316461306638666637363733626161, '刺五加', 'ci wu jia', 5, 4, '辛、苦、微温', '归脾、肾、心经', '宁心安神', NULL, NULL, NULL, NULL, 'https://modao.cc/agent-py/media/generated_images/2026-03-19/1b04ff8ceffd49e2a4837763f13f78b3.jpg', 0, 0, 6, 0, 0, '2026-03-20 13:04:21', '2026-03-20 13:07:47');

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
-- Records of tl_wis_meridian
-- ----------------------------
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303031, 'hand_taiyin', '手太阴肺经', 'shou taiyin fei jing', 1, '起于中焦，下络大肠，还循胃口，上膈属肺', '起于中焦，下络大肠，还循胃口，上膈属肺，从肺系横出腋下，下循臑内，行少阴，心主之前，下肘中，循臂内上骨下廉，入寸口，上鱼，循鱼际，出大指之端', '肺系疾患、咽喉病、上肢内侧病', NULL, 1, '#FF6B6B', 3.00, 1, 1, 0, 0, 1, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303032, 'hand_jueyin', '手厥阴心包经', 'shou jueyin xin bao jing', 1, '起于胸中，出属心包络，下膈，历络三焦', '起于胸中，出属心包络，下膈，历络三焦，其支者循胸出胁，下腋三寸，上抵腋，下循臑内，循肘下行，入肘中，下臂，行两筋之间，入掌中，循中指，出其端', '心胸疾患、胃病，神志病', NULL, 1, '#FF8C00', 3.00, 1, 1, 0, 0, 2, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303033, 'hand_shaoyin', '手少阴心经', 'shou shaoyin xin jing', 1, '起于心中，出属心系，下膈，络小肠', '起于心中，出属心系，下膈，络小肠，其支者从心系上挟咽，系目系，其直者复从心系却上肺，下出腋下，下循臑内后廉，行太阴，心主之后，下肘内，循臂内后廉，抵掌后锐骨之端，入掌内后廉，循小指之内出其端', '心胸疾患，神志病、舌病', NULL, 1, '#FF4500', 3.00, 1, 1, 0, 0, 3, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303034, 'hand_yangming', '手阳明大肠经', 'shou yangming da chang jing', 1, '起于大指次指之端，循指上廉，出合谷两骨之间', '起于大指次指之端，循指上廉，出合谷两骨之间，上入两筋之中，循臂上廉，入肘外廉，上臑外前廉，上肩，出髃骨之前廉，上出于柱骨之会上，下入缺盆，络肺，下膈，属大肠', '头面疾患、咽喉病、热病', NULL, 1, '#32CD32', 3.00, 1, 1, 0, 0, 4, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303035, 'hand_shaoyang', '手少阳三焦经', 'shou shaoyang san jiao jing', 1, '起于小指次指之端，上出两指之间', '起于小指次指之端，上出两指之间，循手表腕，出臂外两骨之间，上贯肘，循臑外上肩，而交出足少阳之后，入缺盆，布膻中，散络心包，下膈，循属三焦', '耳聋、耳鸣、咽喉肿痛、胁痛', NULL, 1, '#00CED1', 3.00, 1, 1, 0, 0, 5, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303036, 'hand_taiyang', '手太阳小肠经', 'shou taiyang xiao chang jing', 1, '起于小指之端，循手外侧上腕，出踝中', '起于小指之端，循手外侧上腕，出踝中，直上循臂骨下廉，出肘内侧两筋之间，上循臑外后廉，出肩解，绕肩胛，交肩上，入缺盆，络心，循咽下膈，抵胃，属小肠', '头颈疾患、咽喉病、热病', NULL, 1, '#1E90FF', 3.00, 1, 1, 0, 0, 6, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303037, 'foot_yangming', '足阳明胃经', 'zu yangming wei jing', 1, '起于鼻之交頞中，旁纳太阳之脉，下循鼻外', '起于鼻之交頞中，旁纳太阳之脉，下循鼻外，入上齿中，还出挟口环唇，下交承浆，却循颐后下廉，出大迎，循颊车，上耳前，过客主人，循发际，至额颅', '胃肠病、头面疾患，神志病', NULL, 1, '#FFA500', 3.00, 1, 1, 0, 0, 7, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303038, 'foot_shaoyang', '足少阳胆经', 'zu shaoyang dan jing', 1, '起于目锐眦，上抵头角，下耳后，循颈行手少阳之前', '起于目锐眦，上抵头角，下耳后，循颈行手少阳之前，至肩上却交出手少阳之后，入缺盆，其支者从耳后入耳中，出走耳前，至目锐眦后', '肝胆疾患、侧头疾患、眼病', NULL, 1, '#9ACD32', 3.00, 1, 1, 0, 0, 8, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303039, 'foot_taiyang', '足太阳膀胱经', 'zu taiyang pang guang jing', 1, '起于目内眦，上额，交巅', '起于目内眦，上额，交巅，其支者从巅至耳上角，其直者从巅入络脑，还出别下项，循肩髆内，挟脊抵腰中，入循膂，络肾，属膀胱', '头项疾患、背腰疾患、脏腑疾患', NULL, 1, '#4169E1', 3.00, 1, 1, 0, 0, 9, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303061, 'foot_taiyin', '足太阴脾经', 'zu taiyin pi jing', 1, '起于大趾之端，循趾内侧白肉际，过核骨后', '起于大趾之端，循趾内侧白肉际，过核骨后，上内踝前廉，上腨内，循胫骨后，交出厥阴之前，上膝股内前廉，入腹，属脾，络胃，上膈，挟咽，连舌本，散舌下', '脾胃病、妇科病、前阴病', NULL, 1, '#FF69B4', 3.00, 1, 1, 0, 0, 10, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303062, 'foot_jueyin', '足厥阴肝经', 'zu jueyin gan jing', 1, '起于大趾丛毛之际，上循足跗上廉', '起于大趾丛毛之际，上循足跗上廉，去内踝一寸，上踝八寸，交出太阴之后，上腘内廉，循股阴，入毛中，过阴器，抵小腹，挟胃属肝，络胆，上贯膈，布胁肋，循喉咙之后，上入颃颡，连目系，上出额，与督脉会于巅', '肝胆病、妇科病、前阴病', NULL, 1, '#DC143C', 3.00, 1, 1, 0, 0, 11, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303063, 'foot_shaoyin', '足少阴肾经', 'zu shaoyin shen jing', 1, '起于小趾之下，邪走足心，出于然谷之下', '起于小趾之下，邪走足心，出于然谷之下，循内踝之后，别入跟中，以上腨内，出腘内廉，上股内后廉，贯脊属肾，络膀胱，其直者从肾上贯肝膈，入肺中，循喉咙，挟舌本', '泌尿生殖系统疾病、肾虚证', NULL, 1, '#8B4513', 3.00, 1, 1, 0, 0, 12, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303064, 'ren', '任脉', 'ren mai', 2, '起于胞中，出于会阴，沿腹部正中线上行', '起于胞中，出于会阴，沿腹部正中线上行，经过阴部，到达腹部正中，沿胸部正中线上行，经过颈部，到达下颌，环绕口唇，分行至两目下', '妇科病、男科病、肠胃病', NULL, 1, '#FF1493', 3.00, 1, 1, 0, 0, 13, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');
INSERT INTO `tl_wis_meridian` VALUES (0x30303030303030643030303030303065, 'du', '督脉', 'du mai', 2, '起于胞中，出于会阴，沿背部正中线上行', '起于胞中，出于会阴，沿背部正中线上行，经过腰部、背部、颈部到头顶，前额到鼻柱，下行至龈交，与任脉相接', '神志病、腰背痛、头面疾患', NULL, 1, '#8A2BE2', 3.00, 1, 1, 0, 0, 14, 0, 0, '2026-03-24 17:27:32', '2026-03-24 17:27:32');

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
-- Records of tl_wis_solar_term
-- ----------------------------
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303031, '立春', 1, 2, 3, 2, 18, '春季养肝，宜疏肝理气', 1, NULL, NULL, '立春，为二十四节气之首。立，是\"开始\"之意；春，代表着温暖、生长。立春标志着万物闭藏的冬季已过去，开始进入风和日暖、万物生长的春季。', '气温回升，日照增加，但仍有倒春寒的可能', '[{\"title\":\"护阳养肝\",\"content\":\"春季属木，与肝相应，宜养肝护阳\"},{\"title\":\"防风御寒\",\"content\":\"初春寒气未消，注意保暖防风\"},{\"title\":\"适度运动\",\"content\":\"适合散步、慢跑、太极等舒缓运动\"}]', '[\"迎春\",\"咬春（吃春饼、萝卜）\",\"打春牛\"]', '[\"立春一年端，种地早盘算\",\"春打六九头，耕牛遍地走\"]', '宜食辛甘发散之品，如葱、香菜、花生等，少食酸收之味', 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303032, '雨水', 2, 2, 19, 3, 4, '雨水节气湿气重，注意保暖', 1, NULL, NULL, '雨水，是二十四节气之中的第2个节气。此时气温回升、冰雪融化、降水增多，故取名为雨水。', '降雨开始增多，气温逐渐升高', '[{\"title\":\"健脾祛湿\",\"content\":\"雨水湿气重，宜健脾利湿\"},{\"title\":\"养肝护脾\",\"content\":\"春应于肝，肝旺易克脾\"}]', '[\"拉保保（找干爹）\",\"回娘屋\",\"接寿\"]', '[\"雨水有雨庄稼好，大春小春一片宝\",\"雨水落了雨，阴阴沉沉到谷雨\"]', NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303033, '惊蛰', 3, 3, 5, 3, 19, '惊蛰万物复苏，宜养肝护脾', 1, NULL, NULL, '惊蛰，是二十四节气中的第3个节气。此时春雷始鸣，惊醒蛰伏于地下冬眠的昆虫，故称惊蛰。', '天气回暖，春雷初响，万物复苏', '[{\"title\":\"养肝护脾\",\"content\":\"惊蛰肝气旺盛，宜疏肝养脾\"},{\"title\":\"防病保健\",\"content\":\"传染病高发期，注意增强免疫力\"}]', '[\"祭白虎\",\"打小人\",\"吃梨\"]', '[\"惊蛰雷鸣，谷米成堆\",\"惊蛰不耙地，好比蒸馍走了气\"]', NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303034, '春分', 4, 3, 20, 4, 4, '春分阴阳平衡，宜疏肝理气', 1, NULL, NULL, '春分，是二十四节气之一，春季第四个节气。此日太阳直射赤道，昼夜等长。春分者，阴阳相半也，故昼夜均而寒暑平。', '昼夜平分，气候温和，雨水充沛', '[{\"title\":\"平衡阴阳\",\"content\":\"春分阴阳相半，宜保持阴阳平衡\"},{\"title\":\"疏肝理气\",\"content\":\"春季肝气旺，宜疏肝解郁\"},{\"title\":\"调和饮食\",\"content\":\"饮食宜清淡，忌偏热偏寒\"}]', '[\"竖蛋\",\"放风筝\",\"春祭\"]', '[\"春分麦起身，一刻值千金\",\"春分有雨到清明，清明下雨无路行\"]', NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303035, '清明', 5, 4, 4, 4, 19, '清明时节，宜踏青赏花', 1, NULL, NULL, '清明，是二十四节气中的第5个节气。此时天气晴朗，草木繁茂，是春耕春种的大好时节。', '天气晴朗，气温回升，草木萌动', '[{\"title\":\"养肝护肺\",\"content\":\"清明时节宜养肝护肺，防感冒\"},{\"title\":\"适度运动\",\"content\":\"适合踏青、登山等户外活动\"}]', '[\"扫墓祭祖\",\"踏青\",\"植树\",\"放风筝\"]', '[\"清明前后，种瓜点豆\",\"清明断雪，谷雨断霜\"]', NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303036, '谷雨', 6, 4, 20, 5, 4, '谷雨时节，健脾祛湿', 1, NULL, NULL, '谷雨，是二十四节气之第6个节气，春季最后一个节气。谷雨取自\"雨生百谷\"之意，此时降水明显增加，田中的秧苗初插、作物新种，最需要雨水的滋润。', '降雨增多，湿度增大，气温升高', '[{\"title\":\"健脾祛湿\",\"content\":\"谷雨湿气重，宜健脾利湿\"},{\"title\":\"疏肝养心\",\"content\":\"春末夏初，宜疏肝养心\"}]', '[\"采茶\",\"食香椿\",\"赏牡丹\",\"走谷雨\"]', '[\"谷雨前后，种瓜点豆\",\"清明断雪，谷雨断霜\"]', NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303037, '立夏', 7, 5, 5, 5, 20, '立夏养心，宜清心火', 2, NULL, NULL, '立夏，是二十四节气中的第7个节气，夏季的第一个节气。此时万物茂盛，气温显著升高。', '气温明显升高，雷雨增多', '[{\"title\":\"养心安神\",\"content\":\"夏应于心，宜养心安神\"},{\"title\":\"清热消暑\",\"content\":\"天气炎热，宜清淡饮食\"}]', '[\"称人\",\"斗蛋\",\"尝新\"]', '[\"立夏小满正栽秧\",\"立夏不下，旱到麦罢\"]', NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303038, '小满', 8, 5, 21, 5, 22, '小满时节，宜养心护脾', 2, NULL, NULL, '小满，二十四节气的第8个节气。小满节气意味着进入了大幅降水的雨季，雨水开始增多，夏熟作物的籽粒开始灌浆饱满。', NULL, '[{\"title\":\"清热利湿\",\"content\":\"湿气渐重，宜清热利湿\"},{\"title\":\"养心护脾\",\"content\":\"注意调养心脾\"}]', NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303039, '芒种', 9, 5, 22, 5, 6, '芒种时节，宜清热祛湿', 2, NULL, NULL, '芒种，二十四节气之第9个节气。芒种字面的意思是\"有芒的麦子快收，有芒的稻子可种\"。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303061, '夏至', 10, 6, 21, 6, 21, '夏至阳气最盛，宜养心安神', 2, NULL, NULL, '夏至，二十四节气之第10个节气。夏至这天，太阳直射北回归线，是北半球白昼最长的一天。夏至阳气最盛，但阴气已开始生长。', NULL, '[{\"title\":\"养心护阳\",\"content\":\"夏至阳盛，宜养心护阳\"},{\"title\":\"晚睡早起\",\"content\":\"顺应天时，适当晚睡早起\"}]', NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303062, '小暑', 11, 7, 7, 6, 23, '小暑时节，宜清心解暑', 2, NULL, NULL, '小暑，二十四节气之第11个节气。暑是炎热的意思，小暑为小热，还不十分热。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303063, '大暑', 12, 7, 23, 6, 7, '大暑炎热，宜防暑降温', 2, NULL, NULL, '大暑，二十四节气之第12个节气，一年中最热的节气。大暑相对小暑，更加炎热。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303064, '立秋', 13, 8, 7, 8, 23, '立秋养肺，宜润燥', 3, NULL, NULL, '立秋，二十四节气之第13个节气。立秋是秋季的第一个节气，标志着孟秋时节的正式开始。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303065, '处暑', 14, 8, 23, 9, 7, '处暑时节，宜滋阴润燥', 3, NULL, NULL, '处暑，二十四节气之第14个节气。处暑即为\"出暑\"，是炎热离开的意思。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303066, '白露', 15, 9, 7, 9, 23, '白露时节，宜润肺养阴', 3, NULL, NULL, '白露，二十四节气之第15个节气。此时天气渐凉，清晨露水日益加厚，凝结成一层白白的水滴，故称白露。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:32');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303130, '秋分', 16, 9, 23, 10, 8, '秋分阴阳平衡，宜养肺润燥', 3, NULL, NULL, '秋分，二十四节气之第16个节气。秋分之日昼夜等长，此后北半球昼短夜长。秋分者，阴阳相半也。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303131, '寒露', 17, 10, 8, 10, 23, '寒露时节，宜保暖防寒', 3, NULL, NULL, '寒露，二十四节气之第17个节气。寒露时气温比白露更低，地面的露水更冷，快要凝结成霜了。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303132, '霜降', 18, 10, 23, 11, 7, '霜降时节，宜温补阳气', 3, NULL, NULL, '霜降，二十四节气之第18个节气，秋季最后一个节气。霜降时节，天气渐寒，开始降霜。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303133, '立冬', 19, 11, 7, 11, 22, '立冬养肾，宜温补阳气', 4, NULL, NULL, '立冬，二十四节气之第19个节气。立冬表示冬季开始，万物收藏规避寒冷。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303134, '小雪', 20, 11, 22, 12, 7, '小雪时节，宜温补御寒', 4, NULL, NULL, '小雪，二十四节气之第20个节气。小雪阶段比入冬阶段气温低，开始降雪但雪量不大。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303135, '大雪', 21, 12, 7, 12, 21, '大雪时节，宜温阳补肾', 4, NULL, NULL, '大雪，二十四节气之第21个节气。大雪时节，雪量增大，天气更冷。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303136, '冬至', 22, 12, 22, 1, 5, '冬至阳气最弱，宜温补阳气', 4, NULL, NULL, '冬至，二十四节气之第22个节气。冬至这天，太阳直射南回归线，北半球白昼最短、黑夜最长。冬至是阴气最盛、阳气始生的转折点。', NULL, '[{\"title\":\"温补阳气\",\"content\":\"冬至一阳生，宜温补护阳\"},{\"title\":\"早睡晚起\",\"content\":\"顺应自然，早睡晚起避寒\"}]', NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303137, '小寒', 23, 1, 5, 1, 20, '小寒时节，宜温补散寒', 4, NULL, NULL, '小寒，二十四节气之第23个节气。小寒标志着一年中最寒冷日子的开始。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');
INSERT INTO `tl_wis_solar_term` VALUES (0x30303030303030643030303030303138, '大寒', 24, 1, 20, 2, 3, '大寒时节，宜温阳散寒', 4, NULL, NULL, '大寒，二十四节气之第24个节气，最后一个节气。大寒是天气寒冷到极致的意思。', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-04 12:41:23', '2026-04-20 13:39:33');

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

-- ----------------------------
-- Records of tl_wis_solar_term_card
-- ----------------------------
INSERT INTO `tl_wis_solar_term_card` VALUES (0x30303030303030643030303030313031, 0x30303030303030643030303030303034, 1, 1, 100, '春分饮食养生', '春分时节宜食青蔬，如菠菜、韭菜、春笋等，以养肝护肝', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, '2026-04-04 12:41:23', '2026-04-04 12:41:23');
INSERT INTO `tl_wis_solar_term_card` VALUES (0x30303030303030643030303030313032, 0x30303030303030643030303030303034, 2, 1, 90, '春分运动养生', '春分时节宜多去户外舒展身体，如散步、太极拳、放风筝等', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, '2026-04-04 12:41:23', '2026-04-04 12:41:23');
INSERT INTO `tl_wis_solar_term_card` VALUES (0x30303030303030643030303030313033, 0x30303030303030643030303030303034, 3, 1, 80, '春分情志养生', '春分时节宜保持心情舒畅，避免情绪波动过大', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, '2026-04-04 12:41:23', '2026-04-04 12:41:23');
INSERT INTO `tl_wis_solar_term_card` VALUES (0x30303030303030643030303030313034, 0x30303030303030643030303030303034, 4, 1, 70, '春分起居养生', '春分时节宜早睡早起，顺应阳气生发', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, '2026-04-04 12:41:23', '2026-04-04 12:41:23');
INSERT INTO `tl_wis_solar_term_card` VALUES (0x30303030303030643030303030313035, 0x30303030303030643030303030303035, 4, 1, 70, '清明起居养生', '春分时节宜早睡早起，顺应阳气生发', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 'https://modao.cc/agent-py/media/generated_images/2026-03-19/a6c37121f6964d6ba83e08439fc2937a.jpg', 0, '2026-04-04 12:41:23', '2026-04-05 15:39:16');

SET FOREIGN_KEY_CHECKS = 1;
