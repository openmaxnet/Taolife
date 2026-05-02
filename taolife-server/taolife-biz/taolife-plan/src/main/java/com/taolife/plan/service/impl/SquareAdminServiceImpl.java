package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanSquare;
import com.taolife.plan.mapper.PlanSquareMapper;
import com.taolife.plan.service.ISquareAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 方案广场管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SquareAdminServiceImpl implements ISquareAdminService {

    private final PlanSquareMapper planSquareMapper;

    /**
     * 分页查询方案广场
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param status   状态（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<PlanSquare> getSquarePage(Integer pageNo, Integer pageSize, Integer status) {
        Page<PlanSquare> page = planSquareMapper.selectAdminPage(new Page<>(pageNo, pageSize), status);
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 修改方案广场状态
     * 审核或管理方案在广场的展示状态
     *
     * @param id     方案ID
     * @param status 状态值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySquareStatus(String id, Integer status) {
        PlanSquare square = planSquareMapper.selectById(id);
        if (square == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "方案广场记录不存在");
        }
        square.setStatus(status);
        square.setUpdateTime(LocalDateTime.now());
        planSquareMapper.update(square);
        log.info("修改方案广场状态：id={}, status={}", id, status);
    }
}
