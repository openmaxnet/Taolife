package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.wisdom.entity.FitnessOption;
import com.taolife.wisdom.entity.FitnessQuestion;
import com.taolife.wisdom.entity.FitnessType;
import com.taolife.wisdom.mapper.FitnessOptionMapper;
import com.taolife.wisdom.mapper.FitnessQuestionMapper;
import com.taolife.wisdom.mapper.FitnessTypeMapper;
import com.taolife.wisdom.param.*;
import com.taolife.wisdom.service.IFitnessQuestionAdminService;
import com.taolife.wisdom.vo.FitnessQuestionAdminVO;
import com.taolife.wisdom.vo.OptionAdminVO;
import com.taolife.wisdom.vo.QuestionDetailAdminVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员体质问卷服务实现
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FitnessQuestionAdminServiceImpl implements IFitnessQuestionAdminService {

    private final FitnessQuestionMapper questionMapper;
    private final FitnessOptionMapper optionMapper;
    private final FitnessTypeMapper constitutionTypeMapper;

    /**
     * 分页查询题目列表
     *
     * @param param 分页查询参数
     * @return 题目分页结果
     */
    @Override
    public PageResult<FitnessQuestionAdminVO> getQuestionPage(QuestionPageAdminParam param) {
        Page<FitnessQuestion> page = questionMapper.selectPageByParam(param);

        PageResult<FitnessQuestion> pageResult = PageResult.of(page);
        List<FitnessQuestionAdminVO> list = pageResult.getList().stream()
                .map(this::convertToQuestionVO)
                .toList();

        PageResult<FitnessQuestionAdminVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取题目详情（含选项）
     *
     * @param param 详情查询参数
     * @return 题目详情VO
     */
    @Override
    public QuestionDetailAdminVO getQuestionDetail(QuestionDetailAdminParam param) {
        FitnessQuestion question = questionMapper.selectById(param.getId());
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "题目不存在");
        }

        QuestionDetailAdminVO detailVO = new QuestionDetailAdminVO();
        detailVO.setQuestion(convertToQuestionVO(question));

        List<FitnessOption> options = optionMapper.selectByQuestionId(param.getId());
        List<OptionAdminVO> optionVOs = options.stream()
                .map(this::convertToOptionVO)
                .toList();
        detailVO.setOptions(optionVOs);

        return detailVO;
    }

    /**
     * 创建题目
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createQuestion(FitnessQuestionSaveAdminParam param) {
        FitnessQuestion question = new FitnessQuestion();
        question.setQuestionNo(param.getQuestionNo());
        question.setQuestionText(param.getQuestionText());
        question.setQuestionTextSecondary(param.getQuestionTextSecondary());
        question.setCategory(param.getCategory());
        question.setDimension(param.getDimension());
        question.setAnswerType(param.getAnswerType());
        question.setIsRequired(param.getIsRequired());
        question.setQuestionMode(param.getQuestionMode());
        question.setWeight(param.getWeight());
        question.setReverseScore(param.getReverseScore());
        question.setIsConsistencyCheck(param.getIsConsistencyCheck());
        question.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        question.setIsDisabled(param.getIsDisabled() != null ? param.getIsDisabled() : 0);
        question.setIsDeleted(0);
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.insert(question);
    }

    /**
     * 修改题目信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyQuestionInfo(FitnessQuestionSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        FitnessQuestion question = questionMapper.selectById(param.getId());
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "题目不存在");
        }
        question.setQuestionNo(param.getQuestionNo());
        question.setQuestionText(param.getQuestionText());
        question.setQuestionTextSecondary(param.getQuestionTextSecondary());
        question.setCategory(param.getCategory());
        question.setDimension(param.getDimension());
        question.setAnswerType(param.getAnswerType());
        question.setIsRequired(param.getIsRequired());
        question.setQuestionMode(param.getQuestionMode());
        question.setWeight(param.getWeight());
        question.setReverseScore(param.getReverseScore());
        question.setIsConsistencyCheck(param.getIsConsistencyCheck());
        if (param.getSortOrder() != null) {
            question.setSortOrder(param.getSortOrder());
        }
        if (param.getIsDisabled() != null) {
            question.setIsDisabled(param.getIsDisabled());
        }
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.update(question);
    }

    /**
     * 删除题目（逻辑删除）
     *
     * @param param 删除参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeQuestion(RemoveQuestionAdminParam param) {
        FitnessQuestion question = questionMapper.selectById(param.getId());
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "题目不存在");
        }
        question.setIsDeleted(1);
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.update(question);
    }

    /**
     * 修改题目状态（启用/禁用）
     *
     * @param param 状态修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyQuestionStatus(ModifyQuestionStatusAdminParam param) {
        FitnessQuestion question = questionMapper.selectById(param.getId());
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "题目不存在");
        }
        question.setIsDisabled(param.getIsDisabled());
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.update(question);
    }

    /**
     * 创建选项
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOption(OptionSaveAdminParam param) {
        FitnessOption option = new FitnessOption();
        option.setQuestionId(param.getQuestionId());
        option.setOptionNo(param.getOptionNo());
        option.setOptionText(param.getOptionText());
        option.setOptionValue(param.getOptionValue());
        option.setTargetTypeCode(param.getTargetTypeCode());
        option.setReverseValue(param.getReverseValue());
        option.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        option.setCreateTime(LocalDateTime.now());
        optionMapper.insert(option);
    }

    /**
     * 修改选项信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyOptionInfo(OptionSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        FitnessOption option = optionMapper.selectOneById(param.getId());
        if (option == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "选项不存在");
        }
        option.setOptionNo(param.getOptionNo());
        option.setOptionText(param.getOptionText());
        option.setOptionValue(param.getOptionValue());
        option.setTargetTypeCode(param.getTargetTypeCode());
        option.setReverseValue(param.getReverseValue());
        if (param.getSortOrder() != null) {
            option.setSortOrder(param.getSortOrder());
        }
        optionMapper.update(option);
    }

    /**
     * 删除选项
     *
     * @param param 删除参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOption(RemoveOptionAdminParam param) {
        FitnessOption option = optionMapper.selectOneById(param.getId());
        if (option == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "选项不存在");
        }
        optionMapper.deleteById(param.getId());
    }

    /**
     * 题目实体转列表VO
     *
     * @param question 题目实体
     * @return 题目VO
     */
    private FitnessQuestionAdminVO convertToQuestionVO(FitnessQuestion question) {
        FitnessQuestionAdminVO vo = new FitnessQuestionAdminVO();
        vo.setId(question.getId());
        vo.setQuestionNo(question.getQuestionNo());
        vo.setQuestionText(question.getQuestionText());
        vo.setQuestionTextSecondary(question.getQuestionTextSecondary());
        vo.setCategory(question.getCategory());
        vo.setDimension(question.getDimension());
        vo.setAnswerType(question.getAnswerType());
        vo.setIsRequired(question.getIsRequired());
        vo.setQuestionMode(question.getQuestionMode());
        vo.setWeight(question.getWeight());
        vo.setReverseScore(question.getReverseScore());
        vo.setIsConsistencyCheck(question.getIsConsistencyCheck());
        vo.setSortOrder(question.getSortOrder());
        vo.setIsDisabled(question.getIsDisabled());
        vo.setCreateTime(question.getCreateTime());

        List<FitnessOption> options = optionMapper.selectByQuestionId(question.getId());
        vo.setOptionCount(options.size());

        return vo;
    }

    /**
     * 选项实体转VO
     *
     * @param option 选项实体
     * @return 选项VO
     */
    private OptionAdminVO convertToOptionVO(FitnessOption option) {
        OptionAdminVO vo = new OptionAdminVO();
        vo.setId(option.getId());
        vo.setQuestionId(option.getQuestionId());
        vo.setOptionNo(option.getOptionNo());
        vo.setOptionText(option.getOptionText());
        vo.setOptionValue(option.getOptionValue());
        vo.setTargetTypeCode(option.getTargetTypeCode());
        vo.setReverseValue(option.getReverseValue());
        vo.setSortOrder(option.getSortOrder());
        vo.setCreateTime(option.getCreateTime());

        if (option.getTargetTypeCode() != null) {
            FitnessType type = constitutionTypeMapper.selectByCode(option.getTargetTypeCode());
            if (type != null) {
                vo.setTargetTypeName(type.getName());
            }
        }

        return vo;
    }
}
