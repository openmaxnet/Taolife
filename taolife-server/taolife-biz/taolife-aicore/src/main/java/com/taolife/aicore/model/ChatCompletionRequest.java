package com.taolife.aicore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Chat Completion 请求模型（OpenAI API 兼容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表（支持多轮对话）
     */
    private List<Message> messages;

    /**
     * 温度参数，控制随机性（0-2）
     */
    private Double temperature;

    /**
     * 最大输出Token数
     */
    private Integer maxTokens;

    /**
     * Top-P 参数
     */
    private Double topP;

    /**
     * 是否启用思考/推理模式
     */
    private Boolean enableThinking;

    /**
     * 响应格式：设置为 json_object 时强制输出JSON
     */
    private Boolean jsonObjectResponse;

    /**
     * 厂商特定额外参数，透传给 VendorAdapter
     */
    private Map<String, Object> vendorParams;
}
