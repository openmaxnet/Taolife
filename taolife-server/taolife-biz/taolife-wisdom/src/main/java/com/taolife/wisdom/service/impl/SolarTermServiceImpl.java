package com.taolife.wisdom.service.impl;

import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.wisdom.entity.SolarTerm;
import com.taolife.wisdom.mapper.SolarTermMapper;
import com.taolife.wisdom.service.ISolarTermService;
import com.taolife.wisdom.vo.SolarTermDetailVO;
import com.taolife.wisdom.vo.SolarTermListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节气Service实现类
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolarTermServiceImpl implements ISolarTermService {

    private final SolarTermMapper solarTermMapper;

    private static final String[] SEASON_NAMES = {"", "春", "夏", "秋", "冬"};

    /**
     * 获取节气列表
     * 遍历所有节气并标记当前节气
     *
     * @return 节气列表VO
     */
    @Override
    public List<SolarTermListVO> getSolarTermList() {
        List<SolarTerm> terms = solarTermMapper.selectAllSolarTerms();
        SolarTerm currentTerm = findCurrentSolarTerm();

        return terms.stream()
                .map(term -> convertToListVO(term, currentTerm))
                .collect(Collectors.toList());
    }

    /**
     * 获取节气详情
     *
     * @param id 节气ID
     * @return 节气详情VO
     */
    @Override
    public SolarTermDetailVO getSolarTermDetail(String id) {
        SolarTerm term = solarTermMapper.selectById(id);
        if (term == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "节气不存在");
        }
        SolarTerm currentTerm = findCurrentSolarTerm();
        return convertToDetailVO(term, currentTerm);
    }

    /**
     * 获取当前节气
     * 根据当前日期自动匹配对应的节气
     *
     * @return 当前节气详情VO
     */
    @Override
    public SolarTermDetailVO getCurrentSolarTerm() {
        SolarTerm term = findCurrentSolarTerm();
        if (term == null) {
            // 如果没找到当前节气，返回第一个
            term = solarTermMapper.selectFirstSolarTerm();
        }
        if (term == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "暂无节气数据");
        }
        return convertToDetailVO(term, term);
    }

    /**
     * 查找当前日期对应的节气
     */
    private SolarTerm findCurrentSolarTerm() {
        LocalDate now = LocalDate.now();
        return solarTermMapper.selectCurrentSolarTerm(now);
    }

    /**
     * 实体转列表VO
     */
    private SolarTermListVO convertToListVO(SolarTerm term, SolarTerm currentTerm) {
        SolarTermListVO vo = new SolarTermListVO();
        vo.setId(term.getId());
        vo.setTermName(term.getTermName());
        vo.setTermOrder(term.getTermOrder());
        vo.setDateRange(formatDateRange(term));
        vo.setSeason(term.getSeason());
        vo.setSeasonName(getSeasonName(term.getSeason()));
        vo.setCoverUrl(term.getCoverUrl());
        vo.setDescription(term.getDescription());
        vo.setIsCurrent(currentTerm != null && currentTerm.getId().equals(term.getId()));
        return vo;
    }

    /**
     * 实体转详情VO
     */
    private SolarTermDetailVO convertToDetailVO(SolarTerm term, SolarTerm currentTerm) {
        SolarTermDetailVO vo = new SolarTermDetailVO();
        vo.setId(term.getId());
        vo.setTermName(term.getTermName());
        vo.setTermOrder(term.getTermOrder());
        vo.setDateRange(formatDateRange(term));
        vo.setSeason(term.getSeason());
        vo.setSeasonName(getSeasonName(term.getSeason()));
        vo.setBackgroundUrl(term.getBackgroundUrl());
        vo.setDescription(term.getDescription());
        vo.setIntroduction(term.getIntroduction());
        vo.setClimate(term.getClimate());
        vo.setHealthPrinciples(term.getHealthPrinciples());
        vo.setCustoms(term.getCustoms());
        vo.setProverbs(term.getProverbs());
        vo.setDietSummary(term.getDietSummary());
        vo.setIsCurrent(currentTerm != null && currentTerm.getId().equals(term.getId()));
        return vo;
    }

    /**
     * 格式化日期范围
     */
    private String formatDateRange(SolarTerm term) {
        return term.getStartMonth() + "月" + term.getStartDay() + "日 - "
                + term.getEndMonth() + "月" + term.getEndDay() + "日";
    }

    /**
     * 获取季节名称
     */
    private String getSeasonName(Integer season) {
        if (season == null || season < 1 || season > 4) {
            return "";
        }
        return SEASON_NAMES[season];
    }
}
