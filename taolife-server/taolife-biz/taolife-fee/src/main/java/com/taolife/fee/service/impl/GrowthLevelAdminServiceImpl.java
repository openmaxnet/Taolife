package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.MemberGrowthLevel;
import com.taolife.fee.mapper.MemberGrowthLevelMapper;
import com.taolife.fee.param.GrowthLevelPageParam;
import com.taolife.fee.param.GrowthLevelSaveParam;
import com.taolife.fee.service.IGrowthLevelAdminService;
import com.taolife.fee.vo.GrowthLevelAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成长等级管理服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrowthLevelAdminServiceImpl implements IGrowthLevelAdminService {

    private final MemberGrowthLevelMapper memberGrowthLevelMapper;

    /**
     * 分页查询成长等级列表
     *
     * @param param 分页查询参数（含页码、每页条数）
     * @return 成长等级分页结果
     */
    @Override
    public PageResult<GrowthLevelAdminVO> getGrowthLevelPage(GrowthLevelPageParam param) {
        Page<MemberGrowthLevel> page = memberGrowthLevelMapper.selectAdminPage(
                new Page<>(param.getPageNo(), param.getPageSize()));
        List<GrowthLevelAdminVO> list = page.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 按 ID 查询成长等级详情
     *
     * @param id 成长等级ID
     * @return 成长等级VO
     */
    @Override
    public GrowthLevelAdminVO getGrowthLevelDetail(String id) {
        MemberGrowthLevel entity = memberGrowthLevelMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "成长等级不存在");
        }
        return convertToAdminVO(entity);
    }

    /**
     * 创建成长等级
     *
     * @param param 成长等级保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createGrowthLevel(GrowthLevelSaveParam param) {
        MemberGrowthLevel entity = new MemberGrowthLevel();
        entity.setLevel(param.getLevel());
        entity.setLevelName(param.getLevelName());
        entity.setMinGrowthValue(param.getMinGrowthValue());
        entity.setBonusAiQuota(param.getBonusAiQuota());
        entity.setBonusPointsMultiplier(param.getBonusPointsMultiplier());
        entity.setBonusStoreDiscount(param.getBonusStoreDiscount());
        entity.setPrivilege(param.getPrivilege());
        entity.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        entity.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        memberGrowthLevelMapper.insert(entity);
        log.info("创建成长等级成功：level={}, levelName={}", param.getLevel(), param.getLevelName());
    }

    /**
     * 修改成长等级信息
     *
     * @param id    成长等级ID
     * @param param 成长等级保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyGrowthLevelInfo(String id, GrowthLevelSaveParam param) {
        MemberGrowthLevel entity = memberGrowthLevelMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "成长等级不存在");
        }
        entity.setLevel(param.getLevel());
        entity.setLevelName(param.getLevelName());
        entity.setMinGrowthValue(param.getMinGrowthValue());
        entity.setBonusAiQuota(param.getBonusAiQuota());
        entity.setBonusPointsMultiplier(param.getBonusPointsMultiplier());
        entity.setBonusStoreDiscount(param.getBonusStoreDiscount());
        entity.setPrivilege(param.getPrivilege());
        entity.setIsEnabled(param.getIsEnabled());
        entity.setSortOrder(param.getSortOrder());
        entity.setUpdateTime(LocalDateTime.now());
        memberGrowthLevelMapper.update(entity);
        log.info("修改成长等级成功：id={}, level={}", id, param.getLevel());
    }

    /**
     * 修改成长等级启用状态
     *
     * @param id        成长等级ID
     * @param isEnabled 是否启用：0-否，1-是
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyGrowthLevelStatus(String id, Integer isEnabled) {
        MemberGrowthLevel entity = memberGrowthLevelMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "成长等级不存在");
        }
        entity.setIsEnabled(isEnabled);
        entity.setUpdateTime(LocalDateTime.now());
        memberGrowthLevelMapper.update(entity);
        log.info("修改成长等级状态：id={}, isEnabled={}", id, isEnabled);
    }

    /**
     * 删除成长等级
     *
     * @param id 成长等级ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGrowthLevel(String id) {
        MemberGrowthLevel entity = memberGrowthLevelMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "成长等级不存在");
        }
        memberGrowthLevelMapper.deleteById(id);
        log.info("删除成长等级：id={}", id);
    }

    private GrowthLevelAdminVO convertToAdminVO(MemberGrowthLevel entity) {
        GrowthLevelAdminVO vo = new GrowthLevelAdminVO();
        vo.setId(entity.getId());
        vo.setLevel(entity.getLevel());
        vo.setLevelName(entity.getLevelName());
        vo.setMinGrowthValue(entity.getMinGrowthValue());
        vo.setBonusAiQuota(entity.getBonusAiQuota());
        vo.setBonusPointsMultiplier(entity.getBonusPointsMultiplier());
        vo.setBonusStoreDiscount(entity.getBonusStoreDiscount());
        vo.setPrivilege(entity.getPrivilege());
        vo.setIsEnabled(entity.getIsEnabled());
        vo.setSortOrder(entity.getSortOrder());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
