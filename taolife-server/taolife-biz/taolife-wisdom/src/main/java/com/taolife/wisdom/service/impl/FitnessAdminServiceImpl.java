package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.wisdom.entity.FitnessType;
import com.taolife.wisdom.mapper.FitnessTypeMapper;
import com.taolife.wisdom.param.*;
import com.taolife.wisdom.service.IFitnessAdminService;
import com.taolife.wisdom.vo.FitnessTypeAdminVO;
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
 * 管理员体质类型服务实现
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FitnessAdminServiceImpl implements IFitnessAdminService {

    private final FitnessTypeMapper constitutionTypeMapper;

    /**
     * 分页查询体质类型列表
     *
     * @param param 分页查询参数
     * @return 体质类型分页结果
     */
    @Override
    public PageResult<FitnessTypeAdminVO> getConstitutionTypePage(FitnessTypePageAdminParam param) {
        Page<FitnessType> page = constitutionTypeMapper.selectPageByParam(param);

        PageResult<FitnessType> pageResult = PageResult.of(page);
        List<FitnessTypeAdminVO> list = pageResult.getList().stream()
                .map(this::convertToVO)
                .toList();

        PageResult<FitnessTypeAdminVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取体质类型详情
     *
     * @param param 详情查询参数
     * @return 体质类型详情VO
     */
    @Override
    public FitnessTypeAdminVO getConstitutionTypeDetail(FitnessTypeDetailAdminParam param) {
        FitnessType type = constitutionTypeMapper.selectOneById(param.getId());
        if (type == null || type.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }
        return convertToVO(type);
    }

    /**
     * 创建体质类型
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConstitutionType(FitnessTypeSaveAdminParam param) {
        FitnessType type = new FitnessType();
        type.setCode(param.getCode());
        type.setName(param.getName());
        type.setNameEn(param.getNameEn());
        type.setDescription(param.getDescription());
        type.setCharacteristics(param.getCharacteristics());
        type.setFormationReason(param.getFormationReason());
        type.setHealthAdvice(param.getHealthAdvice());
        type.setDietGuidance(param.getDietGuidance());
        type.setExerciseGuidance(param.getExerciseGuidance());
        type.setEmotionGuidance(param.getEmotionGuidance());
        type.setAcupointGuidance(param.getAcupointGuidance());
        type.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        type.setIsDisabled(param.getIsDisabled() != null ? param.getIsDisabled() : 0);
        type.setIsDeleted(0);
        type.setCreateTime(LocalDateTime.now());
        type.setUpdateTime(LocalDateTime.now());
        constitutionTypeMapper.insert(type);
    }

    /**
     * 修改体质类型信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyConstitutionTypeInfo(FitnessTypeSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        FitnessType type = constitutionTypeMapper.selectOneById(param.getId());
        if (type == null || type.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }
        type.setCode(param.getCode());
        type.setName(param.getName());
        type.setNameEn(param.getNameEn());
        type.setDescription(param.getDescription());
        type.setCharacteristics(param.getCharacteristics());
        type.setFormationReason(param.getFormationReason());
        type.setHealthAdvice(param.getHealthAdvice());
        type.setDietGuidance(param.getDietGuidance());
        type.setExerciseGuidance(param.getExerciseGuidance());
        type.setEmotionGuidance(param.getEmotionGuidance());
        type.setAcupointGuidance(param.getAcupointGuidance());
        if (param.getSortOrder() != null) {
            type.setSortOrder(param.getSortOrder());
        }
        if (param.getIsDisabled() != null) {
            type.setIsDisabled(param.getIsDisabled());
        }
        type.setUpdateTime(LocalDateTime.now());
        constitutionTypeMapper.update(type);
    }

    /**
     * 删除体质类型（逻辑删除）
     *
     * @param param 删除参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeConstitutionType(RemoveFitnessTypeAdminParam param) {
        FitnessType type = constitutionTypeMapper.selectOneById(param.getId());
        if (type == null || type.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }
        type.setIsDeleted(1);
        type.setUpdateTime(LocalDateTime.now());
        constitutionTypeMapper.update(type);
    }

    /**
     * 修改体质类型状态（启用/禁用）
     *
     * @param param 状态修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyConstitutionTypeStatus(ModifyFitnessTypeStatusAdminParam param) {
        FitnessType type = constitutionTypeMapper.selectOneById(param.getId());
        if (type == null || type.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "体质类型不存在");
        }
        type.setIsDisabled(param.getIsDisabled());
        type.setUpdateTime(LocalDateTime.now());
        constitutionTypeMapper.update(type);
    }

    /**
     * 体质类型实体转VO
     *
     * @param type 体质类型实体
     * @return 体质类型VO
     */
    private FitnessTypeAdminVO convertToVO(FitnessType type) {
        FitnessTypeAdminVO vo = new FitnessTypeAdminVO();
        vo.setId(type.getId());
        vo.setCode(type.getCode());
        vo.setName(type.getName());
        vo.setNameEn(type.getNameEn());
        vo.setDescription(type.getDescription());
        vo.setCharacteristics(type.getCharacteristics());
        vo.setFormationReason(type.getFormationReason());
        vo.setHealthAdvice(type.getHealthAdvice());
        vo.setDietGuidance(type.getDietGuidance());
        vo.setExerciseGuidance(type.getExerciseGuidance());
        vo.setEmotionGuidance(type.getEmotionGuidance());
        vo.setAcupointGuidance(type.getAcupointGuidance());
        vo.setSortOrder(type.getSortOrder());
        vo.setIsDisabled(type.getIsDisabled());
        vo.setCreateTime(type.getCreateTime());
        return vo;
    }
}
