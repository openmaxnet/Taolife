package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.AdDisplayLog;
import com.taolife.fee.mapper.AdDisplayLogMapper;
import com.taolife.fee.param.AdLogPageParam;
import com.taolife.fee.service.IAdDisplayLogAdminService;
import com.taolife.fee.vo.AdLogAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告展示日志管理端服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdDisplayLogAdminServiceImpl implements IAdDisplayLogAdminService {

    private final AdDisplayLogMapper adDisplayLogMapper;

    /**
     * 分页查询广告展示日志
     *
     * @param param 分页查询参数（含页码、每页条数、用户ID、广告类型、动作过滤条件）
     * @return 广告日志分页结果
     */
    @Override
    public PageResult<AdLogAdminVO> getAdLogPage(AdLogPageParam param) {
        // 调用Mapper的分页查询方法（QueryWrapper封装在Mapper中）
        Page<AdDisplayLog> page = adDisplayLogMapper.selectAdLogPage(
                param.getPageNo(), param.getPageSize(),
                param.getAccountId(), param.getAdType(), param.getAction());
        // 转换实体为VO
        List<AdLogAdminVO> list = page.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 广告日志实体转换为VO
     *
     * @param entity 广告日志实体
     * @return 广告日志VO
     */
    private AdLogAdminVO convertToAdminVO(AdDisplayLog entity) {
        if (entity == null) {
            return null;
        }
        AdLogAdminVO vo = new AdLogAdminVO();
        vo.setId(entity.getId());
        vo.setAccountId(entity.getAccountId());
        vo.setAdConfigId(entity.getAdConfigId());
        vo.setAdType(entity.getAdType());
        vo.setAction(entity.getAction());
        vo.setDuration(entity.getDuration());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
