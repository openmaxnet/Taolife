package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Acupoint;
import com.taolife.wisdom.enums.AcupointMarkerTypeEnum;
import com.taolife.wisdom.mapper.AcupointMapper;
import com.taolife.wisdom.param.AcupointQueryParam;
import com.taolife.wisdom.param.AcupointSaveParam;
import com.taolife.wisdom.service.IAcupointService;
import com.taolife.wisdom.vo.AcupointDetailVO;
import com.taolife.wisdom.vo.AcupointListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 穴位服务实现类
 * 实现穴位管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcupointServiceImpl implements IAcupointService {

    private final AcupointMapper acupointMapper;

    /**
     * 获取穴位列表
     * 根据条件分页获取穴位数据
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<AcupointListVO> getAcupointPage(AcupointQueryParam param) {
        // 执行分页查询
        Page<Acupoint> page = acupointMapper.selectAcupointPage(param);

        // 转换为VO列表
        List<AcupointListVO> voList;
        if (page.getRecords() != null) {
            voList = page.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        } else {
            voList = Collections.emptyList();
        }

        return new PageResult<>(voList, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取穴位详情
     * 根据穴位ID查询穴位详情，如果穴位不存在则抛出业务异常
     *
     * @param id 穴位ID
     * @return 穴位详情
     */
    @Override
    public AcupointDetailVO getAcupointDetail(String id) {
        // 根据ID查询穴位
        Acupoint acupoint = acupointMapper.selectById(id);

        if (acupoint == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "穴位不存在");
        }

        // 转换为详情VO
        return convertToDetailVO(acupoint);
    }

    /**
     * 创建穴位
     *
     * @param param 穴位保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAcupoint(AcupointSaveParam param) {
        // 构建穴位实体
        Acupoint acupoint = new Acupoint();
        acupoint.setName(param.getName());
        acupoint.setNamePinyin(param.getNamePinyin());
        acupoint.setCategory(param.getCategory());
        acupoint.setMeridianCode(param.getMeridianCode());
        acupoint.setMeridianName(param.getMeridianName());
        acupoint.setLocationDescription(param.getLocationDescription());
        acupoint.setEfficacy(param.getEfficacy());
        acupoint.setIndications(param.getIndications());
        acupoint.setOperationMethod(param.getOperationMethod());
        acupoint.setMassageTips(param.getMassageTips());
        acupoint.setMarkerType(param.getMarkerType());
        acupoint.setSortOrder(param.getSortOrder());
        acupoint.setIsDisabled(0);
        acupoint.setIsDeleted(0);
        acupoint.setCreateTime(LocalDateTime.now());

        // 插入数据库
        acupointMapper.insert(acupoint);
        log.info("创建穴位成功：name={}", param.getName());
    }

    /**
     * 修改穴位信息
     *
     * @param id    穴位ID
     * @param param 穴位保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAcupointInfo(String id, AcupointSaveParam param) {
        // 查询穴位是否存在
        Acupoint acupoint = acupointMapper.selectByIdForAdmin(id);
        if (acupoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "穴位不存在");
        }

        // 更新穴位信息
        if (param.getName() != null) {
            acupoint.setName(param.getName());
        }
        if (param.getNamePinyin() != null) {
            acupoint.setNamePinyin(param.getNamePinyin());
        }
        if (param.getCategory() != null) {
            acupoint.setCategory(param.getCategory());
        }
        if (param.getMeridianCode() != null) {
            acupoint.setMeridianCode(param.getMeridianCode());
        }
        if (param.getMeridianName() != null) {
            acupoint.setMeridianName(param.getMeridianName());
        }
        if (param.getLocationDescription() != null) {
            acupoint.setLocationDescription(param.getLocationDescription());
        }
        if (param.getEfficacy() != null) {
            acupoint.setEfficacy(param.getEfficacy());
        }
        if (param.getIndications() != null) {
            acupoint.setIndications(param.getIndications());
        }
        if (param.getOperationMethod() != null) {
            acupoint.setOperationMethod(param.getOperationMethod());
        }
        if (param.getMassageTips() != null) {
            acupoint.setMassageTips(param.getMassageTips());
        }
        if (param.getMarkerType() != null) {
            acupoint.setMarkerType(param.getMarkerType());
        }
        if (param.getSortOrder() != null) {
            acupoint.setSortOrder(param.getSortOrder());
        }
        acupoint.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        acupointMapper.update(acupoint);
        log.info("修改穴位信息成功：id={}", id);
    }

    /**
     * 删除穴位
     *
     * @param id 穴位ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAcupoint(String id) {
        // 查询穴位是否存在
        Acupoint acupoint = acupointMapper.selectByIdForAdmin(id);
        if (acupoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "穴位不存在");
        }

        // 执行逻辑删除
        acupoint.setIsDeleted(1);
        acupoint.setUpdateTime(LocalDateTime.now());
        acupointMapper.update(acupoint);
        log.info("删除穴位成功：id={}", id);
    }

    /**
     * 修改穴位状态
     *
     * @param id         穴位ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAcupointStatus(String id, Integer isDisabled) {
        // 查询穴位是否存在
        Acupoint acupoint = acupointMapper.selectByIdForAdmin(id);
        if (acupoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "穴位不存在");
        }

        // 更新禁用状态
        acupoint.setIsDisabled(isDisabled);
        acupoint.setUpdateTime(LocalDateTime.now());
        acupointMapper.update(acupoint);
        log.info("修改穴位状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    /**
     * 转换为列表VO
     *
     * @param acupoint 穴位实体
     * @return 列表VO
     */
    private AcupointListVO convertToListVO(Acupoint acupoint) {
        AcupointListVO vo = new AcupointListVO();
        vo.setId(acupoint.getId());
        vo.setName(acupoint.getName());
        vo.setNamePinyin(acupoint.getNamePinyin());
        vo.setCategory(acupoint.getCategory());
        vo.setCategoryName("经穴");
        vo.setMeridianCode(acupoint.getMeridianCode());
        vo.setMeridianName(acupoint.getMeridianName());
        vo.setLocationDescription(acupoint.getLocationDescription());
        // 简要显示功效，取前100个字符
        vo.setEfficacy(truncateText(acupoint.getEfficacy(), 100));
        vo.setMarkerType(acupoint.getMarkerType());
        vo.setMarkerTypeName(getMarkerTypeName(acupoint.getMarkerType()));
        return vo;
    }

    /**
     * 转换为详情VO
     *
     * @param acupoint 穴位实体
     * @return 详情VO
     */
    private AcupointDetailVO convertToDetailVO(Acupoint acupoint) {
        AcupointDetailVO vo = new AcupointDetailVO();
        vo.setId(acupoint.getId());
        vo.setName(acupoint.getName());
        vo.setNamePinyin(acupoint.getNamePinyin());
        vo.setCategory(acupoint.getCategory());
        vo.setCategoryName("经穴");
        vo.setMeridianCode(acupoint.getMeridianCode());
        vo.setMeridianName(acupoint.getMeridianName());
        vo.setLocationDescription(acupoint.getLocationDescription());
        vo.setEfficacy(acupoint.getEfficacy());
        vo.setIndications(acupoint.getIndications());
        vo.setOperationMethod(acupoint.getOperationMethod());
        vo.setMassageTips(acupoint.getMassageTips());
        vo.setMarkerType(acupoint.getMarkerType());
        vo.setMarkerTypeName(getMarkerTypeName(acupoint.getMarkerType()));
        vo.setSortOrder(acupoint.getSortOrder());
        return vo;
    }

    /**
     * 获取标注类型名称
     *
     * @param markerType 标注类型值
     * @return 标注类型名称
     */
    private String getMarkerTypeName(Integer markerType) {
        AcupointMarkerTypeEnum markerTypeEnum = AcupointMarkerTypeEnum.getByValue(markerType);
        return markerTypeEnum != null ? markerTypeEnum.getName() : null;
    }

    /**
     * 截断文本
     *
     * @param text 原始文本
     * @param maxLength 最大长度
     * @return 截断后的文本
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
