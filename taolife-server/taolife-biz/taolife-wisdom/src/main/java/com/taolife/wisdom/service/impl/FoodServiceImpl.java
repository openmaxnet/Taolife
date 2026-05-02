package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Food;
import com.taolife.wisdom.enums.FoodCategoryEnum;
import com.taolife.wisdom.enums.FoodNatureEnum;
import com.taolife.wisdom.mapper.FoodMapper;
import com.taolife.wisdom.param.FoodQueryParam;
import com.taolife.wisdom.param.FoodSaveParam;
import com.taolife.wisdom.service.IFoodService;
import com.taolife.wisdom.vo.FoodDetailVO;
import com.taolife.wisdom.vo.FoodListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * 食材服务实现类
 * 实现食材管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-03-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements IFoodService {

    private final FoodMapper foodMapper;

    /**
     * 获取食材列表
     * 根据条件分页获取食材数据
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<FoodListVO> getFoodPage(FoodQueryParam param) {
        // 执行分页查询
        Page<Food> page = foodMapper.selectFoodPage(param);

        // 转换为VO列表
        List<FoodListVO> voList;
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
     * 获取食材详情
     * 根据食材ID查询食材详情，如果食材不存在则抛出业务异常
     *
     * @param id 食材ID
     * @return 食材详情
     */
    @Override
    public FoodDetailVO getFoodDetail(String id) {
        // 根据ID查询食材
        Food food = foodMapper.selectById(id);

        if (food == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "食材不存在");
        }

        // 转换为详情VO
        return convertToDetailVO(food);
    }

    /**
     * 创建食材
     * 创建一条新的食材记录
     *
     * @param param 食材创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFood(FoodSaveParam param) {
        if (param.getName() == null || param.getName().isEmpty()) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "食材名称不能为空");
        }

        Food food = new Food();
        food.setName(param.getName());
        food.setNamePinyin(param.getNamePinyin());
        food.setCategory(param.getCategory());
        food.setNature(param.getNature());
        food.setFlavor(param.getFlavor());
        food.setMeridianEntry(param.getMeridianEntry());
        food.setEfficacy(param.getEfficacy());
        food.setIndications(param.getIndications());
        food.setContraindications(param.getContraindications());
        food.setUsage(param.getUsage());
        food.setRecipes(param.getRecipes());
        food.setImageUrl(param.getImageUrl());
        food.setSortOrder(param.getSortOrder());
        food.setViewCount(0);
        food.setCollectCount(0);
        food.setIsDisabled(0);
        food.setIsDeleted(0);
        food.setCreateTime(LocalDateTime.now());
        foodMapper.insert(food);
        log.info("创建食材成功：name={}", param.getName());
    }

    /**
     * 修改食材信息
     * 根据食材ID修改食材信息，仅更新非空字段
     *
     * @param id    食材ID
     * @param param 食材修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyFoodInfo(String id, FoodSaveParam param) {
        Food food = foodMapper.selectByIdForAdmin(id);
        if (food == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "食材不存在");
        }

        if (param.getName() != null && !param.getName().isEmpty()) {
            food.setName(param.getName());
        }
        if (param.getNamePinyin() != null && !param.getNamePinyin().isEmpty()) {
            food.setNamePinyin(param.getNamePinyin());
        }
        if (param.getCategory() != null) {
            food.setCategory(param.getCategory());
        }
        if (param.getNature() != null) {
            food.setNature(param.getNature());
        }
        if (param.getFlavor() != null) {
            food.setFlavor(param.getFlavor());
        }
        if (param.getMeridianEntry() != null) {
            food.setMeridianEntry(param.getMeridianEntry());
        }
        if (param.getEfficacy() != null) {
            food.setEfficacy(param.getEfficacy());
        }
        if (param.getIndications() != null) {
            food.setIndications(param.getIndications());
        }
        if (param.getContraindications() != null) {
            food.setContraindications(param.getContraindications());
        }
        if (param.getUsage() != null) {
            food.setUsage(param.getUsage());
        }
        if (param.getRecipes() != null) {
            food.setRecipes(param.getRecipes());
        }
        if (param.getImageUrl() != null) {
            food.setImageUrl(param.getImageUrl());
        }
        if (param.getSortOrder() != null) {
            food.setSortOrder(param.getSortOrder());
        }
        food.setUpdateTime(LocalDateTime.now());
        foodMapper.update(food);
        log.info("修改食材信息成功：id={}", id);
    }

    /**
     * 删除食材（逻辑删除）
     * 将食材标记为已删除状态
     *
     * @param id 食材ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFood(String id) {
        Food food = foodMapper.selectByIdForAdmin(id);
        if (food == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "食材不存在");
        }

        food.setIsDeleted(1);
        food.setUpdateTime(LocalDateTime.now());
        foodMapper.update(food);
        log.info("删除食材成功：id={}", id);
    }

    /**
     * 修改食材状态
     * 启用或禁用食材
     *
     * @param id         食材ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyFoodStatus(String id, Integer isDisabled) {
        Food food = foodMapper.selectByIdForAdmin(id);
        if (food == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "食材不存在");
        }

        food.setIsDisabled(isDisabled);
        food.setUpdateTime(LocalDateTime.now());
        foodMapper.update(food);
        log.info("修改食材状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    /**
     * 转换为列表VO
     *
     * @param food 食材实体
     * @return 列表VO
     */
    private FoodListVO convertToListVO(Food food) {
        FoodListVO vo = new FoodListVO();
        vo.setId(food.getId());
        vo.setName(food.getName());
        vo.setNamePinyin(food.getNamePinyin());
        vo.setCategory(food.getCategory());
        vo.setCategoryName(getCategoryName(food.getCategory()));
        vo.setNature(food.getNature());
        vo.setNatureName(getNatureName(food.getNature()));
        vo.setFlavor(food.getFlavor());
        vo.setMeridianEntry(food.getMeridianEntry());
        // 简要显示功效，取前100个字符
        vo.setEfficacy(truncateText(food.getEfficacy(), 100));
        vo.setImageUrl(food.getImageUrl());
        vo.setViewCount(food.getViewCount());
        vo.setCollectCount(food.getCollectCount());
        return vo;
    }

    /**
     * 转换为详情VO
     *
     * @param food 食材实体
     * @return 详情VO
     */
    private FoodDetailVO convertToDetailVO(Food food) {
        FoodDetailVO vo = new FoodDetailVO();
        vo.setId(food.getId());
        vo.setName(food.getName());
        vo.setNamePinyin(food.getNamePinyin());
        vo.setCategory(food.getCategory());
        vo.setCategoryName(getCategoryName(food.getCategory()));
        vo.setNature(food.getNature());
        vo.setNatureName(getNatureName(food.getNature()));
        vo.setFlavor(food.getFlavor());
        vo.setMeridianEntry(food.getMeridianEntry());
        vo.setEfficacy(food.getEfficacy());
        vo.setIndications(food.getIndications());
        vo.setContraindications(food.getContraindications());
        vo.setUsage(food.getUsage());
        vo.setRecipes(food.getRecipes());
        vo.setImageUrl(food.getImageUrl());
        vo.setViewCount(food.getViewCount());
        vo.setCollectCount(food.getCollectCount());
        return vo;
    }

    /**
     * 获取分类名称
     *
     * @param category 分类值
     * @return 分类名称
     */
    private String getCategoryName(Integer category) {
        FoodCategoryEnum categoryEnum = FoodCategoryEnum.getByValue(category);
        return categoryEnum != null ? categoryEnum.getName() : null;
    }

    /**
     * 获取性质名称
     *
     * @param nature 性质值
     * @return 性质名称
     */
    private String getNatureName(Integer nature) {
        FoodNatureEnum natureEnum = FoodNatureEnum.getByValue(nature);
        return natureEnum != null ? natureEnum.getName() : null;
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
