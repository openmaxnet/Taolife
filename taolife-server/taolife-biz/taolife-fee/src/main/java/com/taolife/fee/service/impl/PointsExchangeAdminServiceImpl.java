package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.PointsExchangeRecord;
import com.taolife.fee.mapper.PointsExchangeRecordMapper;
import com.taolife.fee.param.PointsExchangePageParam;
import com.taolife.fee.service.IPointsExchangeAdminService;
import com.taolife.fee.vo.PointsExchangeAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分兑换记录管理端服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsExchangeAdminServiceImpl implements IPointsExchangeAdminService {

    private final PointsExchangeRecordMapper pointsExchangeRecordMapper;

    /**
     * 分页查询积分兑换记录（管理员）
     *
     * @param param 分页查询参数（含页码、每页条数、用户账号ID、状态过滤条件）
     * @return 兑换记录分页结果
     */
    @Override
    public PageResult<PointsExchangeAdminVO> getExchangeRecordPage(PointsExchangePageParam param) {
        // 调用Mapper的分页查询方法（QueryWrapper封装在Mapper中）
        Page<PointsExchangeRecord> page = pointsExchangeRecordMapper.selectExchangeRecordPage(
                param.getPageNo(), param.getPageSize(),
                param.getAccountId(), param.getStatus());
        // 转换实体为VO
        List<PointsExchangeAdminVO> list = page.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 积分兑换记录实体转换为VO
     *
     * @param entity 积分兑换记录实体
     * @return 积分兑换记录VO
     */
    private PointsExchangeAdminVO convertToAdminVO(PointsExchangeRecord entity) {
        if (entity == null) {
            return null;
        }
        PointsExchangeAdminVO vo = new PointsExchangeAdminVO();
        vo.setId(entity.getId());
        vo.setAccountId(entity.getAccountId());
        vo.setGoodsId(entity.getGoodsId());
        vo.setPointsCost(entity.getPointsCost());
        vo.setExchangeValue(entity.getExchangeValue());
        vo.setBusinessId(entity.getBusinessId());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
