package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.entity.PlanSquare;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanSquareMapper;
import com.taolife.plan.param.PlanSquareActionParam;
import com.taolife.plan.param.PlanSquareQueryParam;
import com.taolife.plan.param.PlanSquareShareParam;
import com.taolife.plan.service.IPlanSquareService;
import com.taolife.fee.service.IPointsService;
import com.taolife.fee.service.IPointsRuleService;
import com.taolife.plan.vo.PlanSquareDetailVO;
import com.taolife.plan.vo.PlanSquareListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方案广场服务实现类
 * 实现方案广场相关的具体业务逻辑
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanSquareServiceImpl implements IPlanSquareService {

    private final PlanSquareMapper planSquareMapper;
    private final UserPlanMapper userPlanMapper;
    private final IPointsService pointsService;
    private final IPointsRuleService pointsRuleService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 分页查询方案广场列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<PlanSquareListVO> getSquarePage(PlanSquareQueryParam param) {
        // 执行分页查询
        Page<PlanSquare> page = planSquareMapper.selectSquarePage(param);

        // 转换为VO列表
        List<PlanSquareListVO> voList;
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
     * 获取方案广场详情
     *
     * @param id 方案广场ID
     * @return 方案广场详情
     */
    @Override
    public PlanSquareDetailVO getSquareDetail(String id) {
        // 根据ID查询方案广场
        PlanSquare square = planSquareMapper.selectById(id);

        if (square == null) {
            throw new BusinessException(ExceptionCode.PLAN_SQUARE_NOT_FOUND, "方案广场不存在");
        }

        // 增加浏览次数
        square.setViewCount(square.getViewCount() != null ? square.getViewCount() + 1 : 1);
        planSquareMapper.update(square);

        // 转换为详情VO
        return convertToDetailVO(square);
    }

    /**
     * 分享方案到广场
     *
     * @param accountId 账号ID
     * @param param 分享参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shareToSquare(String accountId, PlanSquareShareParam param) {
        // 查询用户方案
        UserPlan plan = userPlanMapper.selectById(param.getUserPlanId());
        if (plan == null) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }

        // 校验方案归属
        if (!plan.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "无权操作此方案");
        }

        // 创建方案广场记录
        PlanSquare square = new PlanSquare();
        square.setAccountId(accountId);
        square.setUserPlanId(param.getUserPlanId());
        square.setNickname(UserContext.get().getNickname());
        square.setAvatarUrl(UserContext.get().getAvatarUrl());
        square.setConstitutionName(plan.getConstitutionName());
        square.setPlanTitle(plan.getPlanTitle());
        square.setPlanSummary(param.getPlanSummary());
        square.setPlanTags(param.getPlanTags());
        square.setCompletionRate(plan.getCompletionRate());
        square.setAdjustmentCount(plan.getAiAdjustmentCount());
        square.setViewCount(0);
        square.setLikeCount(0);
        square.setCollectCount(0);
        square.setCommentCount(0);
        square.setStatus(1);
        square.setIsOfficial(0);
        square.setCreateTime(LocalDateTime.now());

        planSquareMapper.insert(square);

        // 更新方案的分享次数
        plan.setShareCount(plan.getShareCount() != null ? plan.getShareCount() + 1 : 1);
        plan.setUpdateTime(LocalDateTime.now());
        userPlanMapper.update(plan);

        // 奖励积分
        int sharePoints = pointsRuleService.getPointsByCode("PLAN_SHARE");
        pointsService.addPoints(accountId, sharePoints, 2, "PLAN_SHARE", square.getId(), "分享方案到广场");

        log.info("用户[{}]成功分享方案[{}]到广场，广场ID: {}", accountId, param.getUserPlanId(), square.getId());
    }

    /**
     * 方案广场操作（点赞/收藏）
     *
     * @param accountId 账号ID
     * @param param 操作参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planAction(String accountId, PlanSquareActionParam param) {
        // 查询方案广场
        PlanSquare square = planSquareMapper.selectById(param.getSquareId());
        if (square == null) {
            throw new BusinessException(ExceptionCode.PLAN_SQUARE_NOT_FOUND, "方案广场不存在");
        }

        // 根据操作类型处理
        switch (param.getActionType()) {
            case 1:
                // 点赞
                square.setLikeCount(square.getLikeCount() != null ? square.getLikeCount() + 1 : 1);
                break;
            case 2:
                // 取消点赞
                if (square.getLikeCount() != null && square.getLikeCount() > 0) {
                    square.setLikeCount(square.getLikeCount() - 1);
                }
                break;
            case 3:
                // 收藏
                square.setCollectCount(square.getCollectCount() != null ? square.getCollectCount() + 1 : 1);
                break;
            case 4:
                // 取消收藏
                if (square.getCollectCount() != null && square.getCollectCount() > 0) {
                    square.setCollectCount(square.getCollectCount() - 1);
                }
                break;
            default:
                break;
        }

        square.setUpdateTime(LocalDateTime.now());
        planSquareMapper.update(square);

        log.info("用户[{}]对方案广场[{}]执行操作，类型: {}", accountId, param.getSquareId(), param.getActionType());
    }

    /**
     * 转换为列表VO
     *
     * @param square 方案广场实体
     * @return 列表VO
     */
    private PlanSquareListVO convertToListVO(PlanSquare square) {
        PlanSquareListVO vo = new PlanSquareListVO();
        vo.setId(square.getId());
        vo.setNickname(square.getNickname());
        vo.setAvatarUrl(square.getAvatarUrl());
        vo.setConstitutionName(square.getConstitutionName());
        vo.setPlanTitle(square.getPlanTitle());
        vo.setPlanSummary(truncateText(square.getPlanSummary(), 200));
        vo.setPlanTags(square.getPlanTags());
        vo.setCompletionRate(square.getCompletionRate());
        vo.setViewCount(square.getViewCount());
        vo.setLikeCount(square.getLikeCount());
        vo.setCollectCount(square.getCollectCount());
        vo.setCommentCount(square.getCommentCount());
        vo.setIsOfficial(square.getIsOfficial());
        vo.setCreateTime(formatDateTime(square.getCreateTime()));
        return vo;
    }

    /**
     * 转换为详情VO
     *
     * @param square 方案广场实体
     * @return 详情VO
     */
    private PlanSquareDetailVO convertToDetailVO(PlanSquare square) {
        PlanSquareDetailVO vo = new PlanSquareDetailVO();
        vo.setId(square.getId());
        vo.setNickname(square.getNickname());
        vo.setAvatarUrl(square.getAvatarUrl());
        vo.setConstitutionName(square.getConstitutionName());
        vo.setPlanTitle(square.getPlanTitle());
        vo.setPlanSummary(square.getPlanSummary());
        vo.setPlanTags(square.getPlanTags());
        vo.setCompletionRate(square.getCompletionRate());
        vo.setAdjustmentCount(square.getAdjustmentCount());
        vo.setViewCount(square.getViewCount());
        vo.setLikeCount(square.getLikeCount());
        vo.setCollectCount(square.getCollectCount());
        vo.setCommentCount(square.getCommentCount());
        vo.setIsOfficial(square.getIsOfficial());
        vo.setPlanType(square.getPlanType());
        // 初始实现，后续可通过Redis增强
        vo.setIsLiked(false);
        vo.setIsCollected(false);
        vo.setCreateTime(formatDateTime(square.getCreateTime()));
        return vo;
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

    /**
     * 格式化日期时间
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
