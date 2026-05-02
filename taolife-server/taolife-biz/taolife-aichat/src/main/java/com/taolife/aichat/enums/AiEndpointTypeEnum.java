package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * AI端点类型常量
 * 定义AI厂商API端点的类型分类，包含中文名称映射
 *
 * @author 文二
 * @date 2026-04-14
 */
@Getter
@AllArgsConstructor
public enum AiEndpointTypeEnum {

    /**
     * 模型API端点
     */
    MODEL_API("model_api", "模型API"),

    /**
     * 工具API端点
     */
    TOOL_API("tool_api", "工具API"),

    /**
     * Agent API端点
     */
    AGENT_API("agent_api", "Agent API"),

    /**
     * 文件API端点
     */
    FILE_API("file_api", "文件API"),

    /**
     * 批处理API端点
     */
    BATCH_API("batch_api", "批处理API"),

    /**
     * 知识库API端点
     */
    KNOWLEDGE_API("knowledge_api", "知识库API"),

    /**
     * 实时API端点
     */
    REALTIME_API("realtime_api", "实时API");

    /**
     * 端点类型代码
     */
    private final String code;

    /**
     * 端点类型中文名称
     */
    private final String name;

    /** 代码到名称的映射表 */
    private static final Map<String, String> CODE_NAME_MAP = new HashMap<>();

    static {
        for (AiEndpointTypeEnum e : values()) {
            CODE_NAME_MAP.put(e.code, e.name);
        }
    }

    /**
     * 根据code获取枚举
     *
     * @param code 端点类型代码
     * @return 枚举对象，未找到返回null
     */
    public static AiEndpointTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AiEndpointTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据code获取中文名称
     *
     * @param code 端点类型代码
     * @return 中文名称，未找到返回原始code
     */
    public static String getNameByCode(String code) {
        return CODE_NAME_MAP.getOrDefault(code, code);
    }
}