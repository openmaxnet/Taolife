package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.PointsRecord;
import com.taolife.fee.mapper.PointsRecordMapper;
import com.taolife.fee.service.IPointsAdminService;
import com.taolife.fee.vo.PointsRecordAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 积分管理服务实现（管理员）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsAdminServiceImpl implements IPointsAdminService {

    private final PointsRecordMapper pointsRecordMapper;

    /**
     * 分页查询积分变动记录（管理员）
     *
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param accountId 用户账号ID（可选过滤条件）
     * @return 积分记录分页结果
     */
    @Override
    public PageResult<PointsRecordAdminVO> getPointsRecordPage(Integer pageNo, Integer pageSize, String accountId) {
        Page<PointsRecord> page = pointsRecordMapper.selectAdminPage(new Page<>(pageNo, pageSize), accountId);
        List<PointsRecordAdminVO> voList = page.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(voList, pageNo, pageSize, page.getTotalRow());
    }

    private PointsRecordAdminVO toVO(PointsRecord record) {
        PointsRecordAdminVO vo = new PointsRecordAdminVO();
        vo.setId(record.getId());
        vo.setAccountId(record.getAccountId());
        vo.setPointsChange(record.getPointsChange());
        vo.setPointsType(record.getPointsType());
        vo.setBusinessType(record.getBusinessType());
        vo.setBusinessId(record.getBusinessId());
        vo.setRemark(record.getRemark());
        vo.setBalanceAfter(record.getBalanceAfter());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
