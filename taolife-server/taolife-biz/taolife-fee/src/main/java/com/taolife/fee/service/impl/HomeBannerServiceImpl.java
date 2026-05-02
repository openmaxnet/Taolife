package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.properties.CosProperties;
import com.taolife.common.utils.CosStsUtil;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.HomeBanner;
import com.taolife.fee.mapper.HomeBannerMapper;
import com.taolife.fee.service.IHomeBannerService;
import com.taolife.fee.param.BannerPageAdminParam;
import com.taolife.fee.param.BannerSaveAdminParam;
import com.taolife.fee.vo.BannerAdminVO;
import com.taolife.fee.vo.HomeBannerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页轮播Service实现类
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeBannerServiceImpl implements IHomeBannerService {

    private final HomeBannerMapper homeBannerMapper;
    private final CosProperties cosProperties;

    /**
     * 获取启用的轮播列表（用户端）
     *
     * @return 启用的轮播VO列表
     */
    @Override
    public List<HomeBannerVO> getEnabledBanners() {
        List<HomeBanner> banners = homeBannerMapper.selectEnabledBanners();
        return banners.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询轮播列表（管理端）
     *
     * @param param 分页查询参数
     * @return 轮播分页结果
     */
    @Override
    public PageResult<BannerAdminVO> getBannerPage(BannerPageAdminParam param) {
        Page<HomeBanner> page = homeBannerMapper.selectPageByParam(param);
        List<BannerAdminVO> voList = page.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取轮播详情
     *
     * @param id 轮播ID
     * @return 轮播VO
     */
    @Override
    public BannerAdminVO getBannerDetail(String id) {
        HomeBanner banner = homeBannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "轮播不存在");
        }
        return convertToAdminVO(banner);
    }

    /**
     * 创建轮播
     *
     * @param param 创建参数
     */
    @Override
    public void createBanner(BannerSaveAdminParam param) {
        log.info("创建首页轮播，标题：{}", param.getTitle());
        HomeBanner banner = new HomeBanner();
        copyParamToEntity(param, banner);
        homeBannerMapper.insert(banner);
    }

    /**
     * 修改轮播信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    public void modifyBannerInfo(BannerSaveAdminParam param) {
        log.info("修改首页轮播，id：{}", param.getId());
        HomeBanner banner = homeBannerMapper.selectById(param.getId());
        if (banner == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "轮播不存在");
        }
        copyParamToEntity(param, banner);
        homeBannerMapper.update(banner);
    }

    /**
     * 删除轮播（物理删除）
     *
     * @param id 轮播ID
     */
    @Override
    public void removeBanner(String id) {
        log.info("删除首页轮播，id：{}", id);
        HomeBanner banner = homeBannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "轮播不存在");
        }
        homeBannerMapper.deleteById(id);
    }

    /**
     * 修改轮播状态（启用/禁用）
     *
     * @param id     轮播ID
     * @param status 状态（0：禁用，1：启用）
     */
    @Override
    public void modifyBannerStatus(String id, Integer status) {
        log.info("修改首页轮播状态，id：{}，status：{}", id, status);
        HomeBanner banner = homeBannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "轮播不存在");
        }
        banner.setStatus(status);
        homeBannerMapper.update(banner);
    }

    /**
     * 参数复制到实体
     */
    private void copyParamToEntity(BannerSaveAdminParam param, HomeBanner banner) {
        if (param.getId() != null) {
            banner.setId(param.getId());
        }
        banner.setTitle(param.getTitle());
        banner.setSubtitle(param.getSubtitle());
        banner.setImageUrl(param.getImageUrl());
        banner.setLinkType(param.getLinkType());
        banner.setLinkUrl(param.getLinkUrl());
        banner.setSortOrder(param.getSortOrder());
        banner.setStatus(param.getStatus());
    }

    /**
     * 实体转用户端VO
     */
    private HomeBannerVO convertToVO(HomeBanner banner) {
        HomeBannerVO vo = new HomeBannerVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setSubtitle(banner.getSubtitle());
        vo.setImageUrl(CosStsUtil.signCosUrl(cosProperties, banner.getImageUrl()));
        vo.setLinkType(banner.getLinkType());
        vo.setLinkUrl(banner.getLinkUrl());
        return vo;
    }

    /**
     * 实体转管理端VO
     */
    private BannerAdminVO convertToAdminVO(HomeBanner banner) {
        BannerAdminVO vo = new BannerAdminVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setSubtitle(banner.getSubtitle());
        vo.setImageUrl(CosStsUtil.signCosUrl(cosProperties, banner.getImageUrl()));
        vo.setLinkType(banner.getLinkType());
        vo.setLinkUrl(banner.getLinkUrl());
        vo.setSortOrder(banner.getSortOrder());
        vo.setStatus(banner.getStatus());
        vo.setStartDate(banner.getStartDate());
        vo.setEndDate(banner.getEndDate());
        vo.setCreateTime(banner.getCreateTime());
        vo.setUpdateTime(banner.getUpdateTime());
        return vo;
    }
}
