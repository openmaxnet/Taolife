package com.taolife.identity.service.impl;

import com.taolife.identity.entity.UserInteraction;
import com.taolife.identity.mapper.UserFollowMapper;
import com.taolife.identity.mapper.UserInteractionMapper;
import com.taolife.identity.service.IUserInteractionService;
import com.taolife.identity.vo.UserInteractionStatusVO;
import com.taolife.identity.vo.UserStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户互动服务实现
 *
 * @author 文二
 * @date 2026-04-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInteractionServiceImpl implements IUserInteractionService {

    private final UserInteractionMapper interactionMapper;
    private final UserFollowMapper followMapper;
    private final JdbcTemplate jdbcTemplate;

    /** 目标表名映射 */
    private static final String[] TARGET_TABLES = {
            "", "tf_plan_square", "tf_health_article", "tf_health_food", "tf_health_exercise", "tf_plan_detail_relation"
    };

    /**
     * 切换互动状态
     * 对目标进行点赞/收藏切换，第一次操作为新增，重复操作为取消
     * 操作成功后同步更新目标表的计数
     *
     * @param accountId       账号ID
     * @param targetType      目标类型（1-方案广场 2-文章 3-食物 4-运动 5-方案详情）
     * @param targetId        目标ID
     * @param interactionType 互动类型（1-点赞 2-收藏）
     * @return 当前互动状态（含是否已点赞/已收藏）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInteractionStatusVO toggleInteraction(String accountId, Integer targetType, String targetId, Integer interactionType) {
        UserInteraction record = interactionMapper.selectByUserTarget(accountId, targetType, targetId, interactionType);

        boolean activated;
        if (record == null) {
            record = new UserInteraction();
            record.setAccountId(accountId);
            record.setTargetType(targetType);
            record.setTargetId(targetId);
            record.setInteractionType(interactionType);
            interactionMapper.insert(record);
            activated = true;
        } else if (record.getIsDeleted() == 0) {
            record.setIsDeleted(1);
            record.setUpdateTime(LocalDateTime.now());
            interactionMapper.update(record);
            activated = false;
        } else {
            record.setIsDeleted(0);
            record.setUpdateTime(LocalDateTime.now());
            interactionMapper.update(record);
            activated = true;
        }

        syncTargetCount(targetType, targetId, interactionType, activated);

        log.info("互动切换：accountId={}, targetType={}, targetId={}, type={}, activated={}",
                accountId, targetType, targetId, interactionType, activated);

        return getInteractionStatus(accountId, targetType, targetId);
    }

    /**
     * 获取互动状态
     * 查询当前用户对指定目标的点赞和收藏状态
     *
     * @param accountId   账号ID
     * @param targetType  目标类型
     * @param targetId    目标ID
     * @return 互动状态VO
     */
    @Override
    public UserInteractionStatusVO getInteractionStatus(String accountId, Integer targetType, String targetId) {
        UserInteractionStatusVO vo = new UserInteractionStatusVO();
        UserInteraction likeRecord = interactionMapper.selectByUserTarget(accountId, targetType, targetId, 1);
        UserInteraction collectRecord = interactionMapper.selectByUserTarget(accountId, targetType, targetId, 2);
        vo.setIsLiked(likeRecord != null && likeRecord.getIsDeleted() == 0);
        vo.setIsCollected(collectRecord != null && collectRecord.getIsDeleted() == 0);
        return vo;
    }

    /**
     * 获取互动统计数据
     * 查询当前用户的点赞数、收藏数和关注数
     *
     * @param accountId 账号ID
     * @return 互动统计VO
     */
    @Override
    public UserStatsVO getInteractionStats(String accountId) {
        UserStatsVO vo = new UserStatsVO();
        vo.setLikeCount(interactionMapper.countByAccountAndType(accountId, 1));
        vo.setCollectCount(interactionMapper.countByAccountAndType(accountId, 2));
        vo.setFollowingCount(followMapper.countFollowing(accountId));
        return vo;
    }

    /**
     * 同步目标表计数
     * 通过 JdbcTemplate 直接更新目标表的点赞/收藏计数，避免跨模块 Maven 依赖
     *
     * @param targetType      目标类型
     * @param targetId        目标ID
     * @param interactionType 互动类型
     * @param increment       是否增加（true-增加计数，false-减少计数）
     */
    private void syncTargetCount(Integer targetType, String targetId, Integer interactionType, boolean increment) {
        if (targetType < 1 || targetType >= TARGET_TABLES.length) return;
        String table = TARGET_TABLES[targetType];
        int delta = increment ? 1 : -1;

        if (targetType == 5) {
            // 方案详情：is_collected 字段用 0/1
            if (interactionType == 2) {
                jdbcTemplate.update("UPDATE " + table + " SET is_collected = ? WHERE id = ?",
                        increment ? 1 : 0, hexToBytes(targetId));
            }
            return;
        }

        // 通用计数更新
        if (interactionType == 1) {
            // targetType=3,4 没有 like_count 列，跳过
            if (targetType == 3 || targetType == 4) return;
            jdbcTemplate.update("UPDATE " + table + " SET like_count = GREATEST(like_count + ?, 0) WHERE id = ?",
                    delta, hexToBytes(targetId));
        } else if (interactionType == 2) {
            jdbcTemplate.update("UPDATE " + table + " SET collect_count = GREATEST(collect_count + ?, 0) WHERE id = ?",
                    delta, hexToBytes(targetId));
        }
    }

    /**
     * hex字符串转byte数组
     * 将 binary(16) ID 的十六进制字符串转换为 byte[]
     *
     * @param hex 十六进制字符串
     * @return byte数组
     */
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
