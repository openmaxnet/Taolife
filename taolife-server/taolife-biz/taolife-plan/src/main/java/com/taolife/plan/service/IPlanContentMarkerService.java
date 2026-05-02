package com.taolife.plan.service;

/**
 * 方案内容标记服务接口
 * 在AI生成的方案内容中嵌入结构化标记，将食材/穴位名称替换为可点击链接格式
 *
 * @author 文二
 * @date 2026-04-17
 */
public interface IPlanContentMarkerService {

    /**
     * 处理食材方案内容，将食材名替换为 [name](food:id) 格式
     *
     * @param content AI生成的食材方案Markdown内容
     * @return 标记后的内容
     */
    String processFoodContent(String content);

    /**
     * 处理穴位方案内容，将穴位名替换为 [name](acupoint:id) 格式
     *
     * @param content AI生成的穴位方案Markdown内容
     * @return 标记后的内容
     */
    String processAcupointContent(String content);

    /**
     * 处理经络方案内容，将经络名替换为 [name](meridian:id) 格式
     *
     * @param content AI生成的经络方案Markdown内容
     * @return 标记后的内容
     */
    String processMeridianContent(String content);
}
