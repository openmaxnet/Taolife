package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.AcupointCombo;
import com.taolife.wisdom.mapper.AcupointComboMapper;
import com.taolife.wisdom.param.AcupointComboSaveParam;
import com.taolife.wisdom.service.IAcupointComboService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 穴位配伍服务实现类
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcupointComboServiceImpl implements IAcupointComboService {

    private final AcupointComboMapper acupointComboMapper;

    /**
     * 分页查询穴位配伍列表（管理后台）
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param category 分类（可选）
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<AcupointCombo> getAcupointCombinePage(Integer pageNo, Integer pageSize, Integer category, String keyword) {
        Page<AcupointCombo> page = acupointComboMapper.selectAdminPage(new Page<>(pageNo, pageSize), category, keyword);
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 获取穴位配伍详情（管理后台）
     *
     * @param id 穴位配伍ID
     * @return 穴位配伍详情
     */
    @Override
    public AcupointCombo getAcupointCombineDetail(String id) {
        AcupointCombo combine = acupointComboMapper.selectByIdForAdmin(id);
        if (combine == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "穴位配伍不存在");
        }
        return combine;
    }

    /**
     * 创建穴位配伍
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAcupointCombine(AcupointComboSaveParam param) {
        if (param.getName() == null || param.getName().isEmpty()) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "配伍名称不能为空");
        }

        AcupointCombo combine = new AcupointCombo();
        combine.setName(param.getName());
        combine.setCategory(param.getCategory());
        combine.setTargetConstitutionCodes(param.getTargetConstitutionCodes());
        combine.setTargetSeason(param.getTargetSeason());
        combine.setTargetSymptom(param.getTargetSymptom());
        combine.setDescription(param.getDescription());
        combine.setAcupointIds(param.getAcupointIds());
        combine.setAcupointNames(param.getAcupointNames());
        combine.setSequence(param.getSequence());
        combine.setOperationMethod(param.getOperationMethod());
        combine.setDurationMin(param.getDurationMin());
        combine.setFrequencyPerDay(param.getFrequencyPerDay());
        combine.setEfficacy(param.getEfficacy());
        combine.setIndications(param.getIndications());
        combine.setContraindications(param.getContraindications());
        combine.setImageUrl(param.getImageUrl());
        combine.setVideoUrl(param.getVideoUrl());
        combine.setSortOrder(param.getSortOrder());
        combine.setViewCount(0);
        combine.setCollectCount(0);
        combine.setIsDisabled(0);
        combine.setIsDeleted(0);
        combine.setCreateTime(LocalDateTime.now());
        acupointComboMapper.insert(combine);
        log.info("创建穴位配伍成功：name={}", param.getName());
    }

    /**
     * 修改穴位配伍信息
     *
     * @param id    穴位配伍ID
     * @param param 修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAcupointCombineInfo(String id, AcupointComboSaveParam param) {
        AcupointCombo combine = acupointComboMapper.selectByIdForAdmin(id);
        if (combine == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "穴位配伍不存在");
        }

        if (param.getName() != null && !param.getName().isEmpty()) {
            combine.setName(param.getName());
        }
        if (param.getCategory() != null) {
            combine.setCategory(param.getCategory());
        }
        if (param.getTargetConstitutionCodes() != null) {
            combine.setTargetConstitutionCodes(param.getTargetConstitutionCodes());
        }
        if (param.getTargetSeason() != null) {
            combine.setTargetSeason(param.getTargetSeason());
        }
        if (param.getTargetSymptom() != null) {
            combine.setTargetSymptom(param.getTargetSymptom());
        }
        if (param.getDescription() != null) {
            combine.setDescription(param.getDescription());
        }
        if (param.getAcupointIds() != null) {
            combine.setAcupointIds(param.getAcupointIds());
        }
        if (param.getAcupointNames() != null) {
            combine.setAcupointNames(param.getAcupointNames());
        }
        if (param.getSequence() != null) {
            combine.setSequence(param.getSequence());
        }
        if (param.getOperationMethod() != null) {
            combine.setOperationMethod(param.getOperationMethod());
        }
        if (param.getDurationMin() != null) {
            combine.setDurationMin(param.getDurationMin());
        }
        if (param.getFrequencyPerDay() != null) {
            combine.setFrequencyPerDay(param.getFrequencyPerDay());
        }
        if (param.getEfficacy() != null) {
            combine.setEfficacy(param.getEfficacy());
        }
        if (param.getIndications() != null) {
            combine.setIndications(param.getIndications());
        }
        if (param.getContraindications() != null) {
            combine.setContraindications(param.getContraindications());
        }
        if (param.getImageUrl() != null) {
            combine.setImageUrl(param.getImageUrl());
        }
        if (param.getVideoUrl() != null) {
            combine.setVideoUrl(param.getVideoUrl());
        }
        if (param.getSortOrder() != null) {
            combine.setSortOrder(param.getSortOrder());
        }
        combine.setUpdateTime(LocalDateTime.now());
        acupointComboMapper.update(combine);
        log.info("修改穴位配伍信息成功：id={}", id);
    }

    /**
     * 删除穴位配伍（逻辑删除）
     *
     * @param id 穴位配伍ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAcupointCombine(String id) {
        AcupointCombo combine = acupointComboMapper.selectByIdForAdmin(id);
        if (combine == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "穴位配伍不存在");
        }
        combine.setIsDeleted(1);
        combine.setUpdateTime(LocalDateTime.now());
        acupointComboMapper.update(combine);
        log.info("删除穴位配伍成功：id={}", id);
    }

    /**
     * 修改穴位配伍状态（启用/禁用）
     *
     * @param id         穴位配伍ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAcupointCombineStatus(String id, Integer isDisabled) {
        AcupointCombo combine = acupointComboMapper.selectByIdForAdmin(id);
        if (combine == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "穴位配伍不存在");
        }
        combine.setIsDisabled(isDisabled);
        combine.setUpdateTime(LocalDateTime.now());
        acupointComboMapper.update(combine);
        log.info("修改穴位配伍状态成功：id={}, isDisabled={}", id, isDisabled);
    }
}
