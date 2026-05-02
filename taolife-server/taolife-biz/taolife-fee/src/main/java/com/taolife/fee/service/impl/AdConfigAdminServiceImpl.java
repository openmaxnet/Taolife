package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.AdConfig;
import com.taolife.fee.mapper.AdConfigMapper;
import com.taolife.fee.param.AdConfigPageParam;
import com.taolife.fee.param.AdConfigSaveParam;
import com.taolife.fee.service.IAdConfigAdminService;
import com.taolife.fee.vo.AdConfigAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告配置管理端服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdConfigAdminServiceImpl implements IAdConfigAdminService {

    private final AdConfigMapper adConfigMapper;

    /**
     * 分页查询广告配置列表
     *
     * @param param 分页查询参数（含页码、每页条数、配置键、广告类型过滤条件）
     * @return 广告配置分页结果
     */
    @Override
    public PageResult<AdConfigAdminVO> getAdConfigPage(AdConfigPageParam param) {
        // 调用Mapper的分页查询方法（QueryWrapper封装在Mapper中）
        Page<AdConfig> page = adConfigMapper.selectAdConfigPage(
                param.getPageNo(), param.getPageSize(),
                param.getConfigKey(), param.getAdType());
        // 转换实体为VO
        List<AdConfigAdminVO> list = page.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 按 ID 查询广告配置详情
     *
     * @param id 广告配置ID
     * @return 广告配置VO
     */
    @Override
    public AdConfigAdminVO getAdConfig(String id) {
        AdConfig entity = adConfigMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "广告配置不存在");
        }
        return convertToAdminVO(entity);
    }

    /**
     * 创建广告配置
     *
     * @param param 广告配置保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAdConfig(AdConfigSaveParam param) {
        // 构建广告配置实体
        AdConfig entity = new AdConfig();
        entity.setConfigKey(param.getConfigKey());
        entity.setAdType(param.getAdType());
        entity.setAdUnitId(param.getAdUnitId());
        entity.setPlacement(param.getPlacement());
        entity.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        entity.setFreeUserOnly(param.getFreeUserOnly() != null ? param.getFreeUserOnly() : 1);
        entity.setDisplayIntervalSeconds(param.getDisplayIntervalSeconds() != null ? param.getDisplayIntervalSeconds() : 0);
        entity.setExtraConfig(param.getExtraConfig());
        entity.setPriority(param.getPriority() != null ? param.getPriority() : 0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        // 插入数据库
        adConfigMapper.insert(entity);
        log.info("创建广告配置成功：configKey={}", param.getConfigKey());
    }

    /**
     * 修改广告配置
     *
     * @param id    广告配置ID
     * @param param 广告配置保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAdConfig(String id, AdConfigSaveParam param) {
        // 查询广告配置是否存在
        AdConfig entity = adConfigMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "广告配置不存在");
        }
        // 更新广告配置信息
        entity.setConfigKey(param.getConfigKey());
        entity.setAdType(param.getAdType());
        entity.setAdUnitId(param.getAdUnitId());
        entity.setPlacement(param.getPlacement());
        if (param.getIsEnabled() != null) {
            entity.setIsEnabled(param.getIsEnabled());
        }
        if (param.getFreeUserOnly() != null) {
            entity.setFreeUserOnly(param.getFreeUserOnly());
        }
        if (param.getDisplayIntervalSeconds() != null) {
            entity.setDisplayIntervalSeconds(param.getDisplayIntervalSeconds());
        }
        entity.setExtraConfig(param.getExtraConfig());
        if (param.getPriority() != null) {
            entity.setPriority(param.getPriority());
        }
        entity.setUpdateTime(LocalDateTime.now());
        // 更新数据库
        adConfigMapper.update(entity);
        log.info("修改广告配置成功：id={}", id);
    }

    /**
     * 修改广告配置启用状态
     *
     * @param id        广告配置ID
     * @param isEnabled 是否启用：0-否，1-是
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAdConfigStatus(String id, Integer isEnabled) {
        // 查询广告配置是否存在
        AdConfig entity = adConfigMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "广告配置不存在");
        }
        // 更新启用状态
        entity.setIsEnabled(isEnabled);
        entity.setUpdateTime(LocalDateTime.now());
        adConfigMapper.update(entity);
        log.info("修改广告配置状态成功：id={}, isEnabled={}", id, isEnabled);
    }

    /**
     * 删除广告配置
     *
     * @param id 广告配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAdConfig(String id) {
        // 查询广告配置是否存在
        AdConfig entity = adConfigMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "广告配置不存在");
        }
        // 执行逻辑删除
        adConfigMapper.deleteById(id);
        log.info("删除广告配置成功：id={}", id);
    }

    /**
     * 广告配置实体转换为VO
     *
     * @param entity 广告配置实体
     * @return 广告配置VO
     */
    private AdConfigAdminVO convertToAdminVO(AdConfig entity) {
        if (entity == null) {
            return null;
        }
        AdConfigAdminVO vo = new AdConfigAdminVO();
        vo.setId(entity.getId());
        vo.setConfigKey(entity.getConfigKey());
        vo.setAdType(entity.getAdType());
        vo.setAdUnitId(entity.getAdUnitId());
        vo.setPlacement(entity.getPlacement());
        vo.setIsEnabled(entity.getIsEnabled());
        vo.setFreeUserOnly(entity.getFreeUserOnly());
        vo.setDisplayIntervalSeconds(entity.getDisplayIntervalSeconds());
        vo.setExtraConfig(entity.getExtraConfig());
        vo.setPriority(entity.getPriority());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
