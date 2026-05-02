package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.wisdom.entity.SolarTerm;
import com.taolife.wisdom.mapper.SolarTermMapper;
import com.taolife.wisdom.param.*;
import com.taolife.wisdom.service.ISolarTermAdminService;
import com.taolife.wisdom.vo.SolarTermAdminVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员节气服务实现
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolarTermAdminServiceImpl implements ISolarTermAdminService {

    private final SolarTermMapper solarTermMapper;

    /**
     * 分页查询节气列表
     *
     * @param param 分页查询参数
     * @return 节气分页结果
     */
    @Override
    public PageResult<SolarTermAdminVO> getSolarTermPage(SolarTermPageAdminParam param) {
        Page<SolarTerm> page = solarTermMapper.selectPageByParam(param);

        PageResult<SolarTerm> pageResult = PageResult.of(page);
        List<SolarTermAdminVO> list = pageResult.getList().stream()
                .map(this::convertToVO)
                .toList();

        PageResult<SolarTermAdminVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取节气详情
     *
     * @param param 详情查询参数
     * @return 节气详情VO
     */
    @Override
    public SolarTermAdminVO getSolarTermDetail(SolarTermDetailAdminParam param) {
        SolarTerm term = solarTermMapper.selectById(param.getId());
        if (term == null || term.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "节气不存在");
        }
        return convertToVO(term);
    }

    /**
     * 创建节气
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSolarTerm(SolarTermSaveAdminParam param) {
        SolarTerm term = new SolarTerm();
        copyParamToEntity(param, term);
        term.setIsDeleted(0);
        term.setCreateTime(LocalDateTime.now());
        term.setUpdateTime(LocalDateTime.now());
        solarTermMapper.insert(term);
    }

    /**
     * 修改节气信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySolarTermInfo(SolarTermSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        SolarTerm term = solarTermMapper.selectById(param.getId());
        if (term == null || term.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "节气不存在");
        }
        copyParamToEntity(param, term);
        term.setUpdateTime(LocalDateTime.now());
        solarTermMapper.update(term);
    }

    /**
     * 删除节气（逻辑删除）
     *
     * @param param 删除参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSolarTerm(RemoveSolarTermAdminParam param) {
        SolarTerm term = solarTermMapper.selectById(param.getId());
        if (term == null || term.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "节气不存在");
        }
        term.setIsDeleted(1);
        term.setUpdateTime(LocalDateTime.now());
        solarTermMapper.update(term);
    }

    /**
     * 参数复制到实体
     */
    private void copyParamToEntity(SolarTermSaveAdminParam param, SolarTerm term) {
        if (param.getId() != null) {
            term.setId(param.getId());
        }
        term.setTermName(param.getTermName());
        term.setTermOrder(param.getTermOrder());
        term.setStartMonth(param.getStartMonth());
        term.setStartDay(param.getStartDay());
        term.setEndMonth(param.getEndMonth());
        term.setEndDay(param.getEndDay());
        term.setDescription(param.getDescription());
        term.setSeason(param.getSeason());
        term.setCoverUrl(param.getCoverUrl());
        term.setBackgroundUrl(param.getBackgroundUrl());
        term.setIntroduction(param.getIntroduction());
        term.setClimate(param.getClimate());
        term.setHealthPrinciples(param.getHealthPrinciples());
        term.setCustoms(param.getCustoms());
        term.setProverbs(param.getProverbs());
        term.setDietSummary(param.getDietSummary());
    }

    /**
     * 实体转VO
     */
    private SolarTermAdminVO convertToVO(SolarTerm term) {
        SolarTermAdminVO vo = new SolarTermAdminVO();
        vo.setId(term.getId());
        vo.setTermName(term.getTermName());
        vo.setTermOrder(term.getTermOrder());
        vo.setStartMonth(term.getStartMonth());
        vo.setStartDay(term.getStartDay());
        vo.setEndMonth(term.getEndMonth());
        vo.setEndDay(term.getEndDay());
        vo.setDescription(term.getDescription());
        vo.setSeason(term.getSeason());
        vo.setCoverUrl(term.getCoverUrl());
        vo.setBackgroundUrl(term.getBackgroundUrl());
        vo.setIntroduction(term.getIntroduction());
        vo.setClimate(term.getClimate());
        vo.setHealthPrinciples(term.getHealthPrinciples());
        vo.setCustoms(term.getCustoms());
        vo.setProverbs(term.getProverbs());
        vo.setDietSummary(term.getDietSummary());
        vo.setCreateTime(term.getCreateTime());
        vo.setUpdateTime(term.getUpdateTime());
        return vo;
    }
}
