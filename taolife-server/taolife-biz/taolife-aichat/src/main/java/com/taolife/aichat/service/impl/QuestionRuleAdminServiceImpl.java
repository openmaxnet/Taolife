package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.ChatRule;
import com.taolife.aichat.mapper.ChatRuleMapper;
import com.taolife.aichat.param.*;
import com.taolife.aichat.param.ImportAdminParam;
import com.taolife.aichat.service.IQuestionRuleAdminService;
import com.taolife.aichat.vo.QuestionRuleAdminVO;
import com.taolife.aichat.vo.ImportResultVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 管理员分类规则服务实现
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionRuleAdminServiceImpl implements IQuestionRuleAdminService {

    private final ChatRuleMapper chatRuleMapper;

    /**
     * 分页查询分类规则
     * 根据查询参数分页获取分类规则列表，并转换为视图对象
     *
     * @param param 分页查询参数
     * @return 分页的分类规则视图对象结果
     */
    @Override
    public PageResult<QuestionRuleAdminVO> getQuestionRulePage(QuestionRulePageAdminParam param) {
        Page<ChatRule> page = chatRuleMapper.selectPageByParam(param);

        PageResult<ChatRule> pageResult = PageResult.of(page);
        List<QuestionRuleAdminVO> list = pageResult.getList().stream()
                .map(this::convertToVO)
                .toList();

        PageResult<QuestionRuleAdminVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取分类规则详情
     * 根据ID查询单条分类规则，若不存在或已删除则抛出异常
     *
     * @param param 查询参数（含规则ID）
     * @return 分类规则视图对象
     */
    @Override
    public QuestionRuleAdminVO getQuestionRuleDetail(QuestionRuleDetailAdminParam param) {
        ChatRule rule = chatRuleMapper.selectOneById(param.getId());
        if (rule == null || rule.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "分类规则不存在");
        }
        return convertToVO(rule);
    }

    /**
     * 创建分类规则
     * 根据参数创建新的分类规则实体并持久化到数据库
     *
     * @param param 规则保存参数（分类编码、名称、关键词、优先级等）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createQuestionRule(QuestionRuleSaveAdminParam param) {
        ChatRule rule = new ChatRule();
        rule.setCategoryCode(param.getCategoryCode());
        rule.setCategoryName(param.getCategoryName());
        rule.setKeywords(param.getKeywords());
        rule.setPriority(param.getPriority() != null ? param.getPriority() : 0);
        rule.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        rule.setIsDeleted(0);
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        chatRuleMapper.insert(rule);
    }

    /**
     * 修改分类规则信息
     * 验证规则存在后，更新分类编码、名称、关键词、优先级和启用状态
     *
     * @param param 规则保存参数（含ID及待更新字段）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyQuestionRuleInfo(QuestionRuleSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        ChatRule rule = chatRuleMapper.selectOneById(param.getId());
        if (rule == null || rule.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "分类规则不存在");
        }
        rule.setCategoryCode(param.getCategoryCode());
        rule.setCategoryName(param.getCategoryName());
        rule.setKeywords(param.getKeywords());
        if (param.getPriority() != null) {
            rule.setPriority(param.getPriority());
        }
        if (param.getIsEnabled() != null) {
            rule.setIsEnabled(param.getIsEnabled());
        }
        rule.setUpdateTime(LocalDateTime.now());
        chatRuleMapper.update(rule);
    }

    /**
     * 删除分类规则
     * 验证规则存在后，逻辑删除（标记isDeleted为1）
     *
     * @param param 删除参数（含规则ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeQuestionRule(RemoveQuestionRuleAdminParam param) {
        ChatRule rule = chatRuleMapper.selectOneById(param.getId());
        if (rule == null || rule.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "分类规则不存在");
        }
        rule.setIsDeleted(1);
        rule.setUpdateTime(LocalDateTime.now());
        chatRuleMapper.update(rule);
    }

    /**
     * 修改分类规则启用状态
     * 验证规则存在后，更新启用/禁用状态
     *
     * @param param 状态修改参数（含规则ID及目标状态）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyQuestionRuleStatus(ModifyQuestionRuleStatusAdminParam param) {
        ChatRule rule = chatRuleMapper.selectOneById(param.getId());
        if (rule == null || rule.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "分类规则不存在");
        }
        rule.setIsEnabled(param.getIsEnabled());
        rule.setUpdateTime(LocalDateTime.now());
        chatRuleMapper.update(rule);
    }

    /**
     * 导入分类规则（Excel）
     * 解析Excel文件，逐行读取分类编码、名称、关键词和优先级，批量导入到数据库
     *
     * @param param 导入参数（含Excel文件）
     * @return 导入结果（成功/失败数量及失败原因列表）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO importQuestionRules(ImportAdminParam param) {
        MultipartFile file = param.getFile();
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "仅支持Excel文件（.xlsx/.xls）");
        }

        List<ChatRule> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String categoryCode = getCellStringValue(row.getCell(0));
                    String categoryName = getCellStringValue(row.getCell(1));
                    String keywords = getCellStringValue(row.getCell(2));

                    if (categoryCode == null || categoryCode.trim().isEmpty()) {
                        failList.add("第" + (i + 1) + "行：分类编码不能为空");
                        continue;
                    }
                    if (categoryName == null || categoryName.trim().isEmpty()) {
                        failList.add("第" + (i + 1) + "行：分类名称不能为空");
                        continue;
                    }

                    ChatRule entity = new ChatRule();
                    entity.setCategoryCode(categoryCode.trim());
                    entity.setCategoryName(categoryName.trim());
                    entity.setKeywords(keywords != null ? keywords.trim() : "");
                    entity.setPriority(getCellIntValue(row.getCell(3), 0));
                    entity.setIsEnabled(1);
                    entity.setIsDeleted(0);
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setUpdateTime(LocalDateTime.now());

                    successList.add(entity);
                } catch (Exception e) {
                    failList.add("第" + (i + 1) + "行解析失败：" + e.getMessage());
                }
            }

            if (!successList.isEmpty()) {
                chatRuleMapper.insertBatch(successList);
            }
        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            throw new BusinessException(ExceptionCode.SYSTEM_ERROR, "解析Excel文件失败：" + e.getMessage());
        }

        ImportResultVO result = new ImportResultVO();
        result.setTotalCount(successList.size() + failList.size());
        result.setSuccessCount(successList.size());
        result.setFailCount(failList.size());
        result.setFailList(failList);
        return result;
    }

    /**
     * 转换为分类规则视图对象
     * 将实体对象转换为视图对象，同时将关键词JSON字符串解析为列表
     *
     * @param rule 分类规则实体
     * @return 分类规则视图对象
     */
    private QuestionRuleAdminVO convertToVO(ChatRule rule) {
        QuestionRuleAdminVO vo = new QuestionRuleAdminVO();
        vo.setId(rule.getId());
        vo.setCategoryCode(rule.getCategoryCode());
        vo.setCategoryName(rule.getCategoryName());
        vo.setKeywordsStr(rule.getKeywords());
        if (rule.getKeywords() != null && !rule.getKeywords().isEmpty()) {
            vo.setKeywords(Arrays.asList(rule.getKeywords().split(",")));
        }
        vo.setPriority(rule.getPriority());
        vo.setIsEnabled(rule.getIsEnabled());
        vo.setCreateTime(rule.getCreateTime());
        return vo;
    }

    /**
     * 获取Excel单元格字符串值
     * 统一处理STRING、NUMERIC和BOOLEAN类型的单元格值
     *
     * @param cell Excel单元格
     * @return 单元格的字符串值，单元格为空时返回null
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    /**
     * 获取Excel单元格整数值
     * 统一处理NUMERIC和STRING类型的单元格值，解析失败时返回默认值
     *
     * @param cell Excel单元格
     * @param defaultValue 解析失败时的默认值
     * @return 单元格的整数值，单元格为空时返回默认值
     */
    private Integer getCellIntValue(Cell cell, Integer defaultValue) {
        if (cell == null) return defaultValue;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (int) cell.getNumericCellValue();
                case STRING -> Integer.parseInt(cell.getStringCellValue().trim());
                default -> defaultValue;
            };
        } catch (Exception e) {
            return defaultValue;
        }
    }
}