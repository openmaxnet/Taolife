package com.taolife.identity.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.identity.param.InteractionToggleParam;
import com.taolife.identity.service.IUserInteractionService;
import com.taolife.identity.vo.UserInteractionStatusVO;
import com.taolife.identity.vo.UserStatsVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户互动控制器
 *
 * @author 文二
 * @date 2026-04-28
 */
@Slf4j
@RestController
@RequestMapping("/api/identity/interaction")
@RequiredArgsConstructor
public class UserInteractionController {

    private final IUserInteractionService interactionService;

    /**
     * 切换互动状态
     * 对目标进行点赞/收藏等互动操作，重复操作将取消
     *
     * @param param 互动切换参数（目标类型、目标ID、互动类型）
     * @return 当前互动状态
     */
    @PostMapping("/toggleInteraction")
    public ExceptionResult<UserInteractionStatusVO> toggleInteraction(@Valid @RequestBody InteractionToggleParam param) {
        String accountId = UserContext.getAccountId();
        log.info("互动切换：accountId={}, targetType={}, targetId={}, interactionType={}",
                accountId, param.getTargetType(), param.getTargetId(), param.getInteractionType());
        return ExceptionResult.success(interactionService.toggleInteraction(
                accountId, param.getTargetType(), param.getTargetId(), param.getInteractionType()));
    }

    /**
     * 获取互动状态
     * 查询当前用户对指定目标的互动状态
     *
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @return 互动状态
     */
    @GetMapping("/getInteractionStatus")
    public ExceptionResult<UserInteractionStatusVO> getInteractionStatus(
            @RequestParam("targetType") Integer targetType,
            @RequestParam("targetId") String targetId) {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(interactionService.getInteractionStatus(accountId, targetType, targetId));
    }

    /**
     * 获取互动统计数据
     * 查询当前用户的互动统计信息（点赞数、收藏数等）
     *
     * @return 互动统计数据
     */
    @GetMapping("/getInteractionStats")
    public ExceptionResult<UserStatsVO> getInteractionStats() {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(interactionService.getInteractionStats(accountId));
    }
}
