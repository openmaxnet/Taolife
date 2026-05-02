package com.taolife.wisdom.service.impl;

import com.taolife.wisdom.entity.FitnessOption;
import com.taolife.wisdom.entity.FitnessQuestion;
import com.taolife.wisdom.entity.FitnessRecord;
import com.taolife.wisdom.entity.FitnessType;
import com.taolife.wisdom.enums.FitnessCodeEnum;
import com.taolife.wisdom.mapper.FitnessOptionMapper;
import com.taolife.wisdom.mapper.FitnessQuestionMapper;
import com.taolife.wisdom.mapper.FitnessRecordMapper;
import com.taolife.wisdom.mapper.FitnessTypeMapper;
import com.taolife.wisdom.param.AssessmentSubmitParam;
import com.taolife.wisdom.service.IFitnessService;
import com.taolife.wisdom.vo.AssessmentStatusVO;
import com.taolife.wisdom.vo.FitnessQuestionVO;
import com.taolife.wisdom.vo.FitnessRecordVO;
import com.taolife.wisdom.vo.FitnessResultVO;
import com.taolife.wisdom.vo.FitnessTypeVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * 体质辨识服务实现类
 * 实现体质测评相关的业务逻辑
 *
 * @author 文二
 * @date 2026-03-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FitnessServiceImpl implements IFitnessService {

    private final FitnessTypeMapper constitutionTypeMapper;
    private final FitnessQuestionMapper constitutionQuestionMapper;
    private final FitnessOptionMapper constitutionOptionMapper;
    private final FitnessRecordMapper constitutionRecordMapper;
    private final ObjectMapper objectMapper;

    /**
     * 获取体质类型列表
     * 查询所有可用的体质类型
     *
     * @return 体质类型列表
     */
    @Override
    public List<FitnessTypeVO> getConstitutionTypes() {
        List<FitnessType> types = constitutionTypeMapper.selectAllEnabled();
        return types.stream().map(this::convertToTypeVO).collect(Collectors.toList());
    }

    /**
     * 获取问卷题目列表
     * 根据指定模式获取对应的问卷题目（含选项）
     *
     * @param mode 测评模式：1-简易模式，2-精细模式
     * @return 题目列表
     */
    @Override
    public List<FitnessQuestionVO> getQuestionList(Integer mode) {
        // 查询题目列表
        List<FitnessQuestion> questions = constitutionQuestionMapper.selectByMode(mode);
        if (questions.isEmpty()) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "问卷题目不存在");
        }

        // 获取题目ID列表
        List<String> questionIds = questions.stream()
            .map(FitnessQuestion::getId)
            .collect(Collectors.toList());

        // 批量查询选项
        List<FitnessOption> options = constitutionOptionMapper.selectByQuestionIds(questionIds);

        // 按题目ID分组
        Map<String, List<FitnessOption>> optionsMap = options.stream()
            .collect(Collectors.groupingBy(FitnessOption::getQuestionId));

        // 组装VO
        return questions.stream().map(q -> {
            FitnessQuestionVO vo = new FitnessQuestionVO();
            vo.setId(q.getId());
            vo.setQuestionNo(q.getQuestionNo());
            vo.setQuestionText(q.getQuestionText());
            vo.setQuestionTextSecondary(q.getQuestionTextSecondary());
            vo.setCategory(q.getCategory());
            vo.setAnswerType(q.getAnswerType());
            vo.setIsRequired(q.getIsRequired());

            List<FitnessOption> opts = optionsMap.getOrDefault(q.getId(), Collections.emptyList());
            vo.setOptions(opts.stream().map(this::convertToOptionVO).collect(Collectors.toList()));

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 提交测评答案
     * 用户完成问卷后提交答案，系统计算体质结果并保存记录
     *
     * @param accountId 账号ID
     * @param mode      测评模式
     * @param param     提交参数
     * @return 测评结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FitnessResultVO submitAssessment(String accountId, Integer mode, AssessmentSubmitParam param) {
        // 1. 计算体质得分
        Map<String, BigDecimal> scores = calculateScores(param.getAnswers());

        // 2. 判定体质类型
        String constitutionCode = determineConstitution(scores);
        FitnessType fitnessType = constitutionTypeMapper.selectByCode(constitutionCode);

        if (fitnessType == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }

        BigDecimal mainScore = scores.get(constitutionCode);
        BigDecimal confidence = mainScore.multiply(BigDecimal.valueOf(20))
            .setScale(2, RoundingMode.HALF_UP);

        // 3. 保存测评记录
        FitnessRecord record = new FitnessRecord();
        record.setAccountId(accountId);
        record.setConstitutionCode(constitutionCode);
        record.setConstitutionName(fitnessType.getName());
        record.setScore(mainScore);
        record.setConfidence(confidence);
        record.setAssessmentMode(mode);
        record.setIsLatest(1);
        record.setCreateTime(LocalDateTime.now());

        try {
            record.setQuestionAnswers(objectMapper.writeValueAsString(param.getAnswers()));
            record.setAnalysisResult(objectMapper.writeValueAsString(scores));
        } catch (Exception e) {
            log.error("JSON序列化失败", e);
        }

        // 将之前的记录设为非最新
        constitutionRecordMapper.updateAllToNotLatest(accountId);

        // 保存新记录
        constitutionRecordMapper.insert(record);

        // 4. 构建返回结果
        return buildResultVO(record, fitnessType, scores);
    }

    /**
     * 获取测评结果
     * 根据记录ID查询测评结果详情
     *
     * @param recordId 记录ID
     * @return 测评结果
     */
    @Override
    public FitnessResultVO getAssessmentResult(String recordId) {
        FitnessRecord record = constitutionRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "测评记录不存在");
        }

        FitnessType fitnessType = constitutionTypeMapper.selectByCode(record.getConstitutionCode());
        if (fitnessType == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }

        Map<String, BigDecimal> scores = parseScores(record.getAnalysisResult());
        return buildResultVO(record, fitnessType, scores);
    }

    /**
     * 获取最新测评结果
     * 查询用户最近一次测评的结果
     *
     * @param accountId 账号ID
     * @return 测评结果
     */
    @Override
    public FitnessResultVO getLatestResult(String accountId) {
        // 查询最新测评记录
        FitnessRecord record = constitutionRecordMapper.selectLatestByAccountId(accountId);
        // 记录不存在则返回null，由前端处理空状态
        if (record == null) {
            return null;
        }

        // 查询体质类型信息
        FitnessType fitnessType = constitutionTypeMapper.selectByCode(record.getConstitutionCode());
        if (fitnessType == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }

        // 解析得分并构建返回结果
        Map<String, BigDecimal> scores = parseScores(record.getAnalysisResult());
        return buildResultVO(record, fitnessType, scores);
    }

    /**
     * 获取测评历史记录
     * 查询用户所有测评历史记录
     *
     * @param accountId 账号ID
     * @return 历史记录列表
     */
    @Override
    public List<FitnessRecordVO> getHistoryList(String accountId) {
        List<FitnessRecord> records = constitutionRecordMapper.selectByAccountId(accountId);
        return records.stream().map(this::convertToRecordVO).collect(Collectors.toList());
    }

    /**
     * 检查用户是否有测评记录
     * 查询用户是否有做过的体质测评，有记录返回1，无记录返回0
     *
     * @param accountId 账号ID
     * @return 评估状态VO
     */
    @Override
    public AssessmentStatusVO checkAssessmentStatus(String accountId) {
        // 查询最新测评记录
        FitnessRecord record = constitutionRecordMapper.selectLatestByAccountId(accountId);

        // 构建返回结果
        AssessmentStatusVO statusVO = new AssessmentStatusVO();
        if (record == null) {
            statusVO.setAssessmentStatus(0);
        } else {
            statusVO.setAssessmentStatus(1);
        }
        return statusVO;
    }

    /**
     * 计算体质得分
     * 根据用户答案计算每种体质的得分，并基于年龄和性别进行倾向性加权调整
     *
     * @param answers 用户答案列表
     * @return 体质得分Map
     */
    private Map<String, BigDecimal> calculateScores(List<AssessmentSubmitParam.AnswerItem> answers) {
        // 获取所有体质类型
        List<FitnessType> types = constitutionTypeMapper.selectAllEnabled();
        Map<String, List<Integer>> typeScores = new HashMap<>();

        // 初始化
        for (FitnessType type : types) {
            typeScores.put(type.getCode(), new ArrayList<>());
        }

        // 记录初始化的体质类型编码
        log.info("初始化体质类型编码: {}", typeScores.keySet());

        // 提取基础信息（年龄和性别）
        Integer ageRange = null; // 1:18-30, 2:31-45, 3:46-60, 4:60+
        Integer gender = null; // 1:男性, 2:女性

        // 统计每种体质得分
        for (AssessmentSubmitParam.AnswerItem answer : answers) {
            FitnessQuestion question = constitutionQuestionMapper.selectById(answer.getQuestionId());
            if (question == null) {
                log.warn("题目不存在，questionId: {}", answer.getQuestionId());
                continue;
            }

            String category = question.getCategory();
            Integer score = answer.getOptionValue();

            // 提取基础信息
            if ("基础信息".equals(category)) {
                if (question.getQuestionNo() == 1) {
                    ageRange = score; // 年龄范围
                } else if (question.getQuestionNo() == 2) {
                    gender = score; // 性别
                }
                log.info("基础信息 - 年龄范围: {}, 性别: {}", ageRange, gender);
                continue; // 基础信息不参与体质评分
            }

            // 如果是反向计分
            if (question.getReverseScore() != null && question.getReverseScore() == 1) {
                score = 6 - score; // 1->5, 2->4, 3->3, 4->2, 5->1
            }

            // 将中文分类名称映射为英文编码
            String constitutionCode = FitnessCodeEnum.getCodeByCategoryName(category);
            if (constitutionCode == null) {
                log.warn("category '{}' 未找到对应的体质编码，跳过该题目", category);
                continue;
            }

            log.info("题目ID: {}, category: {}, 映射编码: {}, 原始分值: {}, 最终分值: {}",
                answer.getQuestionId(), category, constitutionCode, answer.getOptionValue(), score);

            if (typeScores.containsKey(constitutionCode)) {
                typeScores.get(constitutionCode).add(score);
            } else {
                log.warn("体质编码 '{}' 不在初始化的体质类型列表中", constitutionCode);
            }
        }

        // 计算平均分
        Map<String, BigDecimal> result = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : typeScores.entrySet()) {
            List<Integer> scores = entry.getValue();
            if (scores.isEmpty()) {
                result.put(entry.getKey(), BigDecimal.ZERO);
                log.info("体质类型 {} 没有得分，设为0", entry.getKey());
            } else {
                double avg = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
                result.put(entry.getKey(), BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
                log.info("体质类型 {} 得分列表: {}, 平均分: {}", entry.getKey(), scores, avg);
            }
        }

        // 基于年龄和性别进行倾向性加权调整
        if (ageRange != null && gender != null) {
            result = applyTendencyAdjustment(result, ageRange, gender);
        }

        log.info("最终得分结果: {}", result);
        return result;
    }

    /**
     * 基于年龄和性别进行体质倾向性加权调整
     * 根据中医理论和统计学数据，对特定体质得分进行微调
     *
     * @param scores 原始体质得分
     * @param ageRange 年龄范围：1-18-30岁，2-31-45岁，3-46-60岁，4-60岁以上
     * @param gender 性别：1-男性，2-女性
     * @return 调整后的体质得分
     */
    private Map<String, BigDecimal> applyTendencyAdjustment(Map<String, BigDecimal> scores, Integer ageRange, Integer gender) {
        log.info("开始应用体质倾向性调整 - 年龄范围: {}, 性别: {}", ageRange, gender);

        Map<String, BigDecimal> adjusted = new HashMap<>(scores);
        Map<String, BigDecimal> tendencyWeights = new HashMap<>();

        // 年龄倾向性权重（根据中医理论和统计学数据）
        // 18-30岁：气郁质、湿热质倾向较高（工作压力大、饮食不规律）
        // 31-45岁：痰湿质、血瘀质倾向较高（代谢变慢、压力累积）
        // 46-60岁：气虚质、阳虚质倾向较高（气血衰退）
        // 60岁以上：阴虚质、阳虚质倾向较高（阴阳两虚）
        switch (ageRange) {
            case 1: // 18-30岁
                tendencyWeights.put("qiyu", BigDecimal.valueOf(0.10));   // 气郁质 +10%
                tendencyWeights.put("shire", BigDecimal.valueOf(0.08));   // 湿热质 +8%
                break;
            case 2: // 31-45岁
                tendencyWeights.put("tanshi", BigDecimal.valueOf(0.10));  // 痰湿质 +10%
                tendencyWeights.put("yuxu", BigDecimal.valueOf(0.08));    // 血瘀质 +8%
                break;
            case 3: // 46-60岁
                tendencyWeights.put("qixu", BigDecimal.valueOf(0.10));   // 气虚质 +10%
                tendencyWeights.put("yangxu", BigDecimal.valueOf(0.08));  // 阳虚质 +8%
                break;
            case 4: // 60岁以上
                tendencyWeights.put("yinxu", BigDecimal.valueOf(0.10));  // 阴虚质 +10%
                tendencyWeights.put("yangxu", BigDecimal.valueOf(0.12));  // 阳虚质 +12%
                break;
        }

        // 性别倾向性权重
        // 男性：湿热质、痰湿质倾向较高
        // 女性：血瘀质、气郁质倾向较高
        if (gender == 1) { // 男性
            tendencyWeights.put("shire", tendencyWeights.getOrDefault("shire", BigDecimal.ZERO).add(BigDecimal.valueOf(0.05)));  // 湿热质 +5%
            tendencyWeights.put("tanshi", tendencyWeights.getOrDefault("tanshi", BigDecimal.ZERO).add(BigDecimal.valueOf(0.05))); // 痰湿质 +5%
        } else if (gender == 2) { // 女性
            tendencyWeights.put("yuxu", tendencyWeights.getOrDefault("yuxu", BigDecimal.ZERO).add(BigDecimal.valueOf(0.05)));    // 血瘀质 +5%
            tendencyWeights.put("qiyu", tendencyWeights.getOrDefault("qiyu", BigDecimal.ZERO).add(BigDecimal.valueOf(0.05)));    // 气郁质 +5%
        }

        // 应用权重调整
        for (Map.Entry<String, BigDecimal> entry : tendencyWeights.entrySet()) {
            String constitutionCode = entry.getKey();
            BigDecimal weight = entry.getValue();

            if (adjusted.containsKey(constitutionCode)) {
                BigDecimal originalScore = adjusted.get(constitutionCode);
                // 加权公式：新得分 = 原得分 * (1 + 权重)
                BigDecimal adjustedScore = originalScore.multiply(BigDecimal.ONE.add(weight))
                    .setScale(2, RoundingMode.HALF_UP);

                // 确保得分不超过5分
                adjustedScore = adjustedScore.min(BigDecimal.valueOf(5.0));

                adjusted.put(constitutionCode, adjustedScore);
                log.info("体质 {} 倾向性调整: 原得分 {} -> 调整后 {} (权重: {}%)",
                    constitutionCode, originalScore, adjustedScore, weight.multiply(BigDecimal.valueOf(100)));
            }
        }

        log.info("体质倾向性调整完成");
        return adjusted;
    }

    /**
     * 判定体质类型
     * 根据得分计算结果判定用户的体质类型
     *
     * @param scores 体质得分Map
     * @return 体质编码
     */
    private String determineConstitution(Map<String, BigDecimal> scores) {
        // 平和质特殊判定：平和质得分>=3分 且 其他体质最高分<2.5分
        BigDecimal pingheScore = scores.getOrDefault("pinghe", BigDecimal.ZERO);
        BigDecimal maxOtherScore = scores.entrySet().stream()
            .filter(e -> !"pinghe".equals(e.getKey()))
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getValue)
            .orElse(BigDecimal.ZERO);

        if (pingheScore.compareTo(BigDecimal.valueOf(3.0)) >= 0
            && maxOtherScore.compareTo(BigDecimal.valueOf(2.5)) < 0) {
            return "pinghe";
        }

        // 找出最高分体质
        return scores.entrySet().stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse("qixu");
    }

    /**
     * 构建结果VO
     * 将记录和体质类型组装成返回结果
     *
     * @param record 测评记录
     * @param type  体质类型
     * @param scores 体质得分
     * @return 结果VO
     */
    private FitnessResultVO buildResultVO(FitnessRecord record, FitnessType type, Map<String, BigDecimal> scores) {
        FitnessResultVO vo = new FitnessResultVO();
        vo.setRecordId(record.getId());
        vo.setConstitutionCode(record.getConstitutionCode());
        vo.setConstitutionName(record.getConstitutionName());
        vo.setScore(record.getScore());
        vo.setConfidence(record.getConfidence());
        vo.setAssessmentMode(record.getAssessmentMode());
        vo.setDescription(type.getDescription());
        vo.setCharacteristics(type.getCharacteristics());
        vo.setHealthAdvice(type.getHealthAdvice());
        vo.setDietGuidance(type.getDietGuidance());
        vo.setExerciseGuidance(type.getExerciseGuidance());
        vo.setEmotionGuidance(type.getEmotionGuidance());
        vo.setAcupointGuidance(type.getAcupointGuidance());
        vo.setAllScores(scores);
        vo.setCreateTime(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return vo;
    }

    /**
     * 转换体质类型为VO
     *
     * @param type 体质类型实体
     * @return 体质类型VO
     */
    private FitnessTypeVO convertToTypeVO(FitnessType type) {
        FitnessTypeVO vo = new FitnessTypeVO();
        vo.setCode(type.getCode());
        vo.setName(type.getName());
        vo.setNameEn(type.getNameEn());
        vo.setDescription(type.getDescription());
        vo.setCharacteristics(type.getCharacteristics());
        return vo;
    }

    /**
     * 转换选项为VO
     *
     * @param option 选项实体
     * @return 选项VO
     */
    private FitnessQuestionVO.FitnessOptionVO convertToOptionVO(FitnessOption option) {
        FitnessQuestionVO.FitnessOptionVO vo = new FitnessQuestionVO.FitnessOptionVO();
        vo.setId(option.getId());
        vo.setOptionNo(option.getOptionNo());
        vo.setOptionText(option.getOptionText());
        vo.setOptionValue(option.getOptionValue());
        vo.setTargetTypeCode(option.getTargetTypeCode());
        return vo;
    }

    /**
     * 转换记录为VO
     *
     * @param record 测评记录实体
     * @return 记录VO
     */
    private FitnessRecordVO convertToRecordVO(FitnessRecord record) {
        FitnessRecordVO vo = new FitnessRecordVO();
        vo.setRecordId(record.getId());
        vo.setConstitutionCode(record.getConstitutionCode());
        vo.setConstitutionName(record.getConstitutionName());
        vo.setScore(record.getScore());
        vo.setConfidence(record.getConfidence());
        vo.setAssessmentMode(record.getAssessmentMode());
        vo.setMixedTypes(record.getMixedTypes());
        vo.setCreateTime(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return vo;
    }

    /**
     * 解析得分JSON
     *
     * @param json 得分JSON字符串
     * @return 得分Map
     */
    private Map<String, BigDecimal> parseScores(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, BigDecimal>>() {});
        } catch (Exception e) {
            log.error("解析得分失败", e);
            return new HashMap<>();
        }
    }
}
