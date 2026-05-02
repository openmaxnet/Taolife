package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.PointsGoods;
import com.taolife.fee.mapper.PointsGoodsMapper;
import com.taolife.fee.param.PointsGoodsPageParam;
import com.taolife.fee.param.PointsGoodsSaveParam;
import com.taolife.fee.service.IPointsGoodsAdminService;
import com.taolife.fee.vo.PointsGoodsAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分商品管理端服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsGoodsAdminServiceImpl implements IPointsGoodsAdminService {

    private final PointsGoodsMapper pointsGoodsMapper;

    /**
     * 分页查询积分商品列表
     *
     * @param param 分页查询参数（含页码、每页条数、商品编码、商品类型过滤条件）
     * @return 积分商品分页结果
     */
    @Override
    public PageResult<PointsGoodsAdminVO> getGoodsPage(PointsGoodsPageParam param) {
        // 调用Mapper的分页查询方法（QueryWrapper封装在Mapper中）
        Page<PointsGoods> page = pointsGoodsMapper.selectGoodsPage(
                param.getPageNo(), param.getPageSize(),
                param.getGoodsCode(), param.getGoodsType());
        // 转换实体为VO
        List<PointsGoodsAdminVO> list = page.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 按 ID 查询积分商品详情
     *
     * @param id 积分商品ID
     * @return 积分商品VO
     */
    @Override
    public PointsGoodsAdminVO getGoods(String id) {
        PointsGoods entity = pointsGoodsMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "积分商品不存在");
        }
        return convertToAdminVO(entity);
    }

    /**
     * 创建积分商品
     *
     * @param param 积分商品保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createGoods(PointsGoodsSaveParam param) {
        // 构建积分商品实体
        PointsGoods entity = new PointsGoods();
        entity.setGoodsCode(param.getGoodsCode());
        entity.setGoodsName(param.getGoodsName());
        entity.setGoodsType(param.getGoodsType());
        entity.setValue(param.getValue());
        entity.setPointsRequired(param.getPointsRequired());
        entity.setDailyLimit(param.getDailyLimit() != null ? param.getDailyLimit() : 0);
        entity.setTotalLimit(param.getTotalLimit() != null ? param.getTotalLimit() : 0);
        entity.setIconUrl(param.getIconUrl());
        entity.setDescription(param.getDescription());
        entity.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        entity.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        // 插入数据库
        pointsGoodsMapper.insert(entity);
        log.info("创建积分商品成功：goodsCode={}", param.getGoodsCode());
    }

    /**
     * 修改积分商品信息
     *
     * @param id    积分商品ID
     * @param param 积分商品保存参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyGoods(String id, PointsGoodsSaveParam param) {
        // 查询积分商品是否存在
        PointsGoods entity = pointsGoodsMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "积分商品不存在");
        }
        // 更新积分商品信息
        entity.setGoodsCode(param.getGoodsCode());
        entity.setGoodsName(param.getGoodsName());
        entity.setGoodsType(param.getGoodsType());
        entity.setValue(param.getValue());
        entity.setPointsRequired(param.getPointsRequired());
        if (param.getDailyLimit() != null) {
            entity.setDailyLimit(param.getDailyLimit());
        }
        if (param.getTotalLimit() != null) {
            entity.setTotalLimit(param.getTotalLimit());
        }
        entity.setIconUrl(param.getIconUrl());
        entity.setDescription(param.getDescription());
        if (param.getSortOrder() != null) {
            entity.setSortOrder(param.getSortOrder());
        }
        if (param.getIsEnabled() != null) {
            entity.setIsEnabled(param.getIsEnabled());
        }
        entity.setUpdateTime(LocalDateTime.now());
        // 更新数据库
        pointsGoodsMapper.update(entity);
        log.info("修改积分商品成功：id={}", id);
    }

    /**
     * 修改积分商品启用状态
     *
     * @param id        积分商品ID
     * @param isEnabled 是否启用：0-否，1-是
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyGoodsStatus(String id, Integer isEnabled) {
        // 查询积分商品是否存在
        PointsGoods entity = pointsGoodsMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "积分商品不存在");
        }
        // 更新启用状态
        entity.setIsEnabled(isEnabled);
        entity.setUpdateTime(LocalDateTime.now());
        pointsGoodsMapper.update(entity);
        log.info("修改积分商品状态成功：id={}, isEnabled={}", id, isEnabled);
    }

    /**
     * 删除积分商品
     *
     * @param id 积分商品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGoods(String id) {
        // 查询积分商品是否存在
        PointsGoods entity = pointsGoodsMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "积分商品不存在");
        }
        // 执行逻辑删除
        pointsGoodsMapper.deleteById(id);
        log.info("删除积分商品成功：id={}", id);
    }

    /**
     * 积分商品实体转换为VO
     *
     * @param entity 积分商品实体
     * @return 积分商品VO
     */
    private PointsGoodsAdminVO convertToAdminVO(PointsGoods entity) {
        if (entity == null) {
            return null;
        }
        PointsGoodsAdminVO vo = new PointsGoodsAdminVO();
        vo.setId(entity.getId());
        vo.setGoodsCode(entity.getGoodsCode());
        vo.setGoodsName(entity.getGoodsName());
        vo.setGoodsType(entity.getGoodsType());
        vo.setValue(entity.getValue());
        vo.setPointsRequired(entity.getPointsRequired());
        vo.setDailyLimit(entity.getDailyLimit());
        vo.setTotalLimit(entity.getTotalLimit());
        vo.setIconUrl(entity.getIconUrl());
        vo.setDescription(entity.getDescription());
        vo.setSortOrder(entity.getSortOrder());
        vo.setIsEnabled(entity.getIsEnabled());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
