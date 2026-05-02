package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.AiPromptTemplate;
import com.taolife.aichat.enums.AiPromptVariablePattern;
import com.taolife.aichat.mapper.AiPromptTemplateMapper;
import com.taolife.aichat.param.AiPromptTemplatePageParam;
import com.taolife.aichat.param.AiPromptTemplateSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiPromptTemplateService;
import com.taolife.aichat.vo.AiPromptTemplateVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI提示词模板服务实现
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiPromptTemplateServiceImpl implements IAiPromptTemplateService {

    private final AiPromptTemplateMapper aiPromptTemplateMapper;

    /**
     * 分页查询提示词模板列表
     *
     * @param param 分页查询参数
     * @return 模板分页结果
     */
    @Override
    public PageResult<AiPromptTemplateVO> getTemplatePage(AiPromptTemplatePageParam param) {
        Page<AiPromptTemplate> page = aiPromptTemplateMapper.selectPageByParam(
                param.getPageNo(), param.getPageSize(), param.getTemplateType(), param.getKeyword());

        PageResult<AiPromptTemplate> pageResult = PageResult.of(page);
        PageResult<AiPromptTemplateVO> result = new PageResult<>();
        result.setList(pageResult.getList().stream().map(this::convertToVO).toList());
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取提示词模板详情
     *
     * @param id 模板ID
     * @return 模板详情VO
     */
    @Override
    public AiPromptTemplateVO getTemplateDetail(String id) {
        AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(id);
        if (template == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模板不存在");
        }
        return convertToVO(template);
    }

    /**
     * 创建提示词模板（全局上下文类型自动互斥）
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTemplate(AiPromptTemplateSaveParam param) {
        // 如果创建的是全局上下文类型且启用，先禁用其他全局上下文
        if ("global_context".equals(param.getTemplateType())
                && (param.getIsEnabled() == null || param.getIsEnabled() == 1)) {
            disableOtherGlobalContextTemplates(null);
        }

        AiPromptTemplate template = new AiPromptTemplate();
        template.setTemplateCode(param.getTemplateCode());
        template.setTemplateName(param.getTemplateName());
        template.setTemplateType(param.getTemplateType());
        template.setTemplateContent(param.getTemplateContent());
        template.setVariablesJson(param.getVariablesJson());
        template.setVersion(param.getVersion() != null ? param.getVersion() : 1);
        template.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        template.setDescription(param.getDescription());
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        aiPromptTemplateMapper.insert(template);
    }

    /**
     * 修改提示词模板信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyTemplateInfo(AiPromptTemplateSaveParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(param.getId());
        if (template == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模板不存在");
        }

        // 如果修改的是全局上下文类型且启用状态变为1，禁用其他全局上下文
        if ("global_context".equals(template.getTemplateType())
                && param.getIsEnabled() != null && param.getIsEnabled() == 1) {
            disableOtherGlobalContextTemplates(param.getId());
        }

        template.setTemplateName(param.getTemplateName());
        template.setTemplateContent(param.getTemplateContent());
        if (param.getVariablesJson() != null) {
            template.setVariablesJson(param.getVariablesJson());
        }
        if (param.getIsEnabled() != null) {
            template.setIsEnabled(param.getIsEnabled());
        }
        if (param.getDescription() != null) {
            template.setDescription(param.getDescription());
        }
        template.setUpdateTime(LocalDateTime.now());
        aiPromptTemplateMapper.update(template);
    }

    /**
     * 删除提示词模板
     *
     * @param param 删除参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTemplate(AiRemoveParam param) {
        AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(param.getId());
        if (template == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模板不存在");
        }
        aiPromptTemplateMapper.deleteById(param.getId());
    }

    /**
     * 修改提示词模板状态
     *
     * @param param 状态修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyTemplateStatus(AiStatusParam param) {
        AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(param.getId());
        if (template == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模板不存在");
        }

        // 如果启用的是全局上下文类型，禁用其他全局上下文
        if ("global_context".equals(template.getTemplateType()) && param.getStatus() == 1) {
            disableOtherGlobalContextTemplates(param.getId());
        }

        template.setIsEnabled(param.getStatus());
        template.setUpdateTime(LocalDateTime.now());
        aiPromptTemplateMapper.update(template);
    }

    /**
     * 根据模板编码获取最新版本模板
     *
     * @param templateCode 模板编码
     * @return 模板详情VO
     */
    @Override
    public AiPromptTemplateVO getLatestByCode(String templateCode) {
        AiPromptTemplate template = aiPromptTemplateMapper.selectLatestByCode(templateCode);
        if (template == null) {
            return null;
        }
        return convertToVO(template);
    }

    /**
     * 渲染提示词模板（替换变量占位符）
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的文本
     */
    @Override
    public String renderTemplate(String templateCode, Map<String, Object> variables) {
        AiPromptTemplate template = aiPromptTemplateMapper.selectLatestByCode(templateCode);
        if (template == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模板不存在: " + templateCode);
        }

        String content = template.getTemplateContent();
        if (content == null || content.isEmpty()) {
            return content;
        }

        // 替换 ${variable} 形式的占位符
        java.util.regex.Matcher matcher = AiPromptVariablePattern.INSTANCE.VARIABLE_PATTERN.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            matcher.appendReplacement(sb, value != null ? value.toString() : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 模板实体转VO
     *
     * @param template 模板实体
     * @return 模板VO
     */
    private AiPromptTemplateVO convertToVO(AiPromptTemplate template) {
        AiPromptTemplateVO vo = new AiPromptTemplateVO();
        vo.setId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateType(template.getTemplateType());
        vo.setTemplateContent(template.getTemplateContent());
        vo.setVariablesJson(template.getVariablesJson());
        vo.setVersion(template.getVersion());
        vo.setIsEnabled(template.getIsEnabled());
        vo.setDescription(template.getDescription());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }

    /**
     * 禁用其他全局上下文模板（确保只有一个启用的全局上下文）
     *
     * @param excludeId 需要排除的模板ID（修改时排除自身）
     */
    private void disableOtherGlobalContextTemplates(String excludeId) {
        List<AiPromptTemplate> globalContextTemplates = aiPromptTemplateMapper.selectEnabledByType("global_context");
        for (AiPromptTemplate t : globalContextTemplates) {
            if (excludeId == null || !excludeId.equals(t.getId())) {
                t.setIsEnabled(0);
                t.setUpdateTime(LocalDateTime.now());
                aiPromptTemplateMapper.update(t);
            }
        }
    }
}
