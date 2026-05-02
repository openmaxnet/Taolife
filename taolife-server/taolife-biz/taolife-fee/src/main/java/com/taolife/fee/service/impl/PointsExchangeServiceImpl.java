package com.taolife.fee.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.fee.enums.PointsGoodsTypeEnum;
import com.taolife.fee.entity.PointsExchangeRecord;
import com.taolife.fee.entity.PointsGoods;
import com.taolife.fee.mapper.PointsExchangeRecordMapper;
import com.taolife.fee.mapper.PointsGoodsMapper;
import com.taolife.fee.service.IPointsExchangeService;
import com.taolife.fee.vo.PointsExchangeVO;
import com.taolife.fee.vo.PointsGoodsVO;
import com.taolife.fee.vo.PointsSummaryVO;
import com.taolife.fee.service.IQuotaService;
import com.taolife.fee.service.IPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分兑换服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsExchangeServiceImpl implements IPointsExchangeService {

    private final PointsGoodsMapper pointsGoodsMapper;
    private final PointsExchangeRecordMapper pointsExchangeRecordMapper;
    private final IPointsService pointsService;
    private final IQuotaService quotaService;

    /**
     * 获取所有已启用的积分商品列表
     *
     * @return 积分商品VO列表
     */
    @Override
    public List<PointsGoodsVO> getGoodsList() {
        return pointsGoodsMapper.selectEnabled().stream()
                .map(this::convertToGoodsVO)
                .collect(Collectors.toList());
    }

    /**
     * 积分兑换商品
     * 校验商品可用性、限购次数、积分余额，扣减积分后发放权益并生成兑换记录
     *
     * @param accountId 用户账号ID
     * @param goodsCode 商品编码
     * @return 兑换结果VO
     */
    @Override
    @Transactional
    public PointsExchangeVO exchange(String accountId, String goodsCode) {
        PointsGoods goods = pointsGoodsMapper.selectOneByQuery(
                QueryWrapper.create().where(PointsGoods::getGoodsCode).eq(goodsCode)
        );
        if (goods == null || goods.getIsEnabled() != 1) {
            throw new BusinessException(ExceptionCode.POINTS_GOODS_NOT_FOUND);
        }

        // 检查总限购
        if (goods.getTotalLimit() != null && goods.getTotalLimit() > 0) {
            long totalCount = pointsExchangeRecordMapper.countByAccountIdAndGoodsId(accountId, goods.getId());
            if (totalCount >= goods.getTotalLimit()) {
                throw new BusinessException(ExceptionCode.POINTS_EXCHANGE_LIMIT, "已达到该商品总限购次数");
            }
        }

        if (goods.getDailyLimit() != null && goods.getDailyLimit() > 0) {
            long todayCount = pointsExchangeRecordMapper.countTodayByAccountIdAndGoodsId(accountId, goods.getId());
            if (todayCount >= goods.getDailyLimit()) {
                throw new BusinessException(ExceptionCode.POINTS_EXCHANGE_LIMIT);
            }
        }

        int balance = pointsService.getAvailablePoints(accountId);
        if (balance < goods.getPointsRequired()) {
            throw new BusinessException(ExceptionCode.POINTS_INSUFFICIENT);
        }

        pointsService.deductPoints(accountId, goods.getPointsRequired(),
                "points_exchange", null, "兑换: " + goods.getGoodsName());

        applyBenefit(accountId, goods);

        PointsExchangeRecord record = new PointsExchangeRecord();
        record.setAccountId(accountId);
        record.setGoodsId(goods.getId());
        record.setPointsCost(goods.getPointsRequired());
        record.setExchangeValue(goods.getValue());
        record.setStatus(1);
        record.setCreateTime(LocalDateTime.now());
        pointsExchangeRecordMapper.insert(record);

        PointsExchangeVO vo = new PointsExchangeVO();
        vo.setId(record.getId());
        vo.setGoodsCode(goodsCode);
        vo.setGoodsName(goods.getGoodsName());
        vo.setGoodsType(goods.getGoodsType());
        vo.setPointsCost(goods.getPointsRequired());
        vo.setExchangeValue(goods.getValue());
        vo.setStatus(1);
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    /**
     * 查询当前用户的兑换记录（仅返回已成功的记录）
     *
     * @param accountId 用户账号ID
     * @return 兑换记录列表
     */
    @Override
    public List<PointsExchangeVO> getExchangeRecords(String accountId) {
        List<PointsExchangeRecord> records = pointsExchangeRecordMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(PointsExchangeRecord::getAccountId).eq(accountId)
                        .and(PointsExchangeRecord::getStatus).eq(1)
                        .orderBy(PointsExchangeRecord::getCreateTime, false)
        );
        return records.stream().map(r -> {
            PointsGoods goods = pointsGoodsMapper.selectOneById(r.getGoodsId());
            PointsExchangeVO vo = new PointsExchangeVO();
            vo.setId(r.getId());
            vo.setGoodsCode(goods != null ? goods.getGoodsCode() : null);
            vo.setGoodsName(goods != null ? goods.getGoodsName() : null);
            vo.setGoodsType(goods != null ? goods.getGoodsType() : null);
            vo.setPointsCost(r.getPointsCost());
            vo.setExchangeValue(r.getExchangeValue());
            vo.setStatus(r.getStatus());
            vo.setCreateTime(r.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询当前用户的积分汇总信息
     *
     * @param accountId 用户账号ID
     * @return 积分汇总信息（可用积分、今日获取、用户等级）
     */
    @Override
    public PointsSummaryVO getPointsSummary(String accountId) {
        PointsSummaryVO vo = new PointsSummaryVO();
        vo.setAvailablePoints(pointsService.getAvailablePoints(accountId));
        vo.setTodayEarned(pointsService.getTodayPoints(accountId, LocalDate.now()));
        vo.setUserLevel(pointsService.getUserLevel(accountId));
        return vo;
    }

    private void applyBenefit(String accountId, PointsGoods goods) {
        if (PointsGoodsTypeEnum.AI_QUOTA.getValue().equals(goods.getGoodsType())) {
            quotaService.incrementAiQuota(accountId, goods.getValue());
        } else if (PointsGoodsTypeEnum.ASSESSMENT.getValue().equals(goods.getGoodsType())) {
            quotaService.incrementAssessmentQuota(accountId, goods.getValue());
        } else {
            log.info("商品类型 {} 暂不需要权益执行", goods.getGoodsType());
        }
    }

    private PointsGoodsVO convertToGoodsVO(PointsGoods goods) {
        PointsGoodsVO vo = new PointsGoodsVO();
        vo.setId(goods.getId());
        vo.setGoodsCode(goods.getGoodsCode());
        vo.setGoodsName(goods.getGoodsName());
        vo.setGoodsType(goods.getGoodsType());
        vo.setValue(goods.getValue());
        vo.setPointsRequired(goods.getPointsRequired());
        vo.setDailyLimit(goods.getDailyLimit());
        vo.setIconUrl(goods.getIconUrl());
        vo.setDescription(goods.getDescription());
        return vo;
    }
}
