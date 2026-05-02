package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.SensitiveWord;
import com.taolife.aichat.mapper.SensitiveWordMapper;
import com.taolife.aichat.param.*;
import com.taolife.aichat.param.ImportAdminParam;
import com.taolife.aichat.service.ISensitiveWordAdminService;
import com.taolife.aichat.vo.SensitiveWordAdminVO;
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
import java.util.List;

/**
 * 管理员敏感词服务实现
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveWordAdminServiceImpl implements ISensitiveWordAdminService {

    private final SensitiveWordMapper sensitiveWordMapper;

    /**
     * 分页查询敏感词列表
     *
     * @param param 分页查询参数
     * @return 敏感词分页结果
     */
    @Override
    public PageResult<SensitiveWordAdminVO> getSensitiveWordPage(SensitiveWordPageAdminParam param) {
        Page<SensitiveWord> page = sensitiveWordMapper.selectPageByParam(param);

        PageResult<SensitiveWord> pageResult = PageResult.of(page);
        List<SensitiveWordAdminVO> list = pageResult.getList().stream()
                .map(this::convertToVO)
                .toList();

        PageResult<SensitiveWordAdminVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    @Override
    public SensitiveWordAdminVO getSensitiveWordDetail(SensitiveWordDetailAdminParam param) {
        SensitiveWord word = sensitiveWordMapper.selectOneById(param.getId());
        if (word == null || word.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "敏感词不存在");
        }
        return convertToVO(word);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSensitiveWord(SensitiveWordSaveAdminParam param) {
        SensitiveWord word = new SensitiveWord();
        word.setWord(param.getWord());
        word.setWordType(param.getWordType());
        word.setSeverity(param.getSeverity());
        word.setActionType(param.getActionType());
        word.setReplaceWord(param.getReplaceWord());
        word.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        word.setIsDeleted(0);
        word.setCreateTime(LocalDateTime.now());
        word.setUpdateTime(LocalDateTime.now());
        sensitiveWordMapper.insert(word);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySensitiveWordInfo(SensitiveWordSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        SensitiveWord word = sensitiveWordMapper.selectOneById(param.getId());
        if (word == null || word.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "敏感词不存在");
        }
        word.setWord(param.getWord());
        word.setWordType(param.getWordType());
        word.setSeverity(param.getSeverity());
        word.setActionType(param.getActionType());
        word.setReplaceWord(param.getReplaceWord());
        if (param.getIsEnabled() != null) {
            word.setIsEnabled(param.getIsEnabled());
        }
        word.setUpdateTime(LocalDateTime.now());
        sensitiveWordMapper.update(word);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSensitiveWord(RemoveSensitiveWordAdminParam param) {
        SensitiveWord word = sensitiveWordMapper.selectOneById(param.getId());
        if (word == null || word.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "敏感词不存在");
        }
        word.setIsDeleted(1);
        word.setUpdateTime(LocalDateTime.now());
        sensitiveWordMapper.update(word);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySensitiveWordStatus(ModifySensitiveWordStatusAdminParam param) {
        SensitiveWord word = sensitiveWordMapper.selectOneById(param.getId());
        if (word == null || word.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "敏感词不存在");
        }
        word.setIsEnabled(param.getIsEnabled());
        word.setUpdateTime(LocalDateTime.now());
        sensitiveWordMapper.update(word);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO importSensitiveWords(ImportAdminParam param) {
        MultipartFile file = param.getFile();
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "仅支持Excel文件（.xlsx/.xls）");
        }

        List<SensitiveWord> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String word = getCellStringValue(row.getCell(0));
                    if (word == null || word.trim().isEmpty()) {
                        failList.add("第" + (i + 1) + "行：敏感词不能为空");
                        continue;
                    }

                    SensitiveWord entity = new SensitiveWord();
                    entity.setWord(word.trim());
                    entity.setWordType(getCellIntValue(row.getCell(1), 1));
                    entity.setSeverity(getCellIntValue(row.getCell(2), 1));
                    entity.setActionType(getCellIntValue(row.getCell(3), 1));
                    entity.setReplaceWord(getCellStringValue(row.getCell(4)));
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
                sensitiveWordMapper.insertBatch(successList);
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

    private SensitiveWordAdminVO convertToVO(SensitiveWord word) {
        SensitiveWordAdminVO vo = new SensitiveWordAdminVO();
        vo.setId(word.getId());
        vo.setWord(word.getWord());
        vo.setWordType(word.getWordType());
        vo.setWordTypeName(getWordTypeName(word.getWordType()));
        vo.setSeverity(word.getSeverity());
        vo.setSeverityName(getSeverityName(word.getSeverity()));
        vo.setActionType(word.getActionType());
        vo.setActionTypeName(getActionTypeName(word.getActionType()));
        vo.setReplaceWord(word.getReplaceWord());
        vo.setIsEnabled(word.getIsEnabled());
        vo.setCreateTime(word.getCreateTime());
        return vo;
    }

    private String getWordTypeName(Integer wordType) {
        if (wordType == null) return "未知";
        return switch (wordType) {
            case 1 -> "医疗诊断";
            case 2 -> "政治敏感";
            case 3 -> "不当内容";
            default -> "未知";
        };
    }

    private String getSeverityName(Integer severity) {
        if (severity == null) return "未知";
        return switch (severity) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            default -> "未知";
        };
    }

    private String getActionTypeName(Integer actionType) {
        if (actionType == null) return "未知";
        return switch (actionType) {
            case 1 -> "拒绝回答";
            case 2 -> "替换";
            case 3 -> "警告";
            default -> "未知";
        };
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

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