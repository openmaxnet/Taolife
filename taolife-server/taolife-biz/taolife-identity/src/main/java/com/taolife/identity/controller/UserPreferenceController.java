package com.taolife.identity.controller;

import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.identity.param.*;
import com.taolife.identity.service.IUserPreferenceService;
import com.taolife.identity.vo.UserPreferenceProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户偏好/健康档案控制器
 *
 * @author 文二
 * @date 2026-04-28
 */
@RestController
@RequestMapping("/api/identity/preference")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final IUserPreferenceService userPreferenceService;

    /**
     * 获取偏好聚合档案
     * 查询当前用户的完整偏好档案（身体数据、饮食偏好、运动偏好、生活习惯、健康目标、AI偏好）
     *
     * @return 偏好聚合档案
     */
    @GetMapping("/getProfile")
    public ExceptionResult<UserPreferenceProfileVO> getProfile() {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        return ExceptionResult.success(userPreferenceService.getProfile(accountId));
    }

    /**
     * 更新身体数据
     * 更新用户的身高、体重、血型、过敏史、病史等信息
     *
     * @param param 身体数据参数
     * @return 操作结果
     */
    @PostMapping("/updateHealthProfile")
    public ExceptionResult<Void> updateHealthProfile(@Valid @RequestBody HealthProfileParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        userPreferenceService.updateHealthProfile(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 更新饮食偏好
     * 更新用户的饮食偏好、忌口食材、膳食限制、偏好菜系等信息
     *
     * @param param 饮食偏好参数
     * @return 操作结果
     */
    @PostMapping("/updateDietary")
    public ExceptionResult<Void> updateDietary(@Valid @RequestBody DietaryPreferenceParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        userPreferenceService.updateDietary(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 更新运动偏好
     * 更新用户的运动类型、强度、时段、时长等偏好
     *
     * @param param 运动偏好参数
     * @return 操作结果
     */
    @PostMapping("/updateExercise")
    public ExceptionResult<Void> updateExercise(@Valid @RequestBody ExercisePreferenceParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        userPreferenceService.updateExercise(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 更新生活习惯
     * 更新用户的睡眠时间、压力水平、烟酒状态等生活习惯
     *
     * @param param 生活习惯参数
     * @return 操作结果
     */
    @PostMapping("/updateLifestyle")
    public ExceptionResult<Void> updateLifestyle(@Valid @RequestBody LifestylePreferenceParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        userPreferenceService.updateLifestyle(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 更新健康目标
     * 更新用户的主要健康目标和具体目标列表
     *
     * @param param 健康目标参数
     * @return 操作结果
     */
    @PostMapping("/updateHealthGoals")
    public ExceptionResult<Void> updateHealthGoals(@Valid @RequestBody HealthGoalsParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        userPreferenceService.updateHealthGoals(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 更新AI偏好
     * 更新用户的AI回复风格和详细程度设置
     *
     * @param param AI偏好参数
     * @return 操作结果
     */
    @PostMapping("/updateAiPreference")
    public ExceptionResult<Void> updateAiPreference(@Valid @RequestBody AiPreferenceParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        userPreferenceService.updateAiPreference(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 记录用户行为
     * 记录用户的操作行为（查看食材/运动/穴位、收藏食材等），用于偏好计算
     *
     * @param body 行为数据，包含 behaviorType（行为类型）和 behaviorDetail（行为详情JSON）
     * @return 操作结果
     */
    @PostMapping("/recordBehavior")
    public ExceptionResult<Void> recordBehavior(@RequestBody java.util.Map<String, Object> body) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        Integer behaviorType = (Integer) body.get("behaviorType");
        String behaviorDetail = (String) body.get("behaviorDetail");
        userPreferenceService.recordUserBehavior(accountId, behaviorType, behaviorDetail);
        return ExceptionResult.success();
    }
}
