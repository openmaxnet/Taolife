package com.taolife.identity.service;

import com.taolife.common.utils.PageResult;
import com.taolife.identity.entity.SysAgreement;
import com.taolife.identity.param.SysAgreementSaveParam;
import com.taolife.identity.vo.SysAgreementAdminVO;

/**
 * 系统协议管理服务接口
 *
 * @author 文二
 * @date 2026-04-28
 */
public interface ISysAgreementAdminService {

    /**
     * 分页查询协议列表
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    PageResult<SysAgreementAdminVO> getAgreementPage(int pageNo, int pageSize, String keyword);

    /**
     * 获取协议详情
     *
     * @param id 协议ID
     * @return 协议实体
     */
    SysAgreement getAgreementDetail(String id);

    /**
     * 创建协议
     *
     * @param param 协议创建参数
     */
    void createAgreement(SysAgreementSaveParam param);

    /**
     * 修改协议信息
     *
     * @param id    协议ID
     * @param param 协议修改参数
     */
    void modifyAgreementInfo(String id, SysAgreementSaveParam param);

    /**
     * 删除协议
     *
     * @param id 协议ID
     */
    void removeAgreement(String id);
}
