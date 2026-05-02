package com.taolife.aichat.service;

import com.taolife.aichat.param.AiPromptTemplatePageParam;
import com.taolife.aichat.param.AiPromptTemplateSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.vo.AiPromptTemplateVO;
import com.taolife.common.utils.PageResult;

import java.util.Map;

/**
 * AI提示词模板服务接口
 *
 * @author 文二
 * @date 2026-04-14
 */
public interface IAiPromptTemplateService {

    /**
     * 分页查询提示词模板列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AiPromptTemplateVO> getTemplatePage(AiPromptTemplatePageParam param);

    /**
     * 获取提示词模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    AiPromptTemplateVO getTemplateDetail(String id);

    /**
     * 创建提示词模板
     *
     * @param param 创建参数
     */
    void createTemplate(AiPromptTemplateSaveParam param);

    /**
     * 修改提示词模板信息
     *
     * @param param 修改参数
     */
    void modifyTemplateInfo(AiPromptTemplateSaveParam param);

    /**
     * 删除提示词模板
     *
     * @param param 删除参数
     */
    void removeTemplate(AiRemoveParam param);

    /**
     * 修改提示词模板状态
     *
     * @param param 状态修改参数
     */
    void modifyTemplateStatus(AiStatusParam param);

    /**
     * 根据模板编码获取最新版本的模板
     *
     * @param templateCode 模板编码
     * @return 模板
     */
    AiPromptTemplateVO getLatestByCode(String templateCode);

    /**
     * 渲染提示词模板（替换变量）
     *
     * @param templateCode 模板编码
     * @param variables    变量map
     * @return 渲染后的内容
     */
    String renderTemplate(String templateCode, Map<String, Object> variables);
}
