package com.taolife.aichat.service;

import com.taolife.aichat.param.AiModelPageParam;
import com.taolife.aichat.param.AiModelSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.vo.AiModelVO;
import com.taolife.common.utils.PageResult;

import java.util.List;

/**
 * AI模型服务接口
 *
 * @author 文二
 * @date 2026-04-14
 */
public interface IAiModelService {

    /**
     * 分页查询模型列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AiModelVO> getModelPage(AiModelPageParam param);

    /**
     * 获取模型详情
     *
     * @param id 模型ID
     * @return 模型详情
     */
    AiModelVO getModelDetail(String id);

    /**
     * 创建模型
     *
     * @param param 创建参数
     */
    void createModel(AiModelSaveParam param);

    /**
     * 修改模型信息
     *
     * @param param 修改参数
     */
    void modifyModelInfo(AiModelSaveParam param);

    /**
     * 删除模型
     *
     * @param param 删除参数
     */
    void removeModel(AiRemoveParam param);

    /**
     * 修改模型状态
     *
     * @param param 状态修改参数
     */
    void modifyModelStatus(AiStatusParam param);

    /**
     * 根据模型编码获取模型
     *
     * @param modelCode 模型编码
     * @return 模型
     */
    AiModelVO getModelByCode(String modelCode);

    /**
     * 根据厂商ID获取模型列表
     *
     * @param providerId 厂商ID
     * @return 模型列表
     */
    List<AiModelVO> getModelListByProviderId(String providerId);
}
