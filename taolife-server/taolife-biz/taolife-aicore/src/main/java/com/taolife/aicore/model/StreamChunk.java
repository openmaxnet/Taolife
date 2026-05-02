package com.taolife.aicore.model;

/**
 * 统一流式输出块
 *
 * @param type    块类型（THINKING/ANSWER/EMPTY）
 * @param content 块内容
 */
public record StreamChunk(ChunkType type, String content) {

    /**
     * 是否为空块
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return type == ChunkType.EMPTY;
    }

    /**
     * 流式块类型
     */
    public enum ChunkType {
        /** 思考过程 */
        THINKING,
        /** 正式回答 */
        ANSWER,
        /** 空块 */
        EMPTY
    }

    /**
     * 空块常量
     */
    public static final StreamChunk EMPTY = new StreamChunk(ChunkType.EMPTY, "");

    /**
     * 结束标记常量
     */
    public static final StreamChunk DONE = new StreamChunk(ChunkType.EMPTY, "[DONE]");

    /**
     * 创建思考过程块
     *
     * @param content 思考内容
     * @return 思考过程块
     */
    public static StreamChunk thinking(String content) {
        return new StreamChunk(ChunkType.THINKING, content);
    }

    /**
     * 创建正式回答块
     *
     * @param content 回答内容
     * @return 正式回答块
     */
    public static StreamChunk answer(String content) {
        return new StreamChunk(ChunkType.ANSWER, content);
    }
}
