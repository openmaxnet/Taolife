package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.BannerPageAdminParam;
import com.taolife.fee.param.BannerSaveAdminParam;
import com.taolife.fee.vo.BannerAdminVO;
import com.taolife.fee.vo.HomeBannerVO;

import java.util.List;

/**
 * 首页轮播Service接口
 *
 * @author 文二
 * @date 2026-04-20
 */
public interface IHomeBannerService {

    /**
     * 获取启用的轮播列表（用户端）
     *
     * @return 轮播列表
     */
    List<HomeBannerVO> getEnabledBanners();

    /**
     * 分页获取轮播列表（管理端）
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<BannerAdminVO> getBannerPage(BannerPageAdminParam param);

    /**
     * 获取轮播详情（管理端）
     *
     * @param id 轮播ID
     * @return 轮播详情
     */
    BannerAdminVO getBannerDetail(String id);

    /**
     * 创建轮播
     *
     * @param param 创建参数
     */
    void createBanner(BannerSaveAdminParam param);

    /**
     * 修改轮播信息
     *
     * @param param 修改参数
     */
    void modifyBannerInfo(BannerSaveAdminParam param);

    /**
     * 删除轮播（逻辑删除）
     *
     * @param id 轮播ID
     */
    void removeBanner(String id);

    /**
     * 修改轮播状态
     *
     * @param id     轮播ID
     * @param status 状态（0：禁用，1：启用）
     */
    void modifyBannerStatus(String id, Integer status);
}
