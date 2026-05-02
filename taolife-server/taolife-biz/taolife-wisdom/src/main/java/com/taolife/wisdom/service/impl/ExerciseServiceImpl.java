package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Exercise;
import com.taolife.wisdom.enums.ExerciseCategoryEnum;
import com.taolife.wisdom.enums.ExerciseIntensityEnum;
import com.taolife.wisdom.mapper.ExerciseMapper;
import com.taolife.wisdom.param.ExerciseQueryParam;
import com.taolife.wisdom.param.ExerciseSaveParam;
import com.taolife.wisdom.service.IExerciseService;
import com.taolife.wisdom.vo.ExerciseDetailVO;
import com.taolife.wisdom.vo.ExerciseListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运动项目服务实现类
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements IExerciseService {

    private final ExerciseMapper exerciseMapper;

    // ──── 管理后台 ────

    /**
     * 分页查询运动项目列表（管理后台）
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param category 分类（可选）
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<Exercise> getExercisePage(Integer pageNo, Integer pageSize, Integer category, String keyword) {
        Page<Exercise> page = exerciseMapper.selectAdminPage(new Page<>(pageNo, pageSize), category, keyword);
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 获取运动项目详情（管理后台）
     *
     * @param id 运动项目ID
     * @return 运动项目详情
     */
    @Override
    public Exercise getExerciseDetail(String id) {
        Exercise exercise = exerciseMapper.selectByIdForAdmin(id);
        if (exercise == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "运动项目不存在");
        }
        return exercise;
    }

    /**
     * 创建运动项目
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createExercise(ExerciseSaveParam param) {
        if (param.getName() == null || param.getName().isEmpty()) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "运动名称不能为空");
        }

        Exercise exercise = new Exercise();
        exercise.setName(param.getName());
        exercise.setNamePinyin(param.getNamePinyin());
        exercise.setCategory(param.getCategory());
        exercise.setIntensity(param.getIntensity());
        exercise.setTargetConstitutionCodes(param.getTargetConstitutionCodes());
        exercise.setContraConstitutionCodes(param.getContraConstitutionCodes());
        exercise.setTargetSeason(param.getTargetSeason());
        exercise.setTargetAgeGroup(param.getTargetAgeGroup());
        exercise.setEfficacy(param.getEfficacy());
        exercise.setIndications(param.getIndications());
        exercise.setContraindications(param.getContraindications());
        exercise.setDescription(param.getDescription());
        exercise.setSteps(param.getSteps());
        exercise.setDurationMin(param.getDurationMin());
        exercise.setCaloriesConsumption(param.getCaloriesConsumption());
        exercise.setDifficultyLevel(param.getDifficultyLevel());
        exercise.setVideoUrl(param.getVideoUrl());
        exercise.setVideoType(param.getVideoType());
        exercise.setImageUrl(param.getImageUrl());
        exercise.setSortOrder(param.getSortOrder());
        exercise.setViewCount(0);
        exercise.setCollectCount(0);
        exercise.setIsDisabled(0);
        exercise.setIsDeleted(0);
        exercise.setCreateTime(LocalDateTime.now());
        exerciseMapper.insert(exercise);
        log.info("创建运动项目成功：name={}", param.getName());
    }

    /**
     * 修改运动项目信息
     *
     * @param id    运动项目ID
     * @param param 修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyExerciseInfo(String id, ExerciseSaveParam param) {
        Exercise exercise = exerciseMapper.selectByIdForAdmin(id);
        if (exercise == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "运动项目不存在");
        }

        if (param.getName() != null && !param.getName().isEmpty()) {
            exercise.setName(param.getName());
        }
        if (param.getNamePinyin() != null) {
            exercise.setNamePinyin(param.getNamePinyin());
        }
        if (param.getCategory() != null) {
            exercise.setCategory(param.getCategory());
        }
        if (param.getIntensity() != null) {
            exercise.setIntensity(param.getIntensity());
        }
        if (param.getTargetConstitutionCodes() != null) {
            exercise.setTargetConstitutionCodes(param.getTargetConstitutionCodes());
        }
        if (param.getContraConstitutionCodes() != null) {
            exercise.setContraConstitutionCodes(param.getContraConstitutionCodes());
        }
        if (param.getTargetSeason() != null) {
            exercise.setTargetSeason(param.getTargetSeason());
        }
        if (param.getTargetAgeGroup() != null) {
            exercise.setTargetAgeGroup(param.getTargetAgeGroup());
        }
        if (param.getEfficacy() != null) {
            exercise.setEfficacy(param.getEfficacy());
        }
        if (param.getIndications() != null) {
            exercise.setIndications(param.getIndications());
        }
        if (param.getContraindications() != null) {
            exercise.setContraindications(param.getContraindications());
        }
        if (param.getDescription() != null) {
            exercise.setDescription(param.getDescription());
        }
        if (param.getSteps() != null) {
            exercise.setSteps(param.getSteps());
        }
        if (param.getDurationMin() != null) {
            exercise.setDurationMin(param.getDurationMin());
        }
        if (param.getCaloriesConsumption() != null) {
            exercise.setCaloriesConsumption(param.getCaloriesConsumption());
        }
        if (param.getDifficultyLevel() != null) {
            exercise.setDifficultyLevel(param.getDifficultyLevel());
        }
        if (param.getVideoUrl() != null) {
            exercise.setVideoUrl(param.getVideoUrl());
        }
        if (param.getVideoType() != null) {
            exercise.setVideoType(param.getVideoType());
        }
        if (param.getImageUrl() != null) {
            exercise.setImageUrl(param.getImageUrl());
        }
        if (param.getSortOrder() != null) {
            exercise.setSortOrder(param.getSortOrder());
        }
        exercise.setUpdateTime(LocalDateTime.now());
        exerciseMapper.update(exercise);
        log.info("修改运动项目信息成功：id={}", id);
    }

    /**
     * 删除运动项目（逻辑删除）
     *
     * @param id 运动项目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeExercise(String id) {
        Exercise exercise = exerciseMapper.selectByIdForAdmin(id);
        if (exercise == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "运动项目不存在");
        }
        exercise.setIsDeleted(1);
        exercise.setUpdateTime(LocalDateTime.now());
        exerciseMapper.update(exercise);
        log.info("删除运动项目成功：id={}", id);
    }

    /**
     * 修改运动项目状态（启用/禁用）
     *
     * @param id         运动项目ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyExerciseStatus(String id, Integer isDisabled) {
        Exercise exercise = exerciseMapper.selectByIdForAdmin(id);
        if (exercise == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "运动项目不存在");
        }
        exercise.setIsDisabled(isDisabled);
        exercise.setUpdateTime(LocalDateTime.now());
        exerciseMapper.update(exercise);
        log.info("修改运动项目状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    // ──── 用户端 ────

    /**
     * 分页查询运动项目列表（用户端）
     *
     * @param param 查询参数
     * @return 运动列表分页结果
     */
    @Override
    public PageResult<ExerciseListVO> getExerciseUserPage(ExerciseQueryParam param) {
        if (param.getPageNo() == null) param.setPageNo(1);
        if (param.getPageSize() == null) param.setPageSize(10);
        Page<Exercise> page = exerciseMapper.selectUserPage(new Page<>(param.getPageNo(), param.getPageSize()), param);
        List<ExerciseListVO> voList = page.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取运动项目详情（用户端，含相关推荐）
     *
     * @param id 运动项目ID
     * @return 运动详情VO
     */
    @Override
    public ExerciseDetailVO getExerciseUserDetail(String id) {
        Exercise exercise = exerciseMapper.selectByIdForUser(id);
        if (exercise == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "运动项目不存在");
        }

        ExerciseDetailVO vo = convertToDetailVO(exercise);

        // 查询相关运动（同分类 top 4）
        List<Exercise> related = exerciseMapper.selectRelated(id, exercise.getCategory());
        vo.setRelatedExercises(related.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList()));

        return vo;
    }

    // ──── VO 转换 ────

    /**
     * 运动实体转列表VO
     *
     * @param e 运动项目实体
     * @return 列表VO
     */
    private ExerciseListVO convertToListVO(Exercise e) {
        ExerciseListVO vo = new ExerciseListVO();
        vo.setId(e.getId());
        vo.setName(e.getName());
        vo.setNamePinyin(e.getNamePinyin());
        vo.setCategory(e.getCategory());
        vo.setCategoryName(getCategoryName(e.getCategory()));
        vo.setIntensity(e.getIntensity());
        vo.setIntensityName(getIntensityName(e.getIntensity()));
        vo.setDifficultyLevel(e.getDifficultyLevel());
        vo.setDurationMin(e.getDurationMin());
        vo.setCaloriesConsumption(e.getCaloriesConsumption());
        vo.setEfficacy(truncate(e.getEfficacy(), 100));
        vo.setImageUrl(e.getImageUrl());
        vo.setVideoType(e.getVideoType());
        vo.setViewCount(e.getViewCount());
        vo.setCollectCount(e.getCollectCount());
        return vo;
    }

    /**
     * 运动实体转详情VO
     *
     * @param e 运动项目实体
     * @return 详情VO
     */
    private ExerciseDetailVO convertToDetailVO(Exercise e) {
        ExerciseDetailVO vo = new ExerciseDetailVO();
        vo.setId(e.getId());
        vo.setName(e.getName());
        vo.setNamePinyin(e.getNamePinyin());
        vo.setCategory(e.getCategory());
        vo.setCategoryName(getCategoryName(e.getCategory()));
        vo.setIntensity(e.getIntensity());
        vo.setIntensityName(getIntensityName(e.getIntensity()));
        vo.setDifficultyLevel(e.getDifficultyLevel());
        vo.setDurationMin(e.getDurationMin());
        vo.setCaloriesConsumption(e.getCaloriesConsumption());
        vo.setEfficacy(e.getEfficacy());
        vo.setImageUrl(e.getImageUrl());
        vo.setVideoType(e.getVideoType());
        vo.setVideoUrl(e.getVideoUrl());
        vo.setViewCount(e.getViewCount());
        vo.setCollectCount(e.getCollectCount());
        vo.setDescription(e.getDescription());
        vo.setSteps(e.getSteps());
        vo.setIndications(e.getIndications());
        vo.setContraindications(e.getContraindications());
        vo.setTargetConstitutionCodes(e.getTargetConstitutionCodes());
        vo.setContraConstitutionCodes(e.getContraConstitutionCodes());
        vo.setTargetSeason(e.getTargetSeason());
        vo.setTargetAgeGroup(e.getTargetAgeGroup());
        return vo;
    }

    /**
     * 获取分类名称
     *
     * @param category 分类编码
     * @return 分类名称
     */
    private String getCategoryName(Integer category) {
        if (category == null) return null;
        ExerciseCategoryEnum e = ExerciseCategoryEnum.getByValue(category);
        return e != null ? e.getName() : null;
    }

    /**
     * 获取强度名称
     *
     * @param intensity 强度编码
     * @return 强度名称
     */
    private String getIntensityName(Integer intensity) {
        if (intensity == null) return null;
        ExerciseIntensityEnum e = ExerciseIntensityEnum.getByValue(intensity);
        return e != null ? e.getName() : null;
    }

    /**
     * 截断文本到指定长度
     *
     * @param str    原始文本
     * @param maxLen 最大长度
     * @return 截断后的文本
     */
    private String truncate(String str, int maxLen) {
        if (str == null) return null;
        return str.length() <= maxLen ? str : str.substring(0, maxLen) + "...";
    }
}
