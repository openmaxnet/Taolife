package com.taolife.identity.service.impl;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.taolife.identity.entity.UserPreference;
import com.taolife.identity.mapper.UserPreferenceMapper;
import com.taolife.identity.param.*;
import com.taolife.identity.service.IUserPreferenceService;
import com.taolife.identity.vo.UserPreferenceProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户偏好服务实现类
 *
 * @author 文二
 * @date 2026-04-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceServiceImpl implements IUserPreferenceService {

    private final UserPreferenceMapper userPreferenceMapper;
    private final ObjectMapper objectMapper;

    // ──── 基础方法 ────

    /**
     * 获取用户偏好
     * 根据账号ID查询用户偏好设置
     *
     * @param accountId 账号ID
     * @return 用户偏好实体
     */
    @Override
    public UserPreference getUserPreference(String accountId) {
        return userPreferenceMapper.selectByAccountId(accountId);
    }

    /**
     * 保存用户偏好
     * 新增或更新用户偏好设置
     *
     * @param preference 用户偏好实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserPreference(UserPreference preference) {
        preference.setUpdateTime(LocalDateTime.now());
        userPreferenceMapper.saveOrUpdate(preference);
    }

    // ──── 偏好档案 ────

    /**
     * 获取偏好聚合档案
     * 查询用户完整的偏好档案信息（身体数据、饮食、运动、生活习惯等）
     *
     * @param accountId 账号ID
     * @return 偏好档案VO
     */
    @Override
    public UserPreferenceProfileVO getProfile(String accountId) {
        UserPreference p = getOrCreate(accountId);
        return toProfileVO(p);
    }

    /**
     * 更新身体数据
     * 更新用户的身高、体重、血型、过敏史和病史
     *
     * @param accountId 账号ID
     * @param param     身体数据参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHealthProfile(String accountId, HealthProfileParam param) {
        UserPreference p = getOrCreate(accountId);
        p.setHeight(param.getHeight());
        p.setWeight(param.getWeight());
        p.setBloodType(param.getBloodType());
        p.setAllergyHistory(param.getAllergyHistory());
        p.setMedicalHistory(param.getMedicalHistory());
        touchUserEdit(p);
    }

    /**
     * 更新饮食偏好
     * 更新用户的饮食偏好、忌口、膳食限制等信息
     *
     * @param accountId 账号ID
     * @param param     饮食偏好参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDietary(String accountId, DietaryPreferenceParam param) {
        UserPreference p = getOrCreate(accountId);
        p.setPreferredFoodNature(param.getPreferredFoodNature());
        p.setPreferredFoodTexture(param.getPreferredFoodTexture());
        p.setDailyWaterIntake(param.getDailyWaterIntake());
        p.setDietaryGoal(param.getDietaryGoal());
        try {
            if (param.getDislikedFoods() != null) {
                p.setDislikedFoods(objectMapper.writeValueAsString(param.getDislikedFoods()));
            }
            if (param.getAllergicFoods() != null) {
                p.setAllergicFoods(objectMapper.writeValueAsString(param.getAllergicFoods()));
            }
            if (param.getDietaryRestrictions() != null) {
                p.setDietaryRestrictions(objectMapper.writeValueAsString(param.getDietaryRestrictions()));
            }
            if (param.getPreferredCuisines() != null) {
                p.setPreferredCuisines(objectMapper.writeValueAsString(param.getPreferredCuisines()));
            }
        } catch (Exception e) {
            log.error("序列化饮食偏好失败", e);
        }
        touchUserEdit(p);
    }

    /**
     * 更新运动偏好
     * 更新用户的运动类型、强度、时间等偏好
     *
     * @param accountId 账号ID
     * @param param     运动偏好参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExercise(String accountId, ExercisePreferenceParam param) {
        UserPreference p = getOrCreate(accountId);
        p.setPreferredExerciseType(param.getPreferredExerciseType());
        p.setPreferredExerciseIntensity(param.getPreferredExerciseIntensity());
        p.setPreferredExerciseTime(param.getPreferredExerciseTime());
        p.setPreferredExerciseDuration(param.getPreferredExerciseDuration());
        touchUserEdit(p);
    }

    /**
     * 更新生活习惯
     * 更新用户的睡眠、压力、烟酒等生活习惯
     *
     * @param accountId 账号ID
     * @param param     生活习惯参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLifestyle(String accountId, LifestylePreferenceParam param) {
        UserPreference p = getOrCreate(accountId);
        p.setSleepTime(param.getSleepTime());
        p.setWakeTime(param.getWakeTime());
        p.setSleepQuality(param.getSleepQuality());
        p.setStressLevel(param.getStressLevel());
        p.setSmokingStatus(param.getSmokingStatus());
        p.setDrinkingStatus(param.getDrinkingStatus());
        p.setOccupation(param.getOccupation());
        touchUserEdit(p);
    }

    /**
     * 更新健康目标
     * 更新用户的主要健康目标和具体目标列表
     *
     * @param accountId 账号ID
     * @param param     健康目标参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHealthGoals(String accountId, HealthGoalsParam param) {
        UserPreference p = getOrCreate(accountId);
        p.setHealthGoalPrimary(param.getHealthGoalPrimary());
        try {
            if (param.getHealthGoals() != null) {
                p.setHealthGoals(objectMapper.writeValueAsString(param.getHealthGoals()));
            }
        } catch (Exception e) {
            log.error("序列化健康目标失败", e);
        }
        touchUserEdit(p);
    }

    /**
     * 更新AI偏好
     * 更新用户的AI语气偏好和详细程度设置
     *
     * @param accountId 账号ID
     * @param param     AI偏好参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAiPreference(String accountId, AiPreferenceParam param) {
        UserPreference p = getOrCreate(accountId);
        p.setAiTonePreference(param.getAiTonePreference());
        p.setAiDetailLevel(param.getAiDetailLevel());
        touchUserEdit(p);
    }

    // ──── 行为追踪 ────

    /**
     * 记录用户行为
     * 记录用户的操作行为并更新偏好数据（查看、收藏等）
     *
     * @param accountId      账号ID
     * @param behaviorType   行为类型（1：查看食材，2：收藏食材，3：查看运动，4：查看穴位）
     * @param behaviorDetail 行为详情JSON
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordUserBehavior(String accountId, Integer behaviorType, String behaviorDetail) {
        UserPreference preference = getOrCreate(accountId);

        preference.setTotalInteractions(
            (preference.getTotalInteractions() == null ? 0 : preference.getTotalInteractions()) + 1
        );
        preference.setLastLearnTime(LocalDateTime.now());

        updatePreferenceByBehavior(preference, behaviorType, behaviorDetail);
        userPreferenceMapper.saveOrUpdate(preference);
    }

    /**
     * 更新用户偏好评分
     * 根据已有行为数据重新计算食材和运动的偏好评分
     *
     * @param accountId 账号ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserPreferenceScore(String accountId) {
        UserPreference preference = userPreferenceMapper.selectByAccountId(accountId);
        if (preference == null) {
            return;
        }

        Map<String, Double> foodScore = calculateFoodPreferenceScore(preference);
        Map<String, Double> exerciseScore = calculateExercisePreferenceScore(preference);

        try {
            preference.setFoodPreferenceScore(objectMapper.writeValueAsString(foodScore));
            preference.setExercisePreferenceScore(objectMapper.writeValueAsString(exerciseScore));
            preference.setUpdateTime(LocalDateTime.now());
            userPreferenceMapper.saveOrUpdate(preference);
        } catch (Exception e) {
            log.error("更新偏好评分失败", e);
        }
    }

    // ──── 内部方法 ────

    /** 获取或创建偏好记录 */
    private UserPreference getOrCreate(String accountId) {
        UserPreference p = userPreferenceMapper.selectByAccountId(accountId);
        if (p == null) {
            p = new UserPreference();
            p.setAccountId(accountId);
            p.setTotalInteractions(0);
            p.setPreferenceCompleteness(0);
            p.setCreateTime(LocalDateTime.now());
        }
        return p;
    }

    /** 用户编辑后：更新编辑时间 + 计算完善度 + 保存 */
    private void touchUserEdit(UserPreference p) {
        p.setLastUserEditTime(LocalDateTime.now());
        p.setPreferenceCompleteness(calculateCompleteness(p));
        p.setUpdateTime(LocalDateTime.now());
        userPreferenceMapper.saveOrUpdate(p);
    }

    /** 计算偏好完善度 */
    private int calculateCompleteness(UserPreference p) {
        int filled = 0;
        int total = 0;

        // 身体数据 5 项
        total += 5;
        if (p.getHeight() != null) filled++;
        if (p.getWeight() != null) filled++;
        if (p.getBloodType() != null) filled++;
        if (isNotEmpty(p.getAllergyHistory())) filled++;
        if (isNotEmpty(p.getMedicalHistory())) filled++;

        // 饮食偏好 5 项
        total += 5;
        if (isNotEmpty(p.getPreferredFoodNature())) filled++;
        if (p.getPreferredFoodTexture() != null) filled++;
        if (isNotEmpty(p.getDislikedFoods())) filled++;
        if (isNotEmpty(p.getDietaryRestrictions())) filled++;
        if (p.getDailyWaterIntake() != null) filled++;

        // 运动偏好 4 项
        total += 4;
        if (p.getPreferredExerciseType() != null) filled++;
        if (p.getPreferredExerciseIntensity() != null) filled++;
        if (p.getPreferredExerciseTime() != null) filled++;
        if (p.getPreferredExerciseDuration() != null) filled++;

        // 生活习惯 5 项
        total += 5;
        if (isNotEmpty(p.getSleepTime())) filled++;
        if (isNotEmpty(p.getWakeTime())) filled++;
        if (p.getSleepQuality() != null) filled++;
        if (p.getStressLevel() != null) filled++;
        if (p.getSmokingStatus() != null) filled++;

        // 健康目标 2 项
        total += 2;
        if (p.getHealthGoalPrimary() != null) filled++;
        if (isNotEmpty(p.getHealthGoals())) filled++;

        // AI 偏好 2 项
        total += 2;
        if (p.getAiTonePreference() != null) filled++;
        if (p.getAiDetailLevel() != null) filled++;

        return total == 0 ? 0 : (int) ((double) filled / total * 100);
    }

    private boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    /** 实体 → VO */
    private UserPreferenceProfileVO toProfileVO(UserPreference p) {
        UserPreferenceProfileVO vo = new UserPreferenceProfileVO();
        vo.setId(p.getId());

        // 身体数据
        vo.setHeight(p.getHeight());
        vo.setWeight(p.getWeight());
        vo.setBloodType(p.getBloodType());
        vo.setAllergyHistory(p.getAllergyHistory());
        vo.setMedicalHistory(p.getMedicalHistory());

        // 饮食偏好
        vo.setPreferredFoodNature(p.getPreferredFoodNature());
        vo.setPreferredFoodTexture(p.getPreferredFoodTexture());
        vo.setDailyWaterIntake(p.getDailyWaterIntake());
        vo.setDietaryGoal(p.getDietaryGoal());
        vo.setDislikedFoods(parseJsonList(p.getDislikedFoods()));
        vo.setAllergicFoods(parseJsonList(p.getAllergicFoods()));
        vo.setDietaryRestrictions(parseJsonList(p.getDietaryRestrictions()));
        vo.setPreferredCuisines(parseJsonList(p.getPreferredCuisines()));

        // 运动偏好
        vo.setPreferredExerciseType(p.getPreferredExerciseType());
        vo.setPreferredExerciseIntensity(p.getPreferredExerciseIntensity());
        vo.setPreferredExerciseTime(p.getPreferredExerciseTime());
        vo.setPreferredExerciseDuration(p.getPreferredExerciseDuration());

        // 生活习惯
        vo.setSleepTime(p.getSleepTime());
        vo.setWakeTime(p.getWakeTime());
        vo.setSleepQuality(p.getSleepQuality());
        vo.setStressLevel(p.getStressLevel());
        vo.setSmokingStatus(p.getSmokingStatus());
        vo.setDrinkingStatus(p.getDrinkingStatus());
        vo.setOccupation(p.getOccupation());

        // 健康目标
        vo.setHealthGoalPrimary(p.getHealthGoalPrimary());
        vo.setHealthGoals(parseJsonList(p.getHealthGoals()));

        // AI偏好
        vo.setAiTonePreference(p.getAiTonePreference());
        vo.setAiDetailLevel(p.getAiDetailLevel());

        // 完善度
        vo.setPreferenceCompleteness(p.getPreferenceCompleteness());
        vo.setLastUserEditTime(p.getLastUserEditTime());
        vo.setUpdateTime(p.getUpdateTime());

        return vo;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ──── 行为追踪内部方法 ────

    private void updatePreferenceByBehavior(UserPreference preference, Integer behaviorType,
                                            String behaviorDetail) {
        try {
            switch (behaviorType) {
                case 1: updateFoodViewPreference(preference, behaviorDetail); break;
                case 2: updateFoodCollectPreference(preference, behaviorDetail); break;
                case 3: updateExerciseViewPreference(preference, behaviorDetail); break;
                case 4: updateAcupointViewPreference(preference, behaviorDetail); break;
                default: log.warn("未知的行为类型：{}", behaviorType);
            }
        } catch (Exception e) {
            log.error("更新偏好失败", e);
        }
    }

    private void updateFoodViewPreference(UserPreference preference, String behaviorDetail) {
        try {
            Map<String, Integer> viewCountMap = new HashMap<>();
            if (preference.getFoodPreferenceScore() != null && !preference.getFoodPreferenceScore().isEmpty()) {
                viewCountMap = objectMapper.readValue(preference.getFoodPreferenceScore(),
                    new TypeReference<Map<String, Integer>>() {});
            }
            Map<String, Object> behaviorMap = objectMapper.readValue(behaviorDetail,
                new TypeReference<Map<String, Object>>() {});
            String foodId = (String) behaviorMap.get("foodId");
            if (foodId != null) {
                viewCountMap.put(foodId, viewCountMap.getOrDefault(foodId, 0) + 1);
            }
            preference.setFoodPreferenceScore(objectMapper.writeValueAsString(viewCountMap));
        } catch (Exception e) {
            log.error("更新食材查看偏好失败", e);
        }
    }

    private void updateFoodCollectPreference(UserPreference preference, String behaviorDetail) {
        try {
            List<String> dislikedFoods = new ArrayList<>();
            if (preference.getDislikedFoods() != null && !preference.getDislikedFoods().isEmpty()) {
                dislikedFoods = objectMapper.readValue(preference.getDislikedFoods(),
                    new TypeReference<List<String>>() {});
            }
            Map<String, Object> behaviorMap = objectMapper.readValue(behaviorDetail,
                new TypeReference<Map<String, Object>>() {});
            String foodId = (String) behaviorMap.get("foodId");
            Boolean isCollected = (Boolean) behaviorMap.get("isCollected");
            if (isCollected != null && isCollected && foodId != null) {
                dislikedFoods.remove(foodId);
            }
            preference.setDislikedFoods(objectMapper.writeValueAsString(dislikedFoods));
        } catch (Exception e) {
            log.error("更新食材收藏偏好失败", e);
        }
    }

    private void updateExerciseViewPreference(UserPreference preference, String behaviorDetail) {
        try {
            Map<String, Integer> viewCountMap = new HashMap<>();
            if (preference.getExercisePreferenceScore() != null && !preference.getExercisePreferenceScore().isEmpty()) {
                viewCountMap = objectMapper.readValue(preference.getExercisePreferenceScore(),
                    new TypeReference<Map<String, Integer>>() {});
            }
            Map<String, Object> behaviorMap = objectMapper.readValue(behaviorDetail,
                new TypeReference<Map<String, Object>>() {});
            String exerciseId = (String) behaviorMap.get("exerciseId");
            if (exerciseId != null) {
                viewCountMap.put(exerciseId, viewCountMap.getOrDefault(exerciseId, 0) + 1);
            }
            preference.setExercisePreferenceScore(objectMapper.writeValueAsString(viewCountMap));
        } catch (Exception e) {
            log.error("更新运动查看偏好失败", e);
        }
    }

    private void updateAcupointViewPreference(UserPreference preference, String behaviorDetail) {
        try {
            Map<String, Integer> viewCountMap = new HashMap<>();
            if (preference.getPreferredAcupoints() != null && !preference.getPreferredAcupoints().isEmpty()) {
                viewCountMap = objectMapper.readValue(preference.getPreferredAcupoints(),
                    new TypeReference<Map<String, Integer>>() {});
            }
            Map<String, Object> behaviorMap = objectMapper.readValue(behaviorDetail,
                new TypeReference<Map<String, Object>>() {});
            String acupointId = (String) behaviorMap.get("acupointId");
            if (acupointId != null) {
                viewCountMap.put(acupointId, viewCountMap.getOrDefault(acupointId, 0) + 1);
            }
            preference.setPreferredAcupoints(objectMapper.writeValueAsString(viewCountMap));
        } catch (Exception e) {
            log.error("更新穴位查看偏好失败", e);
        }
    }

    private Map<String, Double> calculateFoodPreferenceScore(UserPreference preference) {
        Map<String, Double> scoreMap = new HashMap<>();
        try {
            if (preference.getFoodPreferenceScore() != null && !preference.getFoodPreferenceScore().isEmpty()) {
                scoreMap = objectMapper.readValue(preference.getFoodPreferenceScore(),
                    new TypeReference<Map<String, Double>>() {});
            }
            if (preference.getDislikedFoods() != null && !preference.getDislikedFoods().isEmpty()) {
                List<String> list = objectMapper.readValue(preference.getDislikedFoods(),
                    new TypeReference<List<String>>() {});
                for (String foodId : list) {
                    scoreMap.put(foodId, -1.0);
                }
            }
            if (preference.getAllergicFoods() != null && !preference.getAllergicFoods().isEmpty()) {
                List<String> list = objectMapper.readValue(preference.getAllergicFoods(),
                    new TypeReference<List<String>>() {});
                for (String foodId : list) {
                    scoreMap.put(foodId, -2.0);
                }
            }
        } catch (Exception e) {
            log.error("计算食材偏好评分失败", e);
        }
        return scoreMap;
    }

    private Map<String, Double> calculateExercisePreferenceScore(UserPreference preference) {
        Map<String, Double> scoreMap = new HashMap<>();
        try {
            if (preference.getExercisePreferenceScore() != null && !preference.getExercisePreferenceScore().isEmpty()) {
                scoreMap = objectMapper.readValue(preference.getExercisePreferenceScore(),
                    new TypeReference<Map<String, Double>>() {});
            }
        } catch (Exception e) {
            log.error("计算运动偏好评分失败", e);
        }
        return scoreMap;
    }
}
