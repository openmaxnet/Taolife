package com.taolife.identity.service;

import com.taolife.identity.entity.UserPreference;
import com.taolife.identity.param.*;
import com.taolife.identity.vo.UserPreferenceProfileVO;

/**
 * 用户偏好服务接口
 *
 * @author 文二
 * @date 2026-04-05
 */
public interface IUserPreferenceService {

    /**
     * 获取用户偏好
     * 根据账号ID查询用户偏好设置
     *
     * @param accountId 账号ID
     * @return 用户偏好实体
     */
    UserPreference getUserPreference(String accountId);

    /**
     * 保存用户偏好
     * 新增或更新用户偏好设置
     *
     * @param preference 用户偏好实体
     */
    void saveUserPreference(UserPreference preference);

    /**
     * 记录用户行为
     * 记录用户的操作行为并更新偏好数据
     *
     * @param accountId      账号ID
     * @param behaviorType   行为类型
     * @param behaviorDetail 行为详情JSON
     */
    void recordUserBehavior(String accountId, Integer behaviorType, String behaviorDetail);

    /**
     * 更新用户偏好评分
     * 根据行为数据重新计算食材和运动的偏好评分
     *
     * @param accountId 账号ID
     */
    void updateUserPreferenceScore(String accountId);

    /**
     * 获取偏好聚合档案
     * 查询用户完整的偏好档案信息
     *
     * @param accountId 账号ID
     * @return 偏好档案VO
     */
    UserPreferenceProfileVO getProfile(String accountId);

    /**
     * 更新身体数据
     *
     * @param accountId 账号ID
     * @param param     身体数据参数
     */
    void updateHealthProfile(String accountId, HealthProfileParam param);

    /**
     * 更新饮食偏好
     *
     * @param accountId 账号ID
     * @param param     饮食偏好参数
     */
    void updateDietary(String accountId, DietaryPreferenceParam param);

    /**
     * 更新运动偏好
     *
     * @param accountId 账号ID
     * @param param     运动偏好参数
     */
    void updateExercise(String accountId, ExercisePreferenceParam param);

    /**
     * 更新生活习惯
     *
     * @param accountId 账号ID
     * @param param     生活习惯参数
     */
    void updateLifestyle(String accountId, LifestylePreferenceParam param);

    /**
     * 更新健康目标
     *
     * @param accountId 账号ID
     * @param param     健康目标参数
     */
    void updateHealthGoals(String accountId, HealthGoalsParam param);

    /**
     * 更新AI偏好
     *
     * @param accountId 账号ID
     * @param param     AI偏好参数
     */
    void updateAiPreference(String accountId, AiPreferenceParam param);
}
