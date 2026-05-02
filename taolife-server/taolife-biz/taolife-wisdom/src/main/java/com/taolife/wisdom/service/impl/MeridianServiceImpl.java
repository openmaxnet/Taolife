package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Meridian;
import com.taolife.wisdom.enums.MeridianCategoryEnum;
import com.taolife.wisdom.mapper.MeridianMapper;
import com.taolife.wisdom.param.MeridianQueryParam;
import com.taolife.wisdom.param.MeridianSaveParam;
import com.taolife.wisdom.service.IMeridianService;
import com.taolife.wisdom.vo.MeridianDetailVO;
import com.taolife.wisdom.vo.MeridianListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 经络服务实现类
 * 实现经络管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MeridianServiceImpl implements IMeridianService {

    private final MeridianMapper meridianMapper;

    /**
     * 获取经络列表
     * 根据条件分页获取经络数据
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<MeridianListVO> getMeridianPage(MeridianQueryParam param) {
        // 执行分页查询
        Page<Meridian> page = meridianMapper.selectMeridianPage(param);

        // 转换为VO列表
        List<MeridianListVO> voList;
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
     * 获取经络详情
     * 根据经络ID查询经络详情，如果经络不存在则抛出业务异常
     *
     * @param id 经络ID
     * @return 经络详情
     */
    @Override
    public MeridianDetailVO getMeridianDetail(String id) {
        // 根据ID查询经络
        Meridian meridian = meridianMapper.selectById(id);

        if (meridian == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "经络不存在");
        }

        // 转换为详情VO
        return convertToDetailVO(meridian);
    }

    /**
     * 创建经络
     *
     * @param param 经络保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMeridian(MeridianSaveParam param) {
        // 构建经络实体
        Meridian meridian = new Meridian();
        meridian.setCode(param.getCode());
        meridian.setName(param.getName());
        meridian.setNamePinyin(param.getNamePinyin());
        meridian.setCategory(param.getCategory());
        meridian.setDescription(param.getDescription());
        meridian.setPathDescription(param.getPathDescription());
        meridian.setMainIndications(param.getMainIndications());
        meridian.setLineColor(param.getLineColor());
        meridian.setLineWidth(param.getLineWidth());
        meridian.setSortOrder(param.getSortOrder());
        meridian.setIsDisabled(0);
        meridian.setIsDeleted(0);
        meridian.setCreateTime(LocalDateTime.now());

        // 插入数据库
        meridianMapper.insert(meridian);
        log.info("创建经络成功：code={}", param.getCode());
    }

    /**
     * 修改经络信息
     *
     * @param id    经络ID
     * @param param 经络保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyMeridianInfo(String id, MeridianSaveParam param) {
        // 查询经络是否存在
        Meridian meridian = meridianMapper.selectByIdForAdmin(id);
        if (meridian == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "经络不存在");
        }

        // 更新经络信息
        if (param.getCode() != null) {
            meridian.setCode(param.getCode());
        }
        if (param.getName() != null) {
            meridian.setName(param.getName());
        }
        if (param.getNamePinyin() != null) {
            meridian.setNamePinyin(param.getNamePinyin());
        }
        if (param.getCategory() != null) {
            meridian.setCategory(param.getCategory());
        }
        if (param.getDescription() != null) {
            meridian.setDescription(param.getDescription());
        }
        if (param.getPathDescription() != null) {
            meridian.setPathDescription(param.getPathDescription());
        }
        if (param.getMainIndications() != null) {
            meridian.setMainIndications(param.getMainIndications());
        }
        if (param.getLineColor() != null) {
            meridian.setLineColor(param.getLineColor());
        }
        if (param.getLineWidth() != null) {
            meridian.setLineWidth(param.getLineWidth());
        }
        if (param.getSortOrder() != null) {
            meridian.setSortOrder(param.getSortOrder());
        }
        meridian.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        meridianMapper.update(meridian);
        log.info("修改经络信息成功：id={}", id);
    }

    /**
     * 删除经络
     *
     * @param id 经络ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMeridian(String id) {
        // 查询经络是否存在
        Meridian meridian = meridianMapper.selectByIdForAdmin(id);
        if (meridian == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "经络不存在");
        }

        // 执行逻辑删除
        meridian.setIsDeleted(1);
        meridian.setUpdateTime(LocalDateTime.now());
        meridianMapper.update(meridian);
        log.info("删除经络成功：id={}", id);
    }

    /**
     * 修改经络状态
     *
     * @param id         经络ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyMeridianStatus(String id, Integer isDisabled) {
        // 查询经络是否存在
        Meridian meridian = meridianMapper.selectByIdForAdmin(id);
        if (meridian == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "经络不存在");
        }

        // 更新禁用状态
        meridian.setIsDisabled(isDisabled);
        meridian.setUpdateTime(LocalDateTime.now());
        meridianMapper.update(meridian);
        log.info("修改经络状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    /**
     * 转换为列表VO
     *
     * @param meridian 经络实体
     * @return 列表VO
     */
    private MeridianListVO convertToListVO(Meridian meridian) {
        MeridianListVO vo = new MeridianListVO();
        vo.setId(meridian.getId());
        vo.setCode(meridian.getCode());
        vo.setName(meridian.getName());
        vo.setNamePinyin(meridian.getNamePinyin());
        vo.setCategory(meridian.getCategory());
        vo.setCategoryName(getCategoryName(meridian.getCategory()));
        // 简要显示描述，取前100个字符
        vo.setDescription(truncateText(meridian.getDescription(), 100));
        vo.setMainIndications(meridian.getMainIndications());
        vo.setLineColor(meridian.getLineColor());
        vo.setLineWidth(meridian.getLineWidth());
        return vo;
    }

    /**
     * 转换为详情VO
     *
     * @param meridian 经络实体
     * @return 详情VO
     */
    private MeridianDetailVO convertToDetailVO(Meridian meridian) {
        MeridianDetailVO vo = new MeridianDetailVO();
        vo.setId(meridian.getId());
        vo.setCode(meridian.getCode());
        vo.setName(meridian.getName());
        vo.setNamePinyin(meridian.getNamePinyin());
        vo.setCategory(meridian.getCategory());
        vo.setCategoryName(getCategoryName(meridian.getCategory()));
        vo.setDescription(meridian.getDescription());
        vo.setPathDescription(meridian.getPathDescription());
        vo.setMainIndications(meridian.getMainIndications());
        vo.setLineColor(meridian.getLineColor());
        vo.setLineWidth(meridian.getLineWidth());
        vo.setSortOrder(meridian.getSortOrder());
        return vo;
    }

    /**
     * 获取分类名称
     *
     * @param category 分类值
     * @return 分类名称
     */
    private String getCategoryName(Integer category) {
        MeridianCategoryEnum categoryEnum = MeridianCategoryEnum.getByValue(category);
        return categoryEnum != null ? categoryEnum.getName() : null;
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
